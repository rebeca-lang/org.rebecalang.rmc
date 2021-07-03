package org.rebecalang.rmc.corerebeca.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaLabelUtility;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ArrayType;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("CoreRebecaTermPrimaryExpressionTranslator")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CoreRebecaTermPrimaryExpressionTranslator extends AbstractStatementTranslator {

	@Autowired
	public CoreRebecaTermPrimaryExpressionTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		super(statementTranslatorContainer);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		TermPrimary termPrimary = (TermPrimary) statement;
		String retValue = termPrimary.getName();
		if (termPrimary.getParentSuffixPrimary() != null) {
			boolean isMessageServer = termPrimary.getLabel() == CoreRebecaLabelUtility.MSGSRV;
			boolean isBuiltIn = termPrimary.getLabel() == CoreRebecaLabelUtility.BUILT_IN_METHOD;
			if (isMessageServer)
				retValue = "_msg_" + retValue;
			else if (!isBuiltIn)
				retValue = "_synchmethod_" + retValue;
			else if (isBuiltIn && retValue.equals("get"))
				retValue = "operator[]";
			String params = "(";
			if (isMessageServer)
				params += "myID, ";
			for (Expression expression : termPrimary.getParentSuffixPrimary().getArguments()) {
				params += statementTranslatorContainer.translate(expression, "") + ", ";
			}
			params = (params.length() == 1 ? params : params.substring(0, params.length() - 2)) + ")";
			retValue += params;
		} else {
			retValue = "_ref_" + retValue;
		}
		
		int indexCounter = 0;
		for (Expression expression : termPrimary.getIndices()) {
			if (statementTranslatorContainer.isSafeMode()) {
				retValue += "[(arrayIndexChecker=" + statementTranslatorContainer.translate(expression, "") + 
						", assertion(arrayIndexChecker >= 0 && arrayIndexChecker <" +
						((ArrayType)termPrimary.getType()).getDimensions().get(indexCounter) +
						", string(\"Array index out of bound: \") + to_string((long long)arrayIndexChecker)" +
						" + \", method \\\"\" + reactiveClassName + \".\" + methodName + \"\\\" line " +
						expression.getLineNumber() +  "\") " +
						", arrayIndexChecker)]";
			} else
				retValue += "[" + statementTranslatorContainer.translate(expression, "") + "]";
			indexCounter++;
		}
		return tab + retValue;
	}

}
