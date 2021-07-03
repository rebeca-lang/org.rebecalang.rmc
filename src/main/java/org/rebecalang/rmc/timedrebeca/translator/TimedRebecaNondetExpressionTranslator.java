package org.rebecalang.rmc.timedrebeca.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.rebecalang.rmc.corerebeca.translator.CoreRebecaNondetExpressionTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TimedRebecaNondetExpressionTranslator extends CoreRebecaNondetExpressionTranslator {
	
	@Autowired
	public TimedRebecaNondetExpressionTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		super(statementTranslatorContainer);
	}

	public String getNonDetHeadString() {
		String retValue = super.getNonDetHeadString() + NEW_LINE + TAB + "#ifdef TTS" + NEW_LINE;
		int numberOfDelays = ((TimedRebecaTermPrimaryExpressionTranslator) 
				statementTranslatorContainer.getTranslator(TermPrimary.class)).getNumberOfDelays();
		for (int cnt = 0; cnt <= numberOfDelays; cnt++) {
			retValue += TAB + TAB + 
						"if (__pc == " + cnt + ") {__pc = -1; goto __jumpPoint" + cnt + ";}" + NEW_LINE;
		}
		retValue += TAB + "#endif" + NEW_LINE;
		return retValue;
	}
	
}
