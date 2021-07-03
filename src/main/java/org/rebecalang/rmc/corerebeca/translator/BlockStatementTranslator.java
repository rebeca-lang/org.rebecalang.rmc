package org.rebecalang.rmc.corerebeca.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
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
public class BlockStatementTranslator extends AbstractStatementTranslator {

	
	@Autowired BlockStatementTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		super(statementTranslatorContainer);
	}

	public String translate(Statement statement, String tab) throws StatementTranslationException {
		BlockStatement blockStatement = (BlockStatement) statement;
		String retValue = tab + "{" + NEW_LINE;
		for (Statement innerStatement : blockStatement.getStatements()) {
			retValue += statementTranslatorContainer.translate(innerStatement, tab + TAB);
			retValue += super.adjustSemicolonForExpression(innerStatement);
			retValue += NEW_LINE;
		}
		return retValue + tab + "}" + NEW_LINE;
	}
}
