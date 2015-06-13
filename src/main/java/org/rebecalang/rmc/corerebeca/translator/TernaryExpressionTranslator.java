package org.rebecalang.rmc.corerebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TernaryExpression;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslatorContainer;

public class TernaryExpressionTranslator extends AbstractStatementTranslator {

	public TernaryExpressionTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		TernaryExpression tExpression = (TernaryExpression) statement;
		return tab + " (" + StatementTranslatorContainer.translate(tExpression.getCondition(), "") +
				"? " + StatementTranslatorContainer.translate(tExpression.getLeft(), "") +
				": " + StatementTranslatorContainer.translate(tExpression.getRight(), "") + ")";
	}

}
