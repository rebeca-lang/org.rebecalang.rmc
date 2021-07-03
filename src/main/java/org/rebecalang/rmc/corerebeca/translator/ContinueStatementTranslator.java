package org.rebecalang.rmc.corerebeca.translator;

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
public class ContinueStatementTranslator extends AbstractStatementTranslator {

	@Autowired
	public ContinueStatementTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		super(statementTranslatorContainer);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		return tab + "continue;" + NEW_LINE;
	}
}
