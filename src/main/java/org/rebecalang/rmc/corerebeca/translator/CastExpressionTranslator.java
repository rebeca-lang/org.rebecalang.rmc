package org.rebecalang.rmc.corerebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.CastExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.rebecalang.rmc.utils.TypesAnalysisUtilities;

public class CastExpressionTranslator extends AbstractStatementTranslator {

	public CastExpressionTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		CastExpression cExpression = (CastExpression) statement;
		if (TypesUtilities.getInstance().canTypeUpCastTo(cExpression.getType(), TypesUtilities.REACTIVE_CLASS_TYPE)) {
			return tab + "(dynamic_cast<" + TypesAnalysisUtilities.getTypeName(cExpression.getType()) + ">("
					+ StatementTranslatorContainer.translate(cExpression.getExpression(), "") + "))";
		}
		return tab + "((" + TypesAnalysisUtilities.getTypeName(cExpression.getType()) + ")"
				+ StatementTranslatorContainer.translate(cExpression.getExpression(), "") + ")";
	}

}
