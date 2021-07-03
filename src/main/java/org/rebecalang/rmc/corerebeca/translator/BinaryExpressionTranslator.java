package org.rebecalang.rmc.corerebeca.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BinaryExpressionTranslator extends AbstractStatementTranslator {

	@Autowired
	public BinaryExpressionTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		super(statementTranslatorContainer);
	}

	@Override
	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		BinaryExpression bExpression = (BinaryExpression) statement;
		return tab + "(" + statementTranslatorContainer.translate(bExpression.getLeft(), "") +
				 bExpression.getOperator() + statementTranslatorContainer.translate(bExpression.getRight(), "") + ")";
	}

}
