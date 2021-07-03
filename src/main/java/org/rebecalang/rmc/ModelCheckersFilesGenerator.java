package org.rebecalang.rmc;

import java.io.File;
import java.util.Set;

import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.propertycompiler.PropertyCompiler;
import org.rebecalang.compiler.propertycompiler.generalrebeca.objectmodel.PropertyModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.rmc.corerebeca.CoreRebecaFileGenerator;
import org.rebecalang.rmc.probabilistictimedrebeca.ProbabilisticTimedRebecaFileGenerator;
import org.rebecalang.rmc.timedrebeca.TimedRebecaFileGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModelCheckersFilesGenerator {
	
	@Autowired
	private ExceptionContainer exceptionContainer;
	
	@Autowired
	private RebecaModelCompiler rebecaModelCompiler;
	
	@Autowired
	private PropertyCompiler propertyCompiler;
	
	@Autowired
	CoreRebecaFileGenerator coreRebecaFileGenerator;
	
	@Autowired
	TimedRebecaFileGenerator timedRebecaFileGenerator;
	
	@Autowired
	ProbabilisticTimedRebecaFileGenerator probabilisticTimedRebecaFileGenerator;
	
	
	PropertyModel propertyModel;
	
	public PropertyModel getPropertyModel() {
		return propertyModel;
	}

	public void generateFiles(File rebecaFile, File propertyFile, File destination,
			Set<CompilerExtension> extension, FileGeneratorProperties fileGenerationProperties) {
		
		exceptionContainer.clear();
		
		propertyModel = null;
		
		Pair<RebecaModel, SymbolTable> compilationResult = 
				rebecaModelCompiler.compileRebecaFile(rebecaFile, extension, fileGenerationProperties.getCoreVersion());
		this.exceptionContainer = rebecaModelCompiler.getExceptionContainer();
		
		if (!exceptionContainer.getExceptions().isEmpty()) {
			return;
		}
		RebecaModel rebecaModel = compilationResult.getFirst();
		if (propertyFile != null) {
			propertyModel = propertyCompiler.compilePropertyFile(propertyFile, rebecaModel, extension);
			if (!exceptionContainer.getExceptions().isEmpty()) {
				return;
			}
		}
		AbstractFileGenerator fileGenerator = null;
		if (extension.contains(CompilerExtension.PROBABILISTIC_REBECA) && extension.contains(CompilerExtension.TIMED_REBECA)) {
			fileGenerator = probabilisticTimedRebecaFileGenerator;
		} else if (extension.contains(CompilerExtension.TIMED_REBECA)) {
			fileGenerator = timedRebecaFileGenerator;
		} else if (extension.contains(CompilerExtension.PROBABILISTIC_REBECA)) {
			return;
		} else {
			fileGenerator = coreRebecaFileGenerator;
		}

		fileGenerator.generateFiles(rebecaModel, propertyModel, destination, extension, fileGenerationProperties);

	}

//	public static ExceptionContainer cloneAndConvertPropertyCompilationExceptions(ExceptionContainer exceptionContainer) {
//		ExceptionContainer container = new ExceptionContainer();
//		for(Exception exception : exceptionContainer.getExceptions()) {
//			if (exception instanceof PropertyCodeCompilationException)
//				container.getExceptions().add(exception);
//			else if (exception instanceof CodeCompilationException) {
//				PropertyCodeCompilationException propertyCompileException = new PropertyCodeCompilationException(
//						exception.getMessage(),
//						((CodeCompilationException)exception).getLine(),
//						((CodeCompilationException)exception).getColumn());
//				container.addException(propertyCompileException);
//			} else
//				container.getExceptions().add(exception);
//		}
//		return container;
//	}
}
