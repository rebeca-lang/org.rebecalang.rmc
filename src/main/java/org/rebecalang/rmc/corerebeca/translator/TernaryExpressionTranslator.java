package org.rebecalang.rmc.corerebeca.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TernaryExpression;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TernaryExpressionTranslator extends AbstractStatementTranslator {

	@Autowired
	public TernaryExpressionTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		super(statementTranslatorContainer);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		TernaryExpression tExpression = (TernaryExpression) statement;
		return tab + " (" + statementTranslatorContainer.translate(tExpression.getCondition(), "") +
				"? " + statementTranslatorContainer.translate(tExpression.getLeft(), "") +
				": " + statementTranslatorContainer.translate(tExpression.getRight(), "") + ")";
	}

}
