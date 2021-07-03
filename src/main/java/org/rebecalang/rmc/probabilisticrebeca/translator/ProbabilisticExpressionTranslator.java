package org.rebecalang.rmc.probabilisticrebeca.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.probabilisticrebeca.objectmodel.ProbabilisticAlternativeValue;
import org.rebecalang.compiler.modelcompiler.probabilisticrebeca.objectmodel.ProbabilisticExpression;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.rebecalang.rmc.corerebeca.translator.CoreRebecaNondetExpressionTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProbabilisticExpressionTranslator extends AbstractStatementTranslator {

	@Autowired
	public ProbabilisticExpressionTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		super(statementTranslatorContainer);
	}

	@Override
	public String translate(Statement statement, String tab)
			throws StatementTranslationException {

		ProbabilisticExpression pExpression = (ProbabilisticExpression) statement;

		String assignProbabilities = "probability *= (";
		String nonDetVariableName = "nonDetVariable" + 
				((CoreRebecaNondetExpressionTranslator) statementTranslatorContainer.getTranslator(NonDetExpression.class)).getNonDetCounter();
		for (ProbabilisticAlternativeValue pav : pExpression.getChoices()) {
			assignProbabilities += nonDetVariableName + " == " + 
					statementTranslatorContainer.translate(pav.getValue(), "") + "? (" +
					statementTranslatorContainer.translate(pav.getProbability(), "") + ") : ";
		}
		assignProbabilities += " -1)";

		NonDetExpression ndExpression = new NonDetExpression();
		ndExpression.setCharacter(pExpression.getCharacter());
		ndExpression.setLineNumber(pExpression.getLineNumber());
		ndExpression.setType(pExpression.getType());
		for (ProbabilisticAlternativeValue pav : pExpression.getChoices()) {
			ndExpression.getChoices().add(pav.getValue());
		}
		CoreRebecaNondetExpressionTranslator nondetExpressionTranslator = (CoreRebecaNondetExpressionTranslator) statementTranslatorContainer.getTranslator(NonDetExpression.class);
		String tailString = nondetExpressionTranslator.getNonDetTailString();
		nondetExpressionTranslator.setNonDetTailString("");
		String retValue = statementTranslatorContainer.translate(ndExpression, tab);
		nondetExpressionTranslator.setNonDetTailString(nondetExpressionTranslator.getNonDetTailString() + tailString);
		retValue = "(" + assignProbabilities + ", " + retValue.substring(1);
		return retValue;
	}	
}