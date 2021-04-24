package org.rebecalang.rmc.corerebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.AbstractStatementTranslator;

public class BreakStatementTranslator extends AbstractStatementTranslator {

	public BreakStatementTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		return tab + "break;" + NEW_LINE;
	}
}
