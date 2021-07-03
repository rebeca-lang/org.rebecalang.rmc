package org.rebecalang.rmc.corerebeca.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.WhileStatement;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WhileStatementTranslator extends AbstractStatementTranslator {

	@Autowired
	public WhileStatementTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		super(statementTranslatorContainer);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		WhileStatement whileStatement = (WhileStatement) statement; 
		String retValue = tab + "while (";
		retValue += statementTranslatorContainer.translate(
				whileStatement.getCondition(), "") + ") {" + NEW_LINE;
		if (whileStatement.getStatement() != null) {
			retValue += statementTranslatorContainer.translate(whileStatement.getStatement(), tab + TAB);
			retValue += super.adjustSemicolonForExpression(whileStatement.getStatement());
		}
		retValue += "}";
		return retValue;
	}
}
