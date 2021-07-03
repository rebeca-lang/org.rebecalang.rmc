package org.rebecalang.rmc.corerebeca;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.FileGeneratorProperties;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.rebecalang.rmc.corerebeca.translator.DotPrimaryExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.CoreRebecaNondetExpressionTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Qualifier("CORE_REBECA")
public class CoreRebecaMethodBodyConvertor {
	
	@Autowired
	protected ExceptionContainer exceptionContainer;
	
	private FileGeneratorProperties fileGenerationProperties;
	protected static String TAB = AbstractStatementTranslator.TAB;
	protected static String NEW_LINE = AbstractStatementTranslator.NEW_LINE;
	
	StatementTranslatorContainer statementTranslatorContainer;
	
//	@Autowired
//	public CoreRebecaMethodBodyConvertor(StatementTranslatorContainer statementTranslatorContainer) {
//		this.statementTranslatorContainer = statementTranslatorContainer;
//	}
	
	public void setStatementTranslatorContainer(StatementTranslatorContainer statementTranslatorContainer) {
		this.statementTranslatorContainer = statementTranslatorContainer;
	}
	
	public void initilize(FileGeneratorProperties fileGenerationProperties) {
		this.fileGenerationProperties = fileGenerationProperties;
	}
	
	private String attachInitiativePart(ReactiveClassDeclaration reactiveClassDeclaration, MethodDeclaration methodDeclaration, String retValue) {
		CoreRebecaNondetExpressionTranslator ndExpressionTranslator = 
				((CoreRebecaNondetExpressionTranslator)statementTranslatorContainer.getTranslator(NonDetExpression.class));
		retValue = TAB + TAB + "shift = 1;" + NEW_LINE +
				TAB + TAB + "#ifdef SAFE_MODE" + NEW_LINE + 
				TAB + TAB + TAB + "string reactiveClassName = this->getName();" + NEW_LINE +
				TAB + TAB + TAB + "string methodName = \"" + methodDeclaration.getName() + "\";" + NEW_LINE +
				TAB + TAB +  "#endif" + NEW_LINE +
				ndExpressionTranslator.getNonDetHeadString() + retValue;
		if (fileGenerationProperties.isSafeMode()) {
			String temp = "long arrayIndexChecker = 0;";
			temp += ((DotPrimaryExpressionTranslator)statementTranslatorContainer.getTranslator(DotPrimary.class)).
					getSafeModeBeforeUsageDefinitions();
			retValue = TAB + TAB + temp + NEW_LINE + retValue;
		}
		retValue += ndExpressionTranslator.getNonDetTailString();
		return retValue;
	}
	public String convertMsgsrvBody(ReactiveClassDeclaration reactiveClassDeclaration, 
			MethodDeclaration methodDeclaration) throws StatementTranslationException {
		statementTranslatorContainer.initialize(fileGenerationProperties);
		String retValue = NEW_LINE + statementTranslatorContainer.translate(methodDeclaration.getBlock(), TAB + TAB);
		retValue = attachInitiativePart(reactiveClassDeclaration, methodDeclaration, retValue);
		retValue += TAB + TAB + "return 0;" + NEW_LINE;
		return retValue;
	}
	
	public String convertSynchMethodBody(ReactiveClassDeclaration reactiveClassDeclaration,
			MethodDeclaration methodDeclaration) throws StatementTranslationException {
		statementTranslatorContainer.initialize(fileGenerationProperties);
		String retValue = NEW_LINE + statementTranslatorContainer.translate(methodDeclaration.getBlock(), TAB + TAB);
		retValue = attachInitiativePart(reactiveClassDeclaration, methodDeclaration, retValue);
		CoreRebecaNondetExpressionTranslator ndExpressionTranslator = 
				((CoreRebecaNondetExpressionTranslator)statementTranslatorContainer.getTranslator(NonDetExpression.class));
		if (!ndExpressionTranslator.getNonDetHeadString().equals(""))
			exceptionContainer.addException(new StatementTranslationException("This version of translator does not support " +
					"nonedeterministic assignment inside synch methods.", 
					methodDeclaration.getLineNumber(), methodDeclaration.getCharacter()));
		return retValue;
	}
	
	public String convertConstructorBody(ReactiveClassDeclaration reactiveClassDeclaration,
			MethodDeclaration methodDeclaration) throws StatementTranslationException {
		statementTranslatorContainer.initialize(fileGenerationProperties);
		String retValue = NEW_LINE + statementTranslatorContainer.translate(methodDeclaration.getBlock(), TAB + TAB);
		retValue = attachInitiativePart(reactiveClassDeclaration, methodDeclaration, retValue);
		CoreRebecaNondetExpressionTranslator ndExpressionTranslator = 
				((CoreRebecaNondetExpressionTranslator)statementTranslatorContainer.getTranslator(NonDetExpression.class));
		if (ndExpressionTranslator.hasNonDetStatement())
			exceptionContainer.addException(new StatementTranslationException("This version of translator does not support " +
					"nonedeterministic assignment inside constructors.", 
					methodDeclaration.getLineNumber(), methodDeclaration.getCharacter()));
		retValue += TAB + TAB + "shift = 0;" + NEW_LINE + TAB + TAB + "return 0;" + NEW_LINE;
		return retValue;
	}
}