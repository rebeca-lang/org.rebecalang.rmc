package org.rebecalang.rmc.corerebeca.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.SwitchStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.SwitchStatementGroup;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SwitchStatementTranslator extends AbstractStatementTranslator {

	@Autowired
	public SwitchStatementTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		super(statementTranslatorContainer);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		SwitchStatement switchStatement = (SwitchStatement) statement;
		String retValue = tab + "switch (";
		retValue += statementTranslatorContainer.translate(
				switchStatement.getExpression(), "") + ") {" + NEW_LINE;
		
		for (SwitchStatementGroup sbsg : switchStatement
				.getSwitchStatementGroups()) {
			Expression switchLabel = sbsg.getExpression();
			if(switchLabel != null)
				retValue += tab + "case (" + statementTranslatorContainer.translate(switchLabel, "") + "):" + NEW_LINE;
			else
				retValue += tab + "default:" + NEW_LINE;
			for (Statement innerStatement : sbsg.getStatements()) {
				retValue += statementTranslatorContainer.translate(innerStatement, tab + TAB);
				retValue += super.adjustSemicolonForExpression(innerStatement);
			}
			retValue += ";";
		}

		return retValue + "}" + NEW_LINE;
	}

}
