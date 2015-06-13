package org.rebecalang.rmc;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionGroup;
import org.rebecalang.compiler.modelcompiler.RebecaCompiler;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
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

	public void generateFiles(File rebecaFile,
			File destinationLocation, Set<CompilerFeature> compilerFeatures,
			Set<AnalysisFeature> analysisFeatures, CommandLine commandLine) {
		
		RebecaCompiler rebecaCompiler = new RebecaCompiler();
		this.container = new ExceptionContainer();
		
		RebecaModel rebecaModel = rebecaCompiler.compileRebecaFile(rebecaFile, compilerFeatures, container);
		if (!container.getExceptions().isEmpty()) {
			return;
		}
		try {
			AbstractFileGenerator fileGenerator = null;
			if (compilerFeatures.contains(CompilerFeature.PROBABILISTIC_REBECA) && compilerFeatures.contains(CompilerFeature.TIMED_REBECA)) {
				fileGenerator = new ProbabilisticTimedRebecaFileGenerator();
				fileGenerator.prepare(rebecaModel, compilerFeatures, analysisFeatures, commandLine, destinationLocation, container);
				
			} else if (compilerFeatures.contains(CompilerFeature.TIMED_REBECA)) {
				fileGenerator = new TimedRebecaFileGenerator();
				fileGenerator.prepare(rebecaModel, compilerFeatures, analysisFeatures, commandLine, destinationLocation, container);
			} else if (compilerFeatures.contains(CompilerFeature.PROBABILISTIC_REBECA)) {
				
			} else {
				fileGenerator = new CoreRebecaFileGenerator();
				fileGenerator.prepare(rebecaModel, compilerFeatures, analysisFeatures, commandLine, destinationLocation, container);
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
