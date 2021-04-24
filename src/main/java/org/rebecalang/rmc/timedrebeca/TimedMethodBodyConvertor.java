package org.rebecalang.rmc.timedrebeca;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.MethodBodyConvertor;
import org.rebecalang.rmc.StatementTranslationException;

public class TimedMethodBodyConvertor extends MethodBodyConvertor {

	public TimedMethodBodyConvertor(Set<AnalysisFeature> analysisFeatures) {
		super(analysisFeatures);
	}

	public String convertConstructorBody(ReactiveClassDeclaration reactiveClassDeclaration,
			MethodDeclaration methodDeclaration) throws StatementTranslationException {
		String result = super.convertConstructorBody(reactiveClassDeclaration, methodDeclaration);
		result = result.substring(0, result.length() - ("return 0;" + NEW_LINE).length());
		result += "queueTail = 0;" + NEW_LINE + TAB + TAB + "while(messageQueue[queueTail]) {applyPolicy(false);queueTail++;}" + 
				NEW_LINE + TAB + TAB + "return 0;" + NEW_LINE;
		return result;
	}

}
