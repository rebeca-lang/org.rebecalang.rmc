package org.rebecalang.rmc.corerebeca.ltl;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.PrimaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.UnaryExpression;
import org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.graph.Degeneralize;
import org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.graph.Edge;
import org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.graph.Graph;
import org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.graph.SCCReduction;
import org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.graph.SFSReduction;
import org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.graph.Simplify;
import org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.graph.SuperSetReduction;
import org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.trans.Formula;
import org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.trans.Node;
import org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.trans.ParseErrorException;
import org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.trans.Pool;
import org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.trans.Rewriter;
import org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.trans.Translator;

public class LTLPropertyHandler {

	private Formula convert2Formula(Expression expression)
			throws PropertyCompileException {
		char operator;
		if (expression instanceof BinaryExpression) {
			BinaryExpression binaryExpression = (BinaryExpression) expression;
			if (binaryExpression.getOperator().equals("||"))
				operator = 'O';
			else if (binaryExpression.getOperator().equals("&&"))
				operator = 'A';
			else if (binaryExpression.getOperator().equals("->")) {
				operator = 'O';
				binaryExpression.setOperator("\\/");
				UnaryExpression notExpression = new UnaryExpression();
				notExpression.setOperator("!");
				notExpression.setExpression(binaryExpression.getLeft());
				binaryExpression.setLeft(notExpression);
				notExpression.setLineNumber(binaryExpression.getLeft()
						.getLineNumber());
				notExpression.setCharacter(binaryExpression.getLeft()
						.getCharacter());
			} else {
				throw new PropertyCompileException("Unknown binary operator "
						+ binaryExpression.getOperator());
			}
			return unique(new Formula(operator, false,
					convert2Formula(binaryExpression.getLeft()),
					convert2Formula(binaryExpression.getRight()), null));
		} else if (expression instanceof UnaryExpression) {
			UnaryExpression unaryExpression = (UnaryExpression) expression;
			if (unaryExpression.getOperator().equals("!"))
				operator = 'N';
			else {
				throw new PropertyCompileException("Unknown unary operator "
						+ unaryExpression.getOperator());
			}
			return unique(new Formula(operator, true,
					convert2Formula(unaryExpression.getExpression()), null,
					null));
		} else if (expression instanceof TermPrimary) {
			TermPrimary termPrimary = (TermPrimary) expression;
			if (termPrimary.getParentSuffixPrimary() != null) {
				if (termPrimary.getName().equals("G")) {
					return unique(new Formula('V', false, False(),
							convert2Formula(termPrimary
									.getParentSuffixPrimary().getArguments()
									.get(0)), null));
				} else if (termPrimary.getName().equals("F")) {
					return unique(new Formula('U', false, True(),
							convert2Formula(termPrimary
									.getParentSuffixPrimary().getArguments()
									.get(0)), null));
				} else if (termPrimary.getName().equals("U")) {
					return unique(new Formula('U', false, convert2Formula(termPrimary
							.getParentSuffixPrimary().getArguments().get(0)),
							convert2Formula(termPrimary
									.getParentSuffixPrimary().getArguments()
									.get(1)), null));
				} else if (termPrimary.getName().equals("W")) {
					return unique(new Formula('W', false, convert2Formula(termPrimary
							.getParentSuffixPrimary().getArguments().get(0)),
							convert2Formula(termPrimary
									.getParentSuffixPrimary().getArguments()
									.get(1)), null));
				} else if (termPrimary.getName().equals("X")) {
					return unique(new Formula('X', false, convert2Formula(termPrimary
							.getParentSuffixPrimary().getArguments().get(0)),
							null, null));
				}
			} else if (expression instanceof PrimaryExpression) {
			    return unique(new Formula('p', true, null, null, retreiveName(termPrimary)));
			} else {
				throw new PropertyCompileException("Unknown expression " + expression);
			}
		}
		throw new PropertyCompileException("Unknown operator");
	}

	private String retreiveName(Expression expression) {
		String retValue = "";
		if (expression instanceof DotPrimary) {
			DotPrimary dotPrimary = (DotPrimary) expression;
			retValue += retreiveName(dotPrimary.getLeft()) + "." + retreiveName(dotPrimary.getRight());
		} else if (expression instanceof TermPrimary) {
			TermPrimary termPrimary = (TermPrimary) expression;
			retValue += "_ref_" + termPrimary.getName();
			for (Expression index : termPrimary.getIndices())
				retValue += "[" + retreiveName(index) + "]";
		}
		return retValue;
	}

	private static Formula False() {
		return new Formula('f', true, null, null, null);
	}

	private static Formula True() {
		return new Formula('t', true, null, null, null);
	}

	Hashtable<String, Formula> terms = new Hashtable<String, Formula>();
	private Formula unique(Formula f) {
		String s = f.toString();
		if (terms.containsKey(s)) {
			return terms.get(s);
		}
		terms.put(s, f);
		return f;
	}

	public Graph ltl2BA(Expression expression) {
		Formula ltlFormula = null;
		try {
			ltlFormula = convert2Formula(expression);
			ltlFormula = Rewriter.rewrite(ltlFormula);
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (PropertyCompileException e) {
			e.printStackTrace();
		}
		Translator.set_algorithm(Translator.LTL2BUCHI);
		Graph gba = Translator.translate(ltlFormula);
		gba = SuperSetReduction.reduce(gba);
		Graph ba = Degeneralize.degeneralize(gba);
		ba = SCCReduction.reduce(ba);
		ba = Simplify.simplify(ba);
		ba = SFSReduction.reduce(ba);

		Node.reset_static();
		Formula.reset_static();
		Pool.reset_static();
		return ba;
	}

	public static String exportGraph(Graph graph) {
		StringBuffer result = new StringBuffer("\n");
		LinkedList<org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.graph.Node> openBorder = 
				new LinkedList<org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.graph.Node>();
		Set<org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.graph.Node> visited = 
				new HashSet<org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.graph.Node>();
		openBorder.add(graph.getInit());
		visited.add(graph.getInit());
		while(!openBorder.isEmpty()) {
			org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.graph.Node node = openBorder.removeFirst();
			if (node.getBooleanAttribute("accepting"))
				result.insert(0, node.getId() + ", ");
			for (Edge next : node.getOutgoingEdges()) {
				result.append(node.getId() + "->" + next.getNext().getId() + "[" +
						next.getAction() + ", " + next.getGuard() + "]\n");
				if (!visited.contains(next.getNext())) {
					visited.add(next.getNext());
					openBorder.addLast(next.getNext());
				}
			}
		}
		
		return result.toString();
	}
}
