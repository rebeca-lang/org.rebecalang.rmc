package org.rebecalang.rmc.corerebeca.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConditionalStatement;
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
public class ConditionalStatementTranslator extends AbstractStatementTranslator {

	@Autowired
	public ConditionalStatementTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		super(statementTranslatorContainer);
	}

	@Override
	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		ConditionalStatement conditionalStatement = (ConditionalStatement) statement;
		String retValue = tab + "if (";
		retValue += statementTranslatorContainer.translate(
				conditionalStatement.getCondition(), "") + ") {" + NEW_LINE;
		if (conditionalStatement.getStatement() != null) {
			retValue += statementTranslatorContainer.translate(conditionalStatement.getStatement(), tab + TAB);
			retValue += super.adjustSemicolonForExpression(conditionalStatement.getStatement()) + "}";
			if (conditionalStatement.getElseStatement() != null) {
				retValue += NEW_LINE + tab + "else {" + NEW_LINE + statementTranslatorContainer.translate(
						conditionalStatement.getElseStatement(), tab + TAB);
				retValue += super.adjustSemicolonForExpression(conditionalStatement.getElseStatement()) + "}";
			}
		}
		else
			retValue += ";";
		return retValue;
	}

}
