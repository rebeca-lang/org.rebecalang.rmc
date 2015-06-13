package org.rebecalang.rmc.corerebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.WhileStatement;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslatorContainer;

public class WhileStatementTranslator extends AbstractStatementTranslator {

	public WhileStatementTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		WhileStatement whileStatement = (WhileStatement) statement; 
		String retValue = tab + "while (";
		retValue += StatementTranslatorContainer.translate(
				whileStatement.getCondition(), "") + ")" + NEW_LINE;
		if (whileStatement.getStatement() != null) {
			retValue += StatementTranslatorContainer.translate(whileStatement.getStatement(), tab + TAB);
			retValue += super.adjustSemicolonForExpression(whileStatement.getStatement());
		} else
			retValue += ";";
		return retValue;
	}
}
