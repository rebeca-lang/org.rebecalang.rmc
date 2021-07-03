package org.rebecalang.rmc;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractStatementTranslator {
	public final static String NEW_LINE = "\r\n";
	public final static String TAB = "\t";
//	protected Set<AnalysisFeature> aFeatures;
//	protected Set<CompilerFeature> cFeatures;
	
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
	
//	public AbstractStatementTranslator(Set<CompilerFeature> cFeatures, Set<AnalysisFeature> aFeatures) {
//		this.cFeatures = cFeatures;
//		this.aFeatures = aFeatures;
//		exceptionContainer = new ExceptionContainer();
//	}

	public void fillExceptionContainer(ExceptionContainer container) {
		container.addAll(this.exceptionContainer);
	}

	public String adjustSemicolonForExpression(Statement innerStatement) {
		return (innerStatement instanceof Expression) ? ";" : "";
	}

//	public boolean removeAnalysisFeature(AnalysisFeature feature) {
//		return aFeatures.remove(feature);
//	}
//
//	public void addAnalysisFeature(AnalysisFeature feature) {
//		aFeatures.add(feature);
//		
//	}
}
