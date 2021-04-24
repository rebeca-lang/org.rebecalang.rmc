package org.rebecalang.rmc.corerebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.InstanceofExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslatorContainer;

public class InstanceofExpressionTranslator extends AbstractStatementTranslator {

	public InstanceofExpressionTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}

	@Override
	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		InstanceofExpression ioExpression = (InstanceofExpression) statement;
		return tab + "(dynamic_cast<" + TypesUtilities.getTypeName(ioExpression.getEvaluationType()) + 
				"Actor*>(" + StatementTranslatorContainer.translate(ioExpression.getValue(), "") + ") != null)";
	}

}
