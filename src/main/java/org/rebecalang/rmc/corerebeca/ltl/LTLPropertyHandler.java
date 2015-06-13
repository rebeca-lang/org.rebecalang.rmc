package org.rebecalang.rmc.corerebeca.ltl;

import java.util.HashMap;
import java.util.Map;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;

public class LTLPropertyHandler {

//	public Map<String, Graph> getLTLSpecification(
//			Map<String, Expression> ltlSpecs) {
//
//		Map<String, Graph> graphs = new HashMap<String, Graph>();
//
//		LTLGraphGenerator ltlGraphGenerator = new LTLGraphGenerator();
//		for (String name : ltlSpecs.keySet()) {
//			Expression ltlExpression = ltlSpecs.get(name);
//			String jpfSyntaxLTL = "!("
//					+ retrieveLTLSpecification(ltlExpression) + ")";
//			Graph graph = null;
//			try {
//				// Convert negation of LTL formula to BA by JPF engine!
//				graph = ltlGraphGenerator.generateClaimGraph(jpfSyntaxLTL);
//			} catch (org.rebecalang.rmc.corerebeca.ltl.buchiautomatahandler.trans.ParseErrorException e) {
//			}
//			graphs.put(name, graph);
//		}
//		return graphs;
//	}

	/**
	 * This method retrieves LTL formula from AST in style of JPF input formula.
	 * 
	 * @param expression
	 *            LTL expression root.
	 * @return Representation of LTL formula in JPF like style.
	 */
	private String retrieveLTLSpecification(Expression expression) {
		String retValue = "";
		/*if (expression.getOperator() != null) {
			if (expression.getOperator().getOp().equals("&&")) {
				retValue = "("
						+ retrieveLTLSpecification(expression.getLeft()
								.getExpression());
				retValue += " /\\ ";
				retValue += retrieveLTLSpecification(expression.getRight()
						.getExpression()) + ")";
			} else if (expression.getOperator().getOp().equals("||")) {
				retValue = "("
						+ retrieveLTLSpecification(expression.getLeft()
								.getExpression());
				retValue += " \\/ ";
				retValue += retrieveLTLSpecification(expression.getRight()
						.getExpression()) + ")";
			} else if (expression.getOperator().getOp().equals("U")) {
				retValue = "("
						+ retrieveLTLSpecification(expression.getLeft()
								.getExpression());
				retValue += " U ";
				retValue += retrieveLTLSpecification(expression.getRight()
						.getExpression()) + ")";
			} else if (expression.getOperator().getOp().equals("R")) {
				retValue = "("
						+ retrieveLTLSpecification(expression.getLeft()
								.getExpression());
				retValue += " R ";
				retValue += retrieveLTLSpecification(expression.getRight()
						.getExpression()) + ")";
			} else if (expression.getOperator().getOp().equals("->")) {
				retValue = "("
						+ retrieveLTLSpecification(expression.getLeft()
								.getExpression());
				retValue += " -> ";
				retValue += retrieveLTLSpecification(expression.getRight()
						.getExpression()) + ")";
			} else if (expression.getOperator().getOp().equals("F"))
				retValue = " <>("
						+ retrieveLTLSpecification(expression.getLeft()
								.getExpression()) + ") ";
			else if (expression.getOperator().getOp().equals("G"))
				retValue = " []("
						+ retrieveLTLSpecification(expression.getLeft()
								.getExpression()) + ") ";
			else if (expression.getOperator().getOp().equals("N"))
				retValue = " X("
						+ retrieveLTLSpecification(expression.getLeft()
								.getExpression()) + ") ";
			else if (expression.getOperator().getOp().equals("!"))
				retValue = " !("
						+ retrieveLTLSpecification(expression.getLeft()
								.getExpression()) + ") ";
			else
				;// throw new Prop
		} else {
			retValue = expression.getLeft().getPrimary().getCallSerial()
					.toLowerCase();
			if (!(retValue.equals("true") && retValue.equals("false")))
				retValue = "__pl__" + retValue;
		}
*/
		return retValue;
	}
}
