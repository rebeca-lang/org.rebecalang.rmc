package org.rebecalang.rmc.corerebeca.ltl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.PrimaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.UnaryExpression;
import org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.graph.Graph;

import owl.automaton.Automaton;
import owl.automaton.acceptance.BuchiAcceptance;
import owl.ltl.LabelledFormula;
import owl.ltl.parser.LtlParser;
import owl.run.Environment;
import owl.translations.modules.LTL2NBAModule;

public class LTLTransformer {

	public Graph ltl2BA(UnaryExpression theNegationOfFormula) {
		String convert2String;
		try {
			convert2String = convert2String(theNegationOfFormula);
			LabelledFormula parse = LtlParser.parse(convert2String);
//			Translator translator = new Translator("nba.symmetric",
//					environment -> LTL2NBAModule.translation(environment, false));
//			Function<LabelledFormula, ? extends Automaton<?, ?>> apply = translator.constructor.apply(Environment.standard());
//			Automaton<?, ?> automaton = apply.apply(parse);
			Automaton<?, BuchiAcceptance> automaton = LTL2NBAModule.translation(Environment.standard(), false).apply(parse);
			Set<?> states = automaton.states();
			System.out.println(states.iterator().next().getClass());
			System.out.println(states);
//			for(Object state : states) {
//				automaton.edges(states.iterator().next());
//				for(Edge<Object> edge : automaton.edges(state)) {
//					
//				}
//				
//			}
//			System.out.println(((Either)states.toArray()[0]).right());
			
			System.out.println(parse);
			
		} catch (PropertyCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public String convert2String(Expression expression) throws PropertyCompileException {
		if (expression instanceof BinaryExpression) {
			BinaryExpression binaryExpression = (BinaryExpression) expression;
			return "(" +
					convert2String(binaryExpression.getLeft()) + ")" + 
					binaryExpression.getOperator() + "(" + 
					convert2String(binaryExpression.getRight()) + ")";
		} else if (expression instanceof UnaryExpression) {
			UnaryExpression unaryExpression = (UnaryExpression) expression;
			return unaryExpression.getOperator()+ "(" +
					convert2String(unaryExpression.getExpression()) + ")";
		} else if (expression instanceof TermPrimary) {
			TermPrimary termPrimary = (TermPrimary) expression;
			if (termPrimary.getParentSuffixPrimary() != null) {
				if (termPrimary.getName().equals("U")) {
					return termPrimary.getName() + " (" +
							convert2String(termPrimary.getParentSuffixPrimary().getArguments().get(0))
							+ ") U (" +
							convert2String(termPrimary.getParentSuffixPrimary().getArguments().get(1))
							+ ")";
				} else {
					return termPrimary.getName() + " (" +
							convert2String(termPrimary.getParentSuffixPrimary().getArguments().get(0))
							+ ")";
				}
			} else if (expression instanceof PrimaryExpression) {
			    return retreiveName(termPrimary);
			} else {
				throw new PropertyCompileException("Unknown expression " + expression);
			}
		} else if (expression instanceof Literal) {
			Literal literal = (Literal) expression;
			return literal.getLiteralValue().toUpperCase();
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

	static class Translator {
		final String name;
		final Function<Environment, ? extends Function<LabelledFormula, ? extends Automaton<?, ?>>> constructor;
		private final Set<FormulaSet> selectedSets;

		Translator(String name, BiFunction<Environment, LabelledFormula, ? extends Automaton<?, ?>> constructor) {
			this(name, x -> y -> constructor.apply(x, y));
		}

		Translator(String name,
				Function<Environment, ? extends Function<LabelledFormula, ? extends Automaton<?, ?>>> constructor) {
			this(name, constructor, Set.of());
		}

		Translator(String name,
				Function<Environment, ? extends Function<LabelledFormula, ? extends Automaton<?, ?>>> constructor,
				Collection<FormulaSet> blacklistedSets) {
			this.name = name;
			this.constructor = constructor;
			this.selectedSets = EnumSet.allOf(FormulaSet.class);
			this.selectedSets.remove(FormulaSet.PARAMETRISED_HARDNESS);
			this.selectedSets.removeAll(blacklistedSets);
		}

		Path referenceFile() {
			return Paths.get(String.format("%s/sizes/%s.json", BASE_PATH, name));
		}

		@Override
		public String toString() {
			return name;
		}
	}
	private static final String BASE_PATH = "data/formulas";

	public enum FormulaSet {
		BASE("base"), CHECK("check"), FGGF("fggf"), FGX("fgx"), REGRESSIONS("regressions"), SIZE_FGGF("size-fggf"),
		SIZE("size"),

		// Literature Patterns
		DWYER("literature/DwyerAC98"), 
		ETESSAMI("literature/EtessamiH00"), 
		LIBEROUTER("literature/Liberouter04"),
		PARAMETRISED("literature/Parametrised"), 
		PARAMETRISED_HARDNESS("literature/Parametrised-Hardness"),
		PELANEK("literature/Pelanek07"), SICKERT("literature/SickertEJK16"), SOMENZI("literature/SomenziB00");

		final String path;

		FormulaSet(String path) {
			this.path = path;
		}

		Path file() {
			return Paths.get(String.format("%s/%s.ltl", BASE_PATH, path));
		}
	}
}
