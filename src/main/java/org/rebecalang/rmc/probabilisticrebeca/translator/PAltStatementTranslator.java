package org.rebecalang.rmc.probabilisticrebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.AbstractStatementTranslator;

public class PAltStatementTranslator extends AbstractStatementTranslator {

	public PAltStatementTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		throw new StatementTranslationException("There is no code generation rule for \"PAlt\" statement.", 
				statement.getLineNumber(), statement.getCharacter());
	}

}
