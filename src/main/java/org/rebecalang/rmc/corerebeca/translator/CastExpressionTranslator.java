package org.rebecalang.rmc.corerebeca.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.CastExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.rebecalang.rmc.utils.TypesAnalysisUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CastExpressionTranslator extends AbstractStatementTranslator {

	@Autowired
	public CastExpressionTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		super(statementTranslatorContainer);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		CastExpression cExpression = (CastExpression) statement;
		if (cExpression.getType().canTypeUpCastTo(CoreRebecaTypeSystem.REACTIVE_CLASS_TYPE)) {
			return tab + "(dynamic_cast<" + TypesAnalysisUtilities.getTypeName(cExpression.getType()) + ">("
					+ statementTranslatorContainer.translate(cExpression.getExpression(), "") + "))";
		}
		return tab + "((" + TypesAnalysisUtilities.getTypeName(cExpression.getType()) + ")"
				+ statementTranslatorContainer.translate(cExpression.getExpression(), "") + ")";
	}

}
