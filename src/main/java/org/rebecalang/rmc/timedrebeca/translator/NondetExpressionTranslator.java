package org.rebecalang.rmc.timedrebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslatorContainer;

public class NondetExpressionTranslator extends org.rebecalang.rmc.corerebeca.translator.NondetExpressionTranslator {
	
	public NondetExpressionTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}

	public String getNonDetHeadString() {
		String retValue = super.getNonDetHeadString() + NEW_LINE + TAB + "#ifdef TTS" + NEW_LINE;
		int numberOfDelays = ((TermPrimaryExpressionTranslator) 
				StatementTranslatorContainer.getTranslator(TermPrimary.class)).getNumberOfDelays();
		for (int cnt = 0; cnt <= numberOfDelays; cnt++) {
			retValue += TAB + TAB + 
						"if (__pc == " + cnt + ") {__pc = -1; goto __jumpPoint" + cnt + ";}" + NEW_LINE;
		}
		retValue += TAB + "#endif" + NEW_LINE;
		return retValue;
	}
	
}
