package org.rebecalang.rmc.testcase;

import org.rebecalang.rmc.RMC;

import junit.framework.TestCase;


public class TimedRebecaTestCase extends TestCase {
	
	String base = "src/test/resources/org/rebecalang/rmc/testcase";
	
//	@Test 
	public void testDiningPhilosophers() {
		String[] parameters = new String[] {
				"--source", base + "/ticket-service-2c_c2notissued.rebeca",
//				"--source", base + "/TinyOSPV4.rebeca",
//				"--source", base + "/yarn-deadline-fifo-1jobs.rebeca",
//				"--source", base + "/ASPIN-modified-faulty.rebeca",
//				"--source", base + "/ticket-service-2c_c2notissued.rebeca",
//				"-e", "ProbabilisticTimedRebeca",
				"-e", "TimedRebeca",
				"-v", "2.1",
				"-o", "Jamal",
//				"-o", "Yarn",
//				"--tracegenerator", 
//				"-tts",
//				"-debug",
//				"-x", 
//				"-h",
		};
		
		try {
			RMC.main(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TimedRebecaTestCase testCase = new TimedRebecaTestCase();
		testCase.testDiningPhilosophers();
	}
}
