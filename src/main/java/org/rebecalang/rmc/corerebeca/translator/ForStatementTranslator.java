package org.rebecalang.rmc.corerebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ForStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslatorContainer;

public class ForStatementTranslator extends AbstractStatementTranslator {

	public ForStatementTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		ForStatement forStatement = (ForStatement) statement;
		String retValue = tab + "for (";
		if (forStatement.getForInitializer() != null) {
			if (forStatement.getForInitializer().getFieldDeclaration() != null) {
				retValue += StatementTranslatorContainer.translate(forStatement.getForInitializer()
						.getFieldDeclaration(), "");
			} else if (forStatement.getForInitializer().getExpressions() != null) {
				for (Expression expression : forStatement.getForInitializer()
						.getExpressions()) {
					retValue += StatementTranslatorContainer.translate(expression, "") + ", ";
				}
				if (forStatement.getForInitializer().getExpressions().size() != 0)
					retValue = retValue.substring(0, retValue.length() - 2);
				retValue += "; ";
			}
		} else
			retValue += "; ";
		
		if (forStatement.getCondition() != null) {
			retValue += "(" + StatementTranslatorContainer.translate(
					forStatement.getCondition(), "") + ")";
		}
		retValue += "; ";
		
		for (Expression expression : forStatement.getForIncrement()) {
			retValue += StatementTranslatorContainer.translate(expression, "") + ", ";
		}
		if (forStatement.getForIncrement().size() != 0)
			retValue = retValue.substring(0, retValue.length() - 2);

		retValue += ") {" + NEW_LINE;
		if (forStatement.getStatement() != null) {
			retValue += StatementTranslatorContainer.translate(forStatement.getStatement(), tab + TAB);
			retValue += super.adjustSemicolonForExpression(forStatement.getStatement());

		}
		retValue += "}";
		return retValue;
		
	}

}
