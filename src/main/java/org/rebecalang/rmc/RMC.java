/*                  In the name of Allah                */
/*                   The best will come                 */

package org.rebecalang.rmc;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.rmc.timedrebeca.TimedRebecaFileGeneratorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RMC {

	@Autowired
	ModelCheckersFilesGenerator modelCheckersFilesGenerator;
	
	@Autowired 
	ExceptionContainer exceptionContainer;
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		
        ApplicationContext context = new AnnotationConfigApplicationContext(CompilerConfig.class, RMCConfig.class);
        RMC rmc = context.getBean(RMC.class);
		
		CommandLineParser cmdLineParser = new GnuParser();
		Options options = new Options();
		PrintStream out = System.out;

		try {
			Option option = OptionBuilder.withArgName("file")
                    .hasArg()
                    .withDescription("Generated C++ source files location. Default location is \'./rmc-output\' folder.")
                    .withLongOpt("output").create('o');
			options.addOption(option);

			option = OptionBuilder.withArgName("file")
                    .hasArg()
                    .withDescription("Rebeca model source file.")
                    .withLongOpt("source").create('s');
			option.setRequired(true);
			options.addOption(option);
			
			option = OptionBuilder.withArgName("file")
                    .hasArg()
                    .withDescription("Rebeca model property file.")
                    .withLongOpt("property").create('p');
			options.addOption(option);
			
			option = OptionBuilder.withArgName("value")
                    .hasArg()
                    .withDescription("Rebeca compiler version (2.0, 2.1, 2.3). Default version is 2.1")
                    .withLongOpt("version").create('v');
			options.addOption(option);
			
			option = OptionBuilder.withArgName("value")
                    .hasArg()
                    .withDescription("Rebeca model extension (CORE_REBECA/TIMED_REBECA/PROBABILISTIC_REBECA/" +
                    		"PROBABILISTIC_TIME_REBECA). Default is \'CORE_REBECA\'.")
                    .withLongOpt("extension").create('e');
			options.addOption(option);
			
			option = OptionBuilder.withArgName("file")
	                .hasOptionalArg()
	                .withDescription("Exports transition system in an XML file.")
	                .withLongOpt("exporttransitionsystem").create("x");
			options.addOption(option);

			options.addOption(new Option(null, "nosafemode", false, "Disable checking for array index out of bound and access to NULL objects during the model checking."));
			
			options.addOption(new Option(null, "debug", false, "Enables debug mode in result C++ files."));
			options.addOption(new Option(null, "debug2", false, "Enables debug level 2 mode in result C++ files."));
			options.addOption(new Option("h", "help", false, "Print this message."));
			options.addOption(new Option(null, "progressreport", false, "Enables progress report during analysis."));
			
			
			options.addOption(new Option(null, "tts", false, "Using TTS semantics of Rebeca instead of FTTS."));
			options.addOption(new Option(null, "tracegenerator", false, "Instead of model checking, some traces of the model is generated."));
			options.addOption(new Option(null, "compactdtg", false, "Using this feature, compact DTG is generated on-the-fly from TTS."));

			options.addOption(
					OptionBuilder.withArgName("file")
	                .hasArg()
	                .withDescription("Rebeca model property file.")
	                .withLongOpt("property").create('p')
					);

			
			CommandLine commandLine = cmdLineParser.parse(options, args);

			if (commandLine.hasOption("help"))
				throw new ParseException("");

			File rebecaFile = new File(commandLine.getOptionValue("source"));
			if (!rebecaFile.exists() || rebecaFile.isDirectory())
				throw new ParseException("No such file: \'" + commandLine.getOptionValue("source") + "'");

			File propertyFile = null;
			if (commandLine.hasOption("property")) {
				propertyFile = new File(commandLine.getOptionValue("property"));
				if (!propertyFile.exists() || propertyFile.isDirectory())
					throw new ParseException("No such file: \'" + commandLine.getOptionValue("property") + "'");
			}

			// Set output location. Default location is rmc-output folder.
			File destination;
			if (commandLine.hasOption("output")) {
				destination = new File(commandLine.getOptionValue("output"));
			} else {
				destination = new File("rmc-output");
			}
			
			FileGeneratorProperties fileGenerationProperties = null;
			Set<CompilerExtension> compilerFeatures = new HashSet<CompilerExtension>();
			switch(commandLine.getOptionValue("extension")) {
			case "PROBABILISTIC_TIMED_REBECA":
				compilerFeatures.add(CompilerExtension.PROBABILISTIC_REBECA);
			case "TIMED_REBECA":
				compilerFeatures.add(CompilerExtension.TIMED_REBECA);
				TimedRebecaFileGeneratorProperties timedRebecaFileGeneratorProperties = new TimedRebecaFileGeneratorProperties();
				if (commandLine.hasOption("tracegenerator"))
					timedRebecaFileGeneratorProperties.setTraceGenerator(true);
				if(commandLine.hasOption("tts"))
					timedRebecaFileGeneratorProperties.setTTS(true);
				if(commandLine.hasOption("compactdtg"))
					timedRebecaFileGeneratorProperties.setCompactDTG(true);
				fileGenerationProperties = timedRebecaFileGeneratorProperties;
				break;
			case "PROBABILISTIC_REBECA":
				compilerFeatures.add(CompilerExtension.PROBABILISTIC_REBECA);
			case "CORE_REBECA":
				fileGenerationProperties = new FileGeneratorProperties();
				break;
			default:
				throw new ParseException("Unrecognized Rebeca extension: " + commandLine.getOptionValue("extension"));
			}
			
			fileGenerationProperties.setCoreVersion(CoreVersion.CORE_2_1);
			if (commandLine.hasOption("version")) {
				switch(commandLine.getOptionValue("version")) {
				case "2.0":
					fileGenerationProperties.setCoreVersion(CoreVersion.CORE_2_0);
					break;
				case "2.1":
					fileGenerationProperties.setCoreVersion(CoreVersion.CORE_2_1);
					break;
				case "2.3":
					fileGenerationProperties.setCoreVersion(CoreVersion.CORE_2_3);
					break;
				default:
					throw new ParseException("Unrecognized Rebeca version: " + commandLine.hasOption("version"));
				}
			}
		

			
			fileGenerationProperties.setSafeMode(!commandLine.hasOption("nosafemode"));
			fileGenerationProperties.setDebugTrace(commandLine.hasOption("debug"));
			fileGenerationProperties.setDebugAll(commandLine.hasOption("debug_all"));
			if(commandLine.hasOption("exporttransitionsystem")) {
				if(compilerFeatures.contains(CompilerExtension.TIMED_REBECA)) {
					if (((TimedRebecaFileGeneratorProperties)fileGenerationProperties).isTraceGenerator())
						throw new CodeCompilationException("\"Trace Generator\" and \"Export State Space\" options are incompatible.", 0, 0);					
				}
				String targetFileName = commandLine.getOptionValue("exporttransitionsystem");
				if(targetFileName == null)
					targetFileName = "statespace.xml";
				fileGenerationProperties.setExportStateSpaceTargetFile(targetFileName);
			}
			fileGenerationProperties.setProgressReport(commandLine.hasOption("progressreport"));
//			if(commandLine.hasOption("errorconsole"))
//				out = new PrintStream(new File(commandLine.getOptionValue("errorconsole")));
			
			
			rmc.modelCheckersFilesGenerator.generateFiles(rebecaFile, propertyFile, destination, compilerFeatures, fileGenerationProperties);
			for (Exception e : rmc.exceptionContainer.getWarnings()) {
				if (e instanceof CodeCompilationException) {
					CodeCompilationException ce = (CodeCompilationException) e;
					out.println("Line " + ce.getLine() + ", Warning: " + ce.getMessage());
				} else {
					out.println(e.getMessage());
					e.printStackTrace();
				}
			}
			List<Exception> exceptions = new ArrayList<Exception>();
			exceptions.addAll(rmc.exceptionContainer.getExceptions());
			Collections.sort(exceptions, new Comparator<Exception>() {
				public int compare(Exception o1, Exception o2) {
					if (!(o1 instanceof CodeCompilationException))
						return 1;
					if (!(o2 instanceof CodeCompilationException))
						return -1;
					CodeCompilationException cce1 = (CodeCompilationException) o1, cce2 = (CodeCompilationException) o2;
					return cce1.getLine() < cce2.getLine() ? -1 : cce1.getLine() > cce2.getLine() ? 1 : cce1.getColumn() < cce2.getColumn() ? -1 :
							cce1.getColumn() > cce2.getColumn() ? 1 : 0;
				}
			});
			for (Exception e : exceptions) {
				if (e instanceof CodeCompilationException) {
					CodeCompilationException ce = (CodeCompilationException) e;
					out.println("Line " + ce.getLine() + ", Error: "
							+ ce.getMessage());
				} else {
					out.println(e.getMessage());
					e.printStackTrace(out);
				}
			}

		} catch (ParseException e) {
			if(!e.getMessage().isEmpty())
				out.println("Unexpected exception: " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("rmc [options]", options);
		} catch (Exception e) {
			e.printStackTrace();
			out.println("Unexpected exception: " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("rmc [options]", options);
		}
	}
}