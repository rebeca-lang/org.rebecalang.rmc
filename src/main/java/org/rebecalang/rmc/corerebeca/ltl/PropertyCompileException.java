/*                          In the name of Allah                         */
/*                           The Best will come                          */

package org.rebecalang.rmc.corerebeca.ltl;

import org.rebecalang.compiler.utils.CodeCompilationException;

@SuppressWarnings("serial")
public class PropertyCompileException extends CodeCompilationException {
    public PropertyCompileException(String string) {
        super(string, -1, -1);
    }

    public PropertyCompileException(String message, int line, int column) {
        super(message, line, column);
    }
}