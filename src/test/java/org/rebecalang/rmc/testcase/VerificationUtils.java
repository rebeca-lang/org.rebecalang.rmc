package org.rebecalang.rmc.testcase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.rebecalang.compiler.utils.ExceptionContainer;

public class VerificationUtils {

	static void assertExceptionContainerIsEmpty(ExceptionContainer exceptionContainer) {
		ExceptionContainer expectedExceptionContainer = new ExceptionContainer();
		
		assertEquals(exceptionContainer, expectedExceptionContainer);
	}
}
