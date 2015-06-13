package org.rebecalang.rmc.corerebeca.ltl;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.CastExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.PrimaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.UnaryExpression;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;

public class PropertyPredicateLogicExpressionTranslator { //extends CoreRebecaExpressionTranslator {

	public PropertyPredicateLogicExpressionTranslator(
			Set<CompilerFeature> cFeatures, Set<AnalysisFeature> aFeatures,
			ExceptionContainer container) {
		//super(cFeatures, aFeatures, container);
	}

	public String translate(Expression expression,
			ExceptionContainer container) {
		String retValue = "";

		if (expression instanceof BinaryExpression) {
			BinaryExpression bExpression = (BinaryExpression) expression;
			retValue = "(" + translate(bExpression.getLeft(), container) +
					 bExpression.getOperator() + translate(bExpression.getRight(), container) + ")";
		} else if (expression instanceof UnaryExpression) {
			UnaryExpression uExpression = (UnaryExpression) expression;
			retValue = "(" + uExpression.getOperator() + translate(uExpression.getExpression(), container) + ")";
		} else if (expression instanceof CastExpression) {
			CastExpression cExpression = (CastExpression) expression;
			retValue += "((" + TypesUtilities.getTypeName(cExpression.getType()) + ")"
					+ translate(cExpression.getExpression(), container) + ")";
		} else if (expression instanceof Literal) {
			Literal lExpression = (Literal) expression;
			retValue = lExpression.getLiteralValue();
		} else if (expression instanceof PrimaryExpression) {
			PrimaryExpression pExpression = (PrimaryExpression) expression;
//			retValue = translatePrimaryExpression(pExpression);
		} else {
			container.addException(new StatementTranslationException("Unknown translation rule for expression type " 
					+ expression.getClass(), expression.getLineNumber(), expression.getCharacter()));
		}
		return retValue;
	}

	protected String translatePrimaryTermExpression(TermPrimary termPrimary) {
//		String retValue = termPrimary.getName();
//		if (termPrimary.getParentSuffixPrimary() != null) {
//			retValue += "(myID";
//			for (Expression expression : termPrimary.getParentSuffixPrimary().getArguments()) {
//				retValue += ", " + translate(expression, container);
//			}
//			retValue += ")";
//			if (TypesUtilities.getInstance().canTypeCastTo(
//					termPrimary.getType(), TypesUtilities.MSGSRV_TYPE)) {
//				retValue = "_msg_" + retValue;
//			} else {
//				retValue = "_synchmethod_" + retValue;
//			}
//		} else {
//			retValue = "_ref_" + retValue;
//		}
//		for (Expression expression : termPrimary.getIndices())
//			retValue += "[" + translate(expression, container) + "]";
//		return retValue;
		return "";
	}
}
