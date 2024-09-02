package org.rebecalang.rmc.corerebeca.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReturnStatement;
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
public class ReturnStatementTranslator extends AbstractStatementTranslator {

	@Autowired
	public ReturnStatementTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		super(statementTranslatorContainer);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		String result = tab + "return "; 
		Expression returnValueExpression = ((ReturnStatement)statement).getExpression();
		if(returnValueExpression != null)
			result += statementTranslatorContainer.translate(returnValueExpression, "");
		result += ";" + NEW_LINE;
		return result;
	}
}
