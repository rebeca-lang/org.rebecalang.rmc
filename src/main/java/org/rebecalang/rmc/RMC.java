/*                  In the name of Allah                */
/*                   The best will come                 */

package org.rebecalang.rmc;

import java.io.File;
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
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;


/**
 * RMC main class that creates MODERE cpp core classes and its LTL/CTL property
 * C++ files.
 */
public class RMC {

	@SuppressWarnings("static-access")
	public static void main(String[] args) {

		CommandLineParser cmdLineParser = new GnuParser();
		Options options = new Options();
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
                    .withDescription("Rebeca compiler version (2.0, 2.1, 2.2). Default version is 2.1")
                    .withLongOpt("version").create('v');
			options.addOption(option);
			
			option = OptionBuilder.withArgName("value")
                    .hasArg()
                    .withDescription("Rebeca model extension (CoreRebeca/TimedRebeca/ProbabilisticRebeca/" +
                    		"ProbabilisticTimedRebeca). Default is \'CoreRebeca\'.")
                    .withLongOpt("extension").create('e');
			options.addOption(option);
			
			option = OptionBuilder.withArgName("file")
	                .hasOptionalArg()
	                .withDescription("Export transition system in an XML file.")
	                .withLongOpt("exporttransitionsystem").create("x");
			options.addOption(option);

			options.addOption(new Option("debug", "Enable debug mode in result C++ files."));
			options.addOption(new Option("debug2", "Enable debug level 2 mode in result C++ files."));
			options.addOption(new Option("h", "help", false, "Print this message."));

			for (OptionGroup additionalOption : GenerateFiles.getInstance().getOptions())
				options.addOptionGroup(additionalOption);

			CommandLine commandLine = cmdLineParser.parse(options, args);

			if (commandLine.hasOption("help"))
				throw new ParseException("");

			File rebecaFile = new File(commandLine.getOptionValue("source"));
			File propertyFile = null;
			if (commandLine.hasOption("property"))
				propertyFile = new File(commandLine.getOptionValue("property"));

			// Set output location. Default location is rmc-output folder.
			File destination;
			if (commandLine.hasOption("output")) {
				destination = new File(commandLine.getOptionValue("output"));
			} else {
				destination = new File("rmc-output");
			}
			
			Set<CompilerFeature> compilerFeatures = new HashSet<CompilerFeature>();
			CompilerFeature coreVersion = null;
			if (commandLine.hasOption("version")) {
				String version = commandLine.getOptionValue("version");
				if (version.equals("2.0"))
					coreVersion = CompilerFeature.CORE_2_0;
				else if (version.equals("2.1"))
					coreVersion = CompilerFeature.CORE_2_1;
				else {
					throw new ParseException("Unrecognized Rebeca version: " + version);
				}
			} else {
				coreVersion = CompilerFeature.CORE_2_1;
			}
			compilerFeatures.add(coreVersion);
			
			String extensionLabel;
			if (commandLine.hasOption("extension")) {
				extensionLabel = commandLine.getOptionValue("extension");
			} else {
				extensionLabel = "CoreRebeca";
			}
			if (extensionLabel.equals("CoreRebeca")) {
				
			} else if (extensionLabel.equals("TimedRebeca")) {
				compilerFeatures.add(CompilerFeature.TIMED_REBECA);
			} else if (extensionLabel.equals("ProbabilisticRebeca")) {
				compilerFeatures.add(CompilerFeature.PROBABILISTIC_REBECA);
			} else if (extensionLabel.equals("ProbabilisticTimedRebeca")) {
				compilerFeatures.add(CompilerFeature.PROBABILISTIC_REBECA);
				compilerFeatures.add(CompilerFeature.TIMED_REBECA);
			} else {
				throw new ParseException("Unrecognized Rebeca extension: " + extensionLabel);
			}

			Set<AnalysisFeature> analysisFeatures = new HashSet<AnalysisFeature>();

			GenerateFiles.getInstance().generateFiles(rebecaFile, propertyFile, destination, compilerFeatures, analysisFeatures, commandLine);
			for (Exception e : GenerateFiles.getInstance().getExceptionContainer().getWarnings()) {
				if (e instanceof CodeCompilationException) {
					CodeCompilationException ce = (CodeCompilationException) e;
					System.out.println("Line " + ce.getLine() + ", Warning: "
							+ ce.getMessage());
				} else {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
			List<Exception> exceptions = new ArrayList<Exception>();
			exceptions.addAll(GenerateFiles.getInstance().getExceptionContainer().getExceptions());
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
					System.out.println("Line " + ce.getLine() + ", Error: "
							+ ce.getMessage());
				} else {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}

		} catch (ParseException e) {
			if(!e.getMessage().isEmpty())
				System.out.println("Unexpected exception: " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("rmc [options]", options);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unexpected exception: " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("rmc [options]", options);
		}
		
	}
}