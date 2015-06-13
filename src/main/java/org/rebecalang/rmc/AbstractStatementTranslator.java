package org.rebecalang.rmc;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;

public abstract class AbstractStatementTranslator {
	public final static String NEW_LINE = "\r\n";
	public final static String TAB = "\t";
	protected Set<AnalysisFeature> aFeatures;
	protected Set<CompilerFeature> cFeatures;
	
	protected ExceptionContainer container;

	public abstract String translate(Statement statement, String tab) throws StatementTranslationException;
	
	public void initialize() {
		
	}
	
	public AbstractStatementTranslator(Set<CompilerFeature> cFeatures, Set<AnalysisFeature> aFeatures) {
		this.cFeatures = cFeatures;
		this.aFeatures = aFeatures;
		container = new ExceptionContainer();
	}

	public void fillExceptionContainer(ExceptionContainer container) {
		container.addAll(this.container);
	}

	public String adjustSemicolonForExpression(Statement innerStatement) {
		return (innerStatement instanceof Expression) ? ";" : "";
	}

}
