package org.rebecalang.rmc.testcase;

import org.rebecalang.rmc.RMC;

import junit.framework.TestCase;
import ru.ifmo.automata.statemashine.IAutomataContext;
import ru.ifmo.automata.statemashine.impl.AutomataContext;
import ru.ifmo.ltl.LtlParseException;
import ru.ifmo.ltl.converter.*;
import ru.ifmo.ltl.grammar.LtlNode;
import ru.ifmo.ltl.grammar.predicate.PredicateFactory;

public class CoreRebecaTestCase extends TestCase {
	
	String base = "src/test/resources/org/rebecalang/rmc/testcase";
	
//	@Test 
	public void testDiningPhilosophers() {
		String[] parameters = new String[] {
				"-s", base + "/phils.rebeca",
				"-e", "CoreRebeca",
				"-v", "2.1",
				"-o", "test",
		};
		try {
		RMC.main(parameters);
		} catch (Exception e) {
			
		}
	}
	
	public static void main(String[] args) throws LtlParseException {

        PredicateFactory predicates = new PredicateFactory();
        
        IAutomataContext context = new AutomataContext();
        
        LtlParser parser = new LtlParser(context, predicates);
        LtlNode parse = parser.parse("G(a)");
        
		CoreRebecaTestCase testCase = new CoreRebecaTestCase();
		testCase.testDiningPhilosophers();
	}
}
