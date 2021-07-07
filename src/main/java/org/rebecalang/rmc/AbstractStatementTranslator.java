package org.rebecalang.rmc;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractStatementTranslator {
	public final static String NEW_LINE = "\r\n";
	public final static String TAB = "\t";
	
	@Autowired
	protected ExceptionContainer exceptionContainer;
	
	protected StatementTranslatorContainer statementTranslatorContainer;
	
	@Autowired
	public AbstractStatementTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		this.statementTranslatorContainer = statementTranslatorContainer;
	}

	public abstract String translate(Statement statement, String tab) throws StatementTranslationException;
	
	public void initialize() {
		
	}
	
	public String adjustSemicolonForExpression(Statement innerStatement) {
		return (innerStatement instanceof Expression) ? ";" : "";
	}
}
