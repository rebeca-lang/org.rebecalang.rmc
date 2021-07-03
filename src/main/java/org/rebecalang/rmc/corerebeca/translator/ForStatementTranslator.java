package org.rebecalang.rmc.corerebeca.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ForStatement;
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
public class ForStatementTranslator extends AbstractStatementTranslator {

	@Autowired
	public ForStatementTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		super(statementTranslatorContainer);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		ForStatement forStatement = (ForStatement) statement;
		String retValue = tab + "for (";
		if (forStatement.getForInitializer() != null) {
			if (forStatement.getForInitializer().getFieldDeclaration() != null) {
				retValue += statementTranslatorContainer.translate(forStatement.getForInitializer()
						.getFieldDeclaration(), "");
			} else if (forStatement.getForInitializer().getExpressions() != null) {
				for (Expression expression : forStatement.getForInitializer()
						.getExpressions()) {
					retValue += statementTranslatorContainer.translate(expression, "") + ", ";
				}
				if (forStatement.getForInitializer().getExpressions().size() != 0)
					retValue = retValue.substring(0, retValue.length() - 2);
				retValue += "; ";
			}
		} else
			retValue += "; ";
		
		if (forStatement.getCondition() != null) {
			retValue += "(" + statementTranslatorContainer.translate(
					forStatement.getCondition(), "") + ")";
		}
		retValue += "; ";
		
		for (Expression expression : forStatement.getForIncrement()) {
			retValue += statementTranslatorContainer.translate(expression, "") + ", ";
		}
		if (forStatement.getForIncrement().size() != 0)
			retValue = retValue.substring(0, retValue.length() - 2);

		retValue += ") {" + NEW_LINE;
		if (forStatement.getStatement() != null) {
			retValue += statementTranslatorContainer.translate(forStatement.getStatement(), tab + TAB);
			retValue += super.adjustSemicolonForExpression(forStatement.getStatement());

		}
		retValue += "}";
		return retValue;
		
	}

}
