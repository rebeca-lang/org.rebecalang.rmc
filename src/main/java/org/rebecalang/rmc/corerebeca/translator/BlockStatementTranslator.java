package org.rebecalang.rmc.corerebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslatorContainer;

public class BlockStatementTranslator extends AbstractStatementTranslator {

	public BlockStatementTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}

	public String translate(Statement statement, String tab) throws StatementTranslationException {
		BlockStatement blockStatement = (BlockStatement) statement;
		String retValue = tab + "{" + NEW_LINE;
		for (Statement innerStatement : blockStatement.getStatements()) {
			retValue += StatementTranslatorContainer.translate(innerStatement, tab + TAB);
			retValue += super.adjustSemicolonForExpression(innerStatement);
			retValue += NEW_LINE;
		}
		return retValue + tab + "}" + NEW_LINE;
	}
}
