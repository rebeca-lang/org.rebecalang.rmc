package org.rebecalang.rmc;

import java.io.File;
import java.util.Properties;
import java.util.Set;

import org.rebecalang.compiler.modelcompiler.RebecaCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.propertycompiler.generalrebeca.objectmodel.PropertyModel;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.rmc.corerebeca.CoreRebecaFileGenerator;
import org.rebecalang.rmc.probabilistictimedrebeca.ProbabilisticTimedRebecaFileGenerator;
import org.rebecalang.rmc.timedrebeca.TimedRebecaFileGenerator;

public class GenerateFiles {
	
	private static GenerateFiles instance = new GenerateFiles();
	private ExceptionContainer container;
	PropertyModel propertyModel;
	
	public PropertyModel getPropertyModel() {
		return propertyModel;
	}

	public static GenerateFiles getInstance() {
		return instance;
	}
	
	public ExceptionContainer getExceptionContainer() {
		return container;
	}

	public void generateFiles(File rebecaFile, File propertyFile,
			File destinationLocation, Set<CompilerFeature> compilerFeatures,
			Set<AnalysisFeature> analysisFeatures, Properties properties) {
		
		propertyModel = null;
		
		RebecaCompiler rebecaCompiler = new RebecaCompiler();
		Pair<RebecaModel, SymbolTable> compilationResult = rebecaCompiler.compileRebecaFile(rebecaFile, compilerFeatures);
		this.container = rebecaCompiler.getExceptionContainer();
		
		if (!container.getExceptions().isEmpty()) {
			return;
		}
		try {
			RebecaModel rebecaModel = compilationResult.getFirst();
			AbstractFileGenerator fileGenerator = null;
			if (compilerFeatures.contains(CompilerFeature.PROBABILISTIC_REBECA) && compilerFeatures.contains(CompilerFeature.TIMED_REBECA)) {
				fileGenerator = new ProbabilisticTimedRebecaFileGenerator();
			} else if (compilerFeatures.contains(CompilerFeature.TIMED_REBECA)) {
				if (propertyFile != null) {
					org.rebecalang.compiler.propertycompiler.timedrebeca.PropertyCompiler propertyCompiler = 
							new org.rebecalang.compiler.propertycompiler.timedrebeca.PropertyCompiler();
					propertyModel = propertyCompiler.compilePropertyModel(
							rebecaModel, compilationResult.getSecond(), propertyFile, compilerFeatures);
					container.addAll(propertyCompiler.getExceptionContainer());
					if (!container.getExceptions().isEmpty()) {
						return;
					}
				}
				fileGenerator = new TimedRebecaFileGenerator();
			} else if (compilerFeatures.contains(CompilerFeature.PROBABILISTIC_REBECA)) {
				return;
			} else {
				if (propertyFile != null) {
					org.rebecalang.compiler.propertycompiler.corerebeca.PropertyCompiler propertyCompiler = 
							new org.rebecalang.compiler.propertycompiler.corerebeca.PropertyCompiler();
					propertyModel = propertyCompiler.compilePropertyModel(
							rebecaModel, compilationResult.getSecond(), propertyFile, compilerFeatures);
					container.addAll(propertyCompiler.getExceptionContainer());
					if (!container.getExceptions().isEmpty()) {
						return;
					}
				}
				fileGenerator = new CoreRebecaFileGenerator();
			}
			fileGenerator.prepare(rebecaModel, propertyModel, 
					compilerFeatures, analysisFeatures, destinationLocation, properties, container);
			fileGenerator.generateFiles();
		} catch(CodeCompilationException ce) {
			container.addException(ce);
		}

	}

//	public Set<OptionGroup> getOptions() {
//		Set<OptionGroup> retValue = new HashSet<OptionGroup>();
//		retValue.add(TimedRebecaFileGenerator.getOptions());
//		retValue.add(CoreRebecaFileGenerator.getOptions());
//		return retValue;
//	}
	
//	public void parseAdditionalCommands(CommandLine commandLine, Set<CompilerFeature> compilerFeatures,
//			Set<AnalysisFeature> analysisFeatures) {
//		if(commandLine.hasOption("debug"))
//			analysisFeatures.add(AnalysisFeature.DEBUG);
//		if(commandLine.hasOption("debug2"))
//			analysisFeatures.add(AnalysisFeature.DEBUG_LEVEL_2);
//	}

}
