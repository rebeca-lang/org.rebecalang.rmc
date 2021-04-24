package org.rebecalang.rmc.corerebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslatorContainer;

public class BinaryExpressionTranslator extends AbstractStatementTranslator {

	public BinaryExpressionTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}

	@Override
	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		BinaryExpression bExpression = (BinaryExpression) statement;
		return tab + "(" + StatementTranslatorContainer.translate(bExpression.getLeft(), "") +
				 bExpression.getOperator() + StatementTranslatorContainer.translate(bExpression.getRight(), "") + ")";
	}

}
