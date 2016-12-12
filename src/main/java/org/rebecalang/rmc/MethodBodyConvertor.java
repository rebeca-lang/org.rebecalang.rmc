package org.rebecalang.rmc;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.rmc.corerebeca.translator.DotPrimaryExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.NondetExpressionTranslator;

public class MethodBodyConvertor {
	
	protected ExceptionContainer container = new ExceptionContainer();
	private static String TAB = AbstractStatementTranslator.TAB;
	private static String NEW_LINE = AbstractStatementTranslator.NEW_LINE;
	private Set<AnalysisFeature> analysisFeatures;
	
	public MethodBodyConvertor(Set<AnalysisFeature> analysisFeatures) {
		this.analysisFeatures = analysisFeatures;
	}
	
	
	private String attachInitiativePart(ReactiveClassDeclaration reactiveClassDeclaration, MethodDeclaration methodDeclaration, String retValue) {
		NondetExpressionTranslator ndExpressionTranslator = 
				((NondetExpressionTranslator)StatementTranslatorContainer.getTranslator(NonDetExpression.class));
		retValue = TAB + TAB + "shift = 1;" + NEW_LINE +
				TAB + TAB + "#ifdef SAFE_MODE" + NEW_LINE + 
				TAB + TAB + TAB + "string reactiveClassName = this->getName();" + NEW_LINE +
				TAB + TAB + TAB + "string methodName = \"" + methodDeclaration.getName() + "\";" + NEW_LINE +
				TAB + TAB +  "#endif" + NEW_LINE +
				ndExpressionTranslator.getNonDetHeadString() + retValue;
		if (analysisFeatures.contains(AnalysisFeature.SAFE_MODE)) {
			String temp = "long arrayIndexChecker = 0;";
			temp += ((DotPrimaryExpressionTranslator)StatementTranslatorContainer.getTranslator(DotPrimary.class)).
					getSafeModeBeforeUsageDefinitions();
			retValue = TAB + TAB + temp + NEW_LINE + retValue;
		}
		retValue += ndExpressionTranslator.getNonDetTailString();
		return retValue;
	}
	public String convertMsgsrvBody(ReactiveClassDeclaration reactiveClassDeclaration, 
			MethodDeclaration methodDeclaration) throws StatementTranslationException {
		StatementTranslatorContainer.initialize();
		String retValue = NEW_LINE + StatementTranslatorContainer.translate(methodDeclaration.getBlock(), TAB + TAB);
		retValue = attachInitiativePart(reactiveClassDeclaration, methodDeclaration, retValue);
		retValue += TAB + TAB + "return 0;" + NEW_LINE;
		return retValue;
	}
	
	public String convertSynchMethodBody(ReactiveClassDeclaration reactiveClassDeclaration,
			MethodDeclaration methodDeclaration) throws StatementTranslationException {
		StatementTranslatorContainer.initialize();
		String retValue = NEW_LINE + StatementTranslatorContainer.translate(methodDeclaration.getBlock(), TAB + TAB);
		retValue = attachInitiativePart(reactiveClassDeclaration, methodDeclaration, retValue);
		NondetExpressionTranslator ndExpressionTranslator = 
				((NondetExpressionTranslator)StatementTranslatorContainer.getTranslator(NonDetExpression.class));
		if (!ndExpressionTranslator.getNonDetHeadString().equals(""))
			container.addException(new StatementTranslationException("This version of translator does not support " +
					"nonedeterministic assignment inside synch methods.", 
					methodDeclaration.getLineNumber(), methodDeclaration.getCharacter()));
		return retValue;
	}
	
	public String convertConstructorBody(ReactiveClassDeclaration reactiveClassDeclaration,
			MethodDeclaration methodDeclaration) throws StatementTranslationException {
		StatementTranslatorContainer.initialize();
		String retValue = NEW_LINE + StatementTranslatorContainer.translate(methodDeclaration.getBlock(), TAB + TAB);
		retValue = attachInitiativePart(reactiveClassDeclaration, methodDeclaration, retValue);
		NondetExpressionTranslator ndExpressionTranslator = 
				((NondetExpressionTranslator)StatementTranslatorContainer.getTranslator(NonDetExpression.class));
		if (ndExpressionTranslator.hasNonDetStatement())
			container.addException(new StatementTranslationException("This version of translator does not support " +
					"nonedeterministic assignment inside constructors.", 
					methodDeclaration.getLineNumber(), methodDeclaration.getCharacter()));
		retValue += TAB + TAB + "shift = 0;" + NEW_LINE + TAB + TAB + "return 0;" + NEW_LINE;
		return retValue;
	}
}