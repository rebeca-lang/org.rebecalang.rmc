package org.rebecalang.rmc.probabilisticrebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.probabilisticrebeca.objectmodel.ProbabilisticAlternativeValue;
import org.rebecalang.compiler.modelcompiler.probabilisticrebeca.objectmodel.ProbabilisticExpression;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.rebecalang.rmc.corerebeca.translator.NondetExpressionTranslator;

public class ProbabilisticExpressionTranslator extends AbstractStatementTranslator {

	public ProbabilisticExpressionTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {

		ProbabilisticExpression pExpression = (ProbabilisticExpression) statement;

		String assignProbabilities = "probability *= (";
		String nonDetVariableName = "nonDetVariable" + 
				((NondetExpressionTranslator) StatementTranslatorContainer.getTranslator(NonDetExpression.class)).getNonDetCounter();
		for (ProbabilisticAlternativeValue pav : pExpression.getChoices()) {
			assignProbabilities += nonDetVariableName + " == " + 
					StatementTranslatorContainer.translate(pav.getValue(), "") + "? (" +
					StatementTranslatorContainer.translate(pav.getProbability(), "") + ") : ";
		}
		assignProbabilities += " -1)";

		NonDetExpression ndExpression = new NonDetExpression();
		ndExpression.setCharacter(pExpression.getCharacter());
		ndExpression.setLineNumber(pExpression.getLineNumber());
		ndExpression.setType(pExpression.getType());
		for (ProbabilisticAlternativeValue pav : pExpression.getChoices()) {
			ndExpression.getChoices().add(pav.getValue());
		}
		NondetExpressionTranslator nondetExpressionTranslator = (NondetExpressionTranslator) StatementTranslatorContainer.getTranslator(NonDetExpression.class);
		String tailString = nondetExpressionTranslator.getNonDetTailString();
		nondetExpressionTranslator.setNonDetTailString("");
		String retValue = StatementTranslatorContainer.translate(ndExpression, tab);
		nondetExpressionTranslator.setNonDetTailString(nondetExpressionTranslator.getNonDetTailString() + tailString);
		retValue = "(" + assignProbabilities + ", " + retValue.substring(1);
		return retValue;
	}	
}