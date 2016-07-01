package org.rebecalang.rmc;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.rmc.corerebeca.translator.NondetExpressionTranslator;

public class MethodBodyConvertor {
	
	protected ExceptionContainer container = new ExceptionContainer();
	private static String TAB = AbstractStatementTranslator.TAB;
	private static String NEW_LINE = AbstractStatementTranslator.NEW_LINE;
	private Set<AnalysisFeature> analysisFeatures;
	
	public MethodBodyConvertor(Set<AnalysisFeature> analysisFeatures) {
		this.analysisFeatures = analysisFeatures;
	}
	
	public String convertMsgsrvBody(MethodDeclaration methodDeclaration) throws StatementTranslationException {
		StatementTranslatorContainer.initialize();
		String retValue = NEW_LINE + StatementTranslatorContainer.translate(methodDeclaration.getBlock(), TAB + TAB);
		NondetExpressionTranslator ndExpressionTranslator = 
				((NondetExpressionTranslator)StatementTranslatorContainer.getTranslator(NonDetExpression.class));
		retValue = TAB + TAB + "shift = 1;" + NEW_LINE +
				ndExpressionTranslator.getNonDetHeadString() + retValue;
		if (analysisFeatures.contains(AnalysisFeature.SAFE_MODE))
			retValue = TAB + TAB + "int arrayIndexChecker = 0;" + NEW_LINE + retValue; 
		retValue += ndExpressionTranslator.getNonDetTailString();
		retValue += TAB + TAB + "return 0;" + NEW_LINE;
		return retValue;
	}
	
	public String convertSynchMethodBody(MethodDeclaration methodDeclaration) throws StatementTranslationException {
		StatementTranslatorContainer.initialize();
		NondetExpressionTranslator ndExpressionTranslator = 
				((NondetExpressionTranslator)StatementTranslatorContainer.getTranslator(NonDetExpression.class));
		String retValue = NEW_LINE + StatementTranslatorContainer.translate(methodDeclaration.getBlock(), TAB + TAB);
		if (analysisFeatures.contains(AnalysisFeature.SAFE_MODE))
			retValue = TAB + TAB + "int arrayIndexChecker;" + NEW_LINE + retValue; 
		if (!ndExpressionTranslator.getNonDetHeadString().equals(""))
			container.addException(new StatementTranslationException("This version of translator does not supprt " +
					"non-deterministic assignment inside synch methods.", 
					methodDeclaration.getLineNumber(), methodDeclaration.getCharacter()));
		return retValue;
	}
	
	public String convertConstructorBody(MethodDeclaration methodDeclaration) throws StatementTranslationException {
		StatementTranslatorContainer.initialize();
		String retValue = NEW_LINE + StatementTranslatorContainer.translate(methodDeclaration.getBlock(), TAB + TAB);
		if (analysisFeatures.contains(AnalysisFeature.SAFE_MODE))
			retValue = TAB + TAB + "int arrayIndexChecker;" + NEW_LINE + retValue; 

		retValue += TAB + TAB + "shift = 0;" + NEW_LINE + TAB + TAB + "return 0;" + NEW_LINE;
		return retValue;
	}
}