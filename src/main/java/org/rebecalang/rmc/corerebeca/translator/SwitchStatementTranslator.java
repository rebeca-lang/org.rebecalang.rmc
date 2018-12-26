package org.rebecalang.rmc.corerebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.SwitchStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.SwitchStatementGroup;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslatorContainer;

public class SwitchStatementTranslator extends AbstractStatementTranslator {

	public SwitchStatementTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		SwitchStatement switchStatement = (SwitchStatement) statement;
		String retValue = tab + "switch (";
		retValue += StatementTranslatorContainer.translate(
				switchStatement.getExpression(), "") + ") {" + NEW_LINE;
		
		for (SwitchStatementGroup sbsg : switchStatement
				.getSwitchStatementGroups()) {
			Expression switchLabel = sbsg.getExpression();
			if(switchLabel != null)
				retValue += tab + "case (" + StatementTranslatorContainer.translate(switchLabel, "") + "):" + NEW_LINE;
			else
				retValue += tab + "default:" + NEW_LINE;
			for (Statement innerStatement : sbsg.getStatements()) {
				retValue += StatementTranslatorContainer.translate(innerStatement, tab + TAB);
				retValue += super.adjustSemicolonForExpression(innerStatement);
			}
		}

		return retValue + "}" + NEW_LINE;
	}

}
