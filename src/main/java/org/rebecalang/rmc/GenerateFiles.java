package org.rebecalang.rmc;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionGroup;
import org.rebecalang.compiler.modelcompiler.RebecaCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.propertycompiler.corerebeca.PropertyCompiler;
import org.rebecalang.compiler.propertycompiler.corerebeca.objectmodel.PropertyModel;
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
	
	public static GenerateFiles getInstance() {
		return instance;
	}
	
	public ExceptionContainer getExceptionContainer() {
		return container;
	}

	public void generateFiles(File rebecaFile, File propertyFile,
			File destinationLocation, Set<CompilerFeature> compilerFeatures,
			Set<AnalysisFeature> analysisFeatures, CommandLine commandLine) {
		
		RebecaCompiler rebecaCompiler = new RebecaCompiler();
		Pair<RebecaModel, SymbolTable> compilationResult = rebecaCompiler.compileRebecaFile(rebecaFile, compilerFeatures);
		RebecaModel rebecaModel = compilationResult.getFirst();
		this.container = rebecaCompiler.getExceptionContainer();
		
		if (!container.getExceptions().isEmpty()) {
			return;
		}
		try {
			PropertyModel propertyModel = null;
			if (propertyFile != null) {
				PropertyCompiler propertyCompiler = new PropertyCompiler();
				propertyModel = propertyCompiler.compilePropertyModel(
						rebecaModel, compilationResult.getSecond(), propertyFile, compilerFeatures);
				container.addAll(propertyCompiler.getExceptionContainer());
				if (!container.getExceptions().isEmpty()) {
					return;
				}
			}
			AbstractFileGenerator fileGenerator = null;
			if (compilerFeatures.contains(CompilerFeature.PROBABILISTIC_REBECA) && compilerFeatures.contains(CompilerFeature.TIMED_REBECA)) {
				fileGenerator = new ProbabilisticTimedRebecaFileGenerator();
				fileGenerator.prepare(rebecaModel, propertyModel, compilerFeatures, analysisFeatures, commandLine, destinationLocation, container);
				
			} else if (compilerFeatures.contains(CompilerFeature.TIMED_REBECA)) {
				fileGenerator = new TimedRebecaFileGenerator();
				fileGenerator.prepare(rebecaModel, propertyModel, compilerFeatures, analysisFeatures, commandLine, destinationLocation, container);
			} else if (compilerFeatures.contains(CompilerFeature.PROBABILISTIC_REBECA)) {
				
			} else {
				fileGenerator = new CoreRebecaFileGenerator();
				fileGenerator.prepare(rebecaModel, propertyModel, compilerFeatures, analysisFeatures, commandLine, destinationLocation, container);
			}
			fileGenerator.generateFiles();
		} catch(CodeCompilationException ce) {
			container.addException(ce);
		}

	}

	public Set<OptionGroup> getOptions() {
		Set<OptionGroup> retValue = new HashSet<OptionGroup>();
		retValue.add(TimedRebecaFileGenerator.getOptions());
		retValue.add(CoreRebecaFileGenerator.getOptions());
		return retValue;
	}

}
