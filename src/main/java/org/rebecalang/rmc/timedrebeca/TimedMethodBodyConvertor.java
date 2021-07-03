package org.rebecalang.rmc.timedrebeca;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.corerebeca.CoreRebecaMethodBodyConvertor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Qualifier("TIMED_REBECA")
public class TimedMethodBodyConvertor extends CoreRebecaMethodBodyConvertor {

//	public TimedMethodBodyConvertor(StatementTranslatorContainer statementTranslatorContainer) {
//		super(statementTranslatorContainer);	
//	}

	public String convertConstructorBody(ReactiveClassDeclaration reactiveClassDeclaration,
			MethodDeclaration methodDeclaration) throws StatementTranslationException {
		String result = super.convertConstructorBody(reactiveClassDeclaration, methodDeclaration);
		result = result.substring(0, result.length() - ("return 0;" + NEW_LINE).length());
		result += "queueTail = 0;" + NEW_LINE + TAB + TAB + "while(messageQueue[queueTail]) {applyPolicy(false);queueTail++;}" + 
				NEW_LINE + TAB + TAB + "return 0;" + NEW_LINE;
		return result;
	}

}
