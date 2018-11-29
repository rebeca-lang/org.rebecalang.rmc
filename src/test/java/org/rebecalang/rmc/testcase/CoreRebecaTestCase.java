package org.rebecalang.rmc.testcase;

import junit.framework.TestCase;

import org.rebecalang.rmc.RMC;


public class CoreRebecaTestCase extends TestCase {
	
	String base = "src/test/resources/org/rebecalang/rmc/testcase";
	
//	@Test 
	public void testDiningPhilosophers() {
		String[] parameters = new String[] {
				"-s", base + "/WhackAMoleP.rebeca",
//				"-s", base + "/phils.rebeca",
//				"-s", base + "/spanning-tree-protocol-best-port.rebeca",
				"-p", base + "/WhackAMoleP.property",
//				"-p", base + "/phils.property",
				"-e", "CoreRebeca",
				"-v", "2.1",
				"-o", "WhackAMoleP",
//				"-o", "phils",
				"-x",
//				"-debug2",
//				"-debug",
		};

		try {
			RMC.main(parameters);
		} catch (Exception e) {
			
		}
	}
	
	public static void main(String[] args){

//        PredicateFactory predicates = new PredicateFactory();
//        
//        IAutomataContext context = new AutomataContext();
//        
//        LtlParser parser = new LtlParser(context, predicates);
//        LtlNode parse = parser.parse("G(a)");
        
		CoreRebecaTestCase testCase = new CoreRebecaTestCase();
		testCase.testDiningPhilosophers();
	}
}
