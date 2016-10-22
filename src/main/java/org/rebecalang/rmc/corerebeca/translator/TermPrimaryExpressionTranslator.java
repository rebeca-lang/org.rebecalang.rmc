package org.rebecalang.rmc.corerebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaLabelUtility;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ArrayType;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslatorContainer;

public class TermPrimaryExpressionTranslator extends AbstractStatementTranslator {

	public TermPrimaryExpressionTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		TermPrimary termPrimary = (TermPrimary) statement;
		String retValue = termPrimary.getName();
		if (termPrimary.getParentSuffixPrimary() != null) {
			boolean isMessageServer = termPrimary.getLabel() == CoreRebecaLabelUtility.MSGSRV;
			retValue = (isMessageServer ? "_msg_" : "_synchmethod_") + retValue;
			String params = "(";
			if (isMessageServer)
				params += "myID, ";
			for (Expression expression : termPrimary.getParentSuffixPrimary().getArguments()) {
				params += StatementTranslatorContainer.translate(expression, "") + ", ";
			}
			params = (params.length() == 1 ? params : params.substring(0, params.length() - 2)) + ")";
			retValue += params;
		} else {
			retValue = "_ref_" + retValue;
		}
		
		int indexCounter = 0;
		for (Expression expression : termPrimary.getIndices()) {
			if (aFeatures.contains(AnalysisFeature.SAFE_MODE)) {
				retValue += "[(arrayIndexChecker=" + StatementTranslatorContainer.translate(expression, "") + 
						", _synchmethod_assertion(arrayIndexChecker >= 0" +
						", string(\"Array index out of bound: \") + to_string((long)arrayIndexChecker)) " +
						", _synchmethod_assertion(arrayIndexChecker <" + ((ArrayType)termPrimary.getType()).getDimensions().get(indexCounter) +
						", string(\"Array index out of bound: \") + to_string((long)arrayIndexChecker)) " +
						", arrayIndexChecker)]";
			} else
				retValue += "[" + StatementTranslatorContainer.translate(expression, "") + "]";
			indexCounter++;
		}
		return tab + retValue;
	}

}
