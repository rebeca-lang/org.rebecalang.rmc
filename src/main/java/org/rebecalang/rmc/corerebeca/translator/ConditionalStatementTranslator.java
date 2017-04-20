package org.rebecalang.rmc.corerebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConditionalStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslatorContainer;

public class ConditionalStatementTranslator extends AbstractStatementTranslator {

	public ConditionalStatementTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}

	@Override
	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		ConditionalStatement conditionalStatement = (ConditionalStatement) statement;
		String retValue = tab + "if (";
		retValue += StatementTranslatorContainer.translate(
				conditionalStatement.getCondition(), "") + ") {" + NEW_LINE;
		if (conditionalStatement.getStatement() != null) {
			retValue += StatementTranslatorContainer.translate(conditionalStatement.getStatement(), tab + TAB);
			retValue += super.adjustSemicolonForExpression(conditionalStatement.getStatement()) + "}";
			if (conditionalStatement.getElseStatement() != null) {
				retValue += NEW_LINE + tab + "else {" + NEW_LINE + StatementTranslatorContainer.translate(
						conditionalStatement.getElseStatement(), tab + TAB) + "}";
				retValue += super.adjustSemicolonForExpression(conditionalStatement.getElseStatement());
			}
		}
		else
			retValue += ";";
		return retValue;
	}

}
