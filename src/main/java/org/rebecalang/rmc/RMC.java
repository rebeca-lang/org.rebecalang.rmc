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
import java.util.Properties;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;


/**
 * RMC main class that creates MODERE cpp core classes and its LTL/CTL property
 * C++ files.
 */
public class RMC {

//	@SuppressWarnings("static-access")
//	public static void test (String[] args) {
//		CommandLineParser cmdLineParser = new GnuParser();
//		Options options = new Options();
//		PrintStream out = System.out;
//
//		try {
//			Option option = OptionBuilder.withArgName("file")
//                    .hasArg()
//                    .withDescription("Generated C++ source files location. Default location is \'./rmc-output\' folder.")
//                    .withLongOpt("output").create('o');
//			options.addOption(option);
//
//			option = OptionBuilder.withArgName("file")
//                    .hasArg()
//                    .withDescription("Rebeca model source file.")
//                    .withLongOpt("source").create('s');
//			option.setRequired(true);
//			options.addOption(option);
//			
//			option = OptionBuilder.withArgName("file")
//                    .hasArg()
//                    .withDescription("Rebeca model property file.")
//                    .withLongOpt("property").create('p');
//			options.addOption(option);
//			
//			option = OptionBuilder.withArgName("value")
//                    .hasArg()
//                    .withDescription("Rebeca compiler version (2.0, 2.1, 2.2). Default version is 2.1")
//                    .withLongOpt("version").create('v');
//			options.addOption(option);
//			
//			option = OptionBuilder.withArgName("value")
//                    .hasArg()
//                    .withDescription("Rebeca model extension (CoreRebeca/TimedRebeca/ProbabilisticRebeca/" +
//                    		"ProbabilisticTimedRebeca). Default is \'CoreRebeca\'.")
//                    .withLongOpt("extension").create('e');
//			options.addOption(option);
//			
//			option = OptionBuilder.withArgName("file")
//	                .hasOptionalArg()
//	                .withDescription("Exports transition system in an XML file.")
//	                .withLongOpt("exporttransitionsystem").create("x");
//			options.addOption(option);
//
//			options.addOption(new Option("safemode", "Checks for array index out of bound during the model checking."));
//			options.addOption(new Option("simplified_statespace", "Eliminates the content of states in the XML file of the state space."));
//			
//			options.addOption(new Option("debug", "Enables debug mode in result C++ files."));
//			options.addOption(new Option("debug2", "Enables debug level 2 mode in result C++ files."));
//			options.addOption(new Option("h", "help", false, "Print this message."));
//			options.addOption(new Option(null, "errorconsole", true, "Error log console."));
//			
//			CommandLine commandLine = cmdLineParser.parse(options, args);
//
//			if (commandLine.hasOption("help"))
//				throw new ParseException("");
//
//			File rebecaFile = new File(commandLine.getOptionValue("source"));
//			if (!rebecaFile.exists() || rebecaFile.isDirectory())
//				throw new ParseException("No such file: \'" + commandLine.getOptionValue("source") + "'");
//
//			File propertyFile = null;
//			if (commandLine.hasOption("property")) {
//				propertyFile = new File(commandLine.getOptionValue("property"));
//				if (!propertyFile.exists() || propertyFile.isDirectory())
//					throw new ParseException("No such file: \'" + commandLine.getOptionValue("property") + "'");
//
//			}
//
//			// Set output location. Default location is rmc-output folder.
//			File destination;
//			if (commandLine.hasOption("output")) {
//				destination = new File(commandLine.getOptionValue("output"));
//			} else {
//				destination = new File("rmc-output");
//			}
//			
//			Set<CompilerFeature> compilerFeatures = new HashSet<CompilerFeature>();
//			CompilerFeature coreVersion = null;
//			if (commandLine.hasOption("version")) {
//				String version = commandLine.getOptionValue("version");
//				if (version.equals("2.0"))
//					coreVersion = CompilerFeature.CORE_2_0;
//				else if (version.equals("2.1"))
//					coreVersion = CompilerFeature.CORE_2_1;
//				else {
//					throw new ParseException("Unrecognized Rebeca version: " + version);
//				}
//			} else {
//				coreVersion = CompilerFeature.CORE_2_1;
//			}
//			compilerFeatures.add(coreVersion);
//
//
//			String extensionLabel;
//			if (commandLine.hasOption("extension")) {
//				extensionLabel = commandLine.getOptionValue("extension");
//			} else {
//				extensionLabel = "CoreRebeca";
//			}
//			
//			if (extensionLabel.equals("CoreRebeca")) {
//				
//			} else if (extensionLabel.equals("TimedRebeca")) {
//				compilerFeatures.add(CompilerFeature.TIMED_REBECA);
//			} else if (extensionLabel.equals("ProbabilisticRebeca")) {
//				compilerFeatures.add(CompilerFeature.PROBABILISTIC_REBECA);
//			} else if (extensionLabel.equals("ProbabilisticTimedRebeca")) {
//				compilerFeatures.add(CompilerFeature.PROBABILISTIC_REBECA);
//				compilerFeatures.add(CompilerFeature.TIMED_REBECA);
//			} else {
//				throw new ParseException("Unrecognized Rebeca extension: " + extensionLabel);
//			}
//
//			Set<AnalysisFeature> analysisFeatures = new HashSet<AnalysisFeature>();
//			
//			if(commandLine.hasOption("safemode"))
//				analysisFeatures.add(AnalysisFeature.SAFE_MODE);
//			if(commandLine.hasOption("simplified_statespace"))
//				analysisFeatures.add(AnalysisFeature.SIMPLIFIED_STATESPACE);
//			if(commandLine.hasOption("errorconsole"))
//				out = new PrintStream(new File(commandLine.getOptionValue("errorconsole")));
//			
//			GenerateFiles.getInstance().generateFiles(rebecaFile, propertyFile, destination, compilerFeatures, 
//					analysisFeatures, Properties);
//			for (Exception e : GenerateFiles.getInstance().getExceptionContainer().getWarnings()) {
//				if (e instanceof CodeCompilationException) {
//					CodeCompilationException ce = (CodeCompilationException) e;
//					out.println("Line " + ce.getLine() + ", Warning: " + ce.getMessage());
//				} else {
//					out.println(e.getMessage());
//					e.printStackTrace();
//				}
//			}
//			List<Exception> exceptions = new ArrayList<Exception>();
//			exceptions.addAll(GenerateFiles.getInstance().getExceptionContainer().getExceptions());
//			Collections.sort(exceptions, new Comparator<Exception>() {
//				public int compare(Exception o1, Exception o2) {
//					if (!(o1 instanceof CodeCompilationException))
//						return 1;
//					if (!(o2 instanceof CodeCompilationException))
//						return -1;
//					CodeCompilationException cce1 = (CodeCompilationException) o1, cce2 = (CodeCompilationException) o2;
//					return cce1.getLine() < cce2.getLine() ? -1 : cce1.getLine() > cce2.getLine() ? 1 : cce1.getColumn() < cce2.getColumn() ? -1 :
//							cce1.getColumn() > cce2.getColumn() ? 1 : 0;
//				}
//			});
//			for (Exception e : exceptions) {
//				if (e instanceof CodeCompilationException) {
//					CodeCompilationException ce = (CodeCompilationException) e;
//					out.println("Line " + ce.getLine() + ", Error: "
//							+ ce.getMessage());
//				} else {
//					out.println(e.getMessage());
//					e.printStackTrace(out);
//				}
//			}
//
//		} catch (ParseException e) {
//			if(!e.getMessage().isEmpty())
//				out.println("Unexpected exception: " + e.getMessage());
//			HelpFormatter formatter = new HelpFormatter();
//			formatter.printHelp("rmc [options]", options);
//		} catch (Exception e) {
//			e.printStackTrace();
//			out.println("Unexpected exception: " + e.getMessage());
//			HelpFormatter formatter = new HelpFormatter();
//			formatter.printHelp("rmc [options]", options);
//		}		
//	}
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
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
	                .withDescription("Exports transition system in an XML file.")
	                .withLongOpt("exporttransitionsystem").create("x");
			options.addOption(option);

			options.addOption(new Option(null, "safemode", false, "Checks for array index out of bound during the model checking."));
			options.addOption(new Option(null, "simplified_statespace", false, "Eliminates the content of states in the XML file of the state space."));
			
			options.addOption(new Option(null, "debug", false, "Enables debug mode in result C++ files."));
			options.addOption(new Option(null, "debug2", false, "Enables debug level 2 mode in result C++ files."));
			options.addOption(new Option("h", "help", false, "Print this message."));
			options.addOption(new Option(null, "errorconsole", true, "Error log console."));
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
			
			Set<CompilerFeature> compilerFeatures = new HashSet<CompilerFeature>();
			Set<AnalysisFeature> analysisFeatures = new HashSet<AnalysisFeature>();

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
				if (commandLine.hasOption("tracegenerator")) {
					if (analysisFeatures.contains(AnalysisFeature.EXPORT_STATE_SPACE)) {
						throw new CodeCompilationException("\"Trace Generator\" and \"Export State Space\" options are incompatible.", 0, 0);
					}
					analysisFeatures.add(AnalysisFeature.TRACE_GENERATOR);
				}
				if(commandLine.hasOption("tts"))
					analysisFeatures.add(AnalysisFeature.TTS);
				if(commandLine.hasOption("compactdtg"))
					analysisFeatures.add(AnalysisFeature.COMPACT_DTG);
			} else if (extensionLabel.equals("ProbabilisticRebeca")) {
				compilerFeatures.add(CompilerFeature.PROBABILISTIC_REBECA);
			} else if (extensionLabel.equals("ProbabilisticTimedRebeca")) {
				compilerFeatures.add(CompilerFeature.PROBABILISTIC_REBECA);
				compilerFeatures.add(CompilerFeature.TIMED_REBECA);
			} else {
				throw new ParseException("Unrecognized Rebeca extension: " + extensionLabel);
			}

			Properties properties = new Properties();
			
			if(commandLine.hasOption("safemode"))
				analysisFeatures.add(AnalysisFeature.SAFE_MODE);
			if(commandLine.hasOption("simplified_statespace"))
				analysisFeatures.add(AnalysisFeature.SIMPLIFIED_STATESPACE);
			if(commandLine.hasOption("errorconsole"))
				out = new PrintStream(new File(commandLine.getOptionValue("errorconsole")));
			if(commandLine.hasOption("progressreport"))
				analysisFeatures.add(AnalysisFeature.PROGRESS_REPORT);
			if(commandLine.hasOption("debug"))
				analysisFeatures.add(AnalysisFeature.DEBUG);
			if(commandLine.hasOption("debug2"))
				analysisFeatures.add(AnalysisFeature.DEBUG_LEVEL_2);
			if(commandLine.hasOption("exporttransitionsystem")) {
				analysisFeatures.add(AnalysisFeature.EXPORT_STATE_SPACE);
				if (commandLine.getOptionValue("exporttransitionsystem") != null)
					properties.put("statespace", commandLine.getOptionValue("exporttransitionsystem"));
			}
			
			
			GenerateFiles.getInstance().generateFiles(rebecaFile, propertyFile, destination, compilerFeatures, 
					analysisFeatures, properties);
			for (Exception e : GenerateFiles.getInstance().getExceptionContainer().getWarnings()) {
				if (e instanceof CodeCompilationException) {
					CodeCompilationException ce = (CodeCompilationException) e;
					out.println("Line " + ce.getLine() + ", Warning: " + ce.getMessage());
				} else {
					out.println(e.getMessage());
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