package org.rebecalang.rmc;

import org.rebecalang.compiler.utils.CodeCompilationException;

@SuppressWarnings("serial")
public class StatementTranslationException extends CodeCompilationException {

	public StatementTranslationException(String message, int line, int column) {
		super(message, line, column);
	}
}
