package org.rebecalang.rmc.testcase;

import org.rebecalang.rmc.RMC;

import junit.framework.TestCase;


public class TimedRebecaTestCase extends TestCase {
	
	String base = "src/test/resources/org/rebecalang/rmc/testcase";
	
//	@Test 
	public void testDiningPhilosophers() {
		String[] parameters = new String[] {
//				"--source", base + "/ticket-service-2c_c2notissued.rebeca",
				"--source", base + "/NoC-xy44.rebeca",
//				"--source", base + "/yarn-deadline-fifo-1jobs.rebeca",
//				"--source", base + "/TinyOSPV6-TDMA.rebeca",
//				"--source", base + "/tcsma2.rebeca",
//				"-e", "ProbabilisticTimedRebeca",
				"-e", "TimedRebeca",
				"-v", "2.1",
				"-o", "NOC",
//				"-o", "Yarn",
//				"--tracegenerator", 
				"-tts",
//				"-debug",
//				"-debug2",
				"-x",
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
