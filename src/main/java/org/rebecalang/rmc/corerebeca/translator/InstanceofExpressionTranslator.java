package org.rebecalang.rmc.corerebeca.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.InstanceofExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InstanceofExpressionTranslator extends AbstractStatementTranslator {

	@Autowired
	public InstanceofExpressionTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		super(statementTranslatorContainer);
	}

	@Override
	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		InstanceofExpression ioExpression = (InstanceofExpression) statement;
		return tab + "(dynamic_cast<" +ioExpression.getEvaluationType().getTypeName() + 
				"Actor*>(" + statementTranslatorContainer.translate(ioExpression.getValue(), "") + ") != null)";
	}

}
