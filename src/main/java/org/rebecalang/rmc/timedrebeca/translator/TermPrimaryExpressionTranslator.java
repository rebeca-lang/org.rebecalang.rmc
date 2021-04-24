package org.rebecalang.rmc.timedrebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaLabelUtility;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.timedrebeca.TimedRebecaLabelUtility;
import org.rebecalang.compiler.modelcompiler.timedrebeca.objectmodel.TimedRebecaParentSuffixPrimary;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.rebecalang.rmc.corerebeca.translator.FieldDeclarationStatementTranslator;

public class TermPrimaryExpressionTranslator extends org.rebecalang.rmc.corerebeca.translator.TermPrimaryExpressionTranslator {

	protected int numberOfDelays;
	
	public TermPrimaryExpressionTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}
	
	public void initialize() {
		numberOfDelays = -1;
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		String retValue = tab;
		TermPrimary termPrimary = (TermPrimary) statement;
		
		if (termPrimary.getParentSuffixPrimary() != null &&
				termPrimary.getLabel() == TimedRebecaLabelUtility.DELAY) {
			numberOfDelays++;
			if (aFeatures.contains(AnalysisFeature.TTS)) {
				NondetExpressionTranslator ndTranslator = (NondetExpressionTranslator)
					StatementTranslatorContainer.getTranslator(NonDetExpression.class);
				retValue += TAB + "__res = _ref_now + " + 
					StatementTranslatorContainer.translate(termPrimary.getParentSuffixPrimary().getArguments().get(0), "") + ";" + NEW_LINE + 
					TAB + "__pc = " + numberOfDelays + ";" + NEW_LINE + TAB +
					"shift = 0;" + NEW_LINE + ndTranslator.getNonDetTailString() + TAB + "return 0;" + NEW_LINE +
					TAB + "__jumpPoint" + numberOfDelays + ":" + NEW_LINE;
				ndTranslator.setNonDetTailString("");
				FieldDeclarationStatementTranslator fdStatementTranslator = 
						(FieldDeclarationStatementTranslator) StatementTranslatorContainer.getTranslator(FieldDeclaration.class);
				if (fdStatementTranslator.getInitialization() != null) {
					container.addException(new StatementTranslationException("You are not allowed to initialize local" +
							" variables while using delay and \"timed transition system\" analysis algorithm.",
							termPrimary.getLineNumber(), termPrimary.getCharacter()));
				}
				return retValue;
			} else
				return "_ref_now += " + 
				StatementTranslatorContainer.translate(termPrimary.getParentSuffixPrimary().getArguments().get(0), "");
		} else {
			if (termPrimary.getParentSuffixPrimary() == null &&
					termPrimary.getLabel() == CoreRebecaLabelUtility.LOCAL_VARIABLE &&
					aFeatures.contains(AnalysisFeature.TTS) &&
					numberOfDelays > 0) {
				container.addWarning(new StatementTranslationException(
						"Value of local variables do not preserve after \"delay\" statement.",
						termPrimary.getLineNumber(), termPrimary.getCharacter()));
			}

			if (termPrimary.getParentSuffixPrimary() != null && 
					termPrimary.getLabel() == CoreRebecaLabelUtility.MSGSRV) {
				retValue += "_timed_msg_" + termPrimary.getName() + "(myID";
				for (Expression expression : termPrimary.getParentSuffixPrimary().getArguments()) {
					retValue += ", " + StatementTranslatorContainer.translate(expression, "");
				}
				retValue += ", ";
				Expression afterExpression = ((TimedRebecaParentSuffixPrimary)termPrimary.getParentSuffixPrimary()).getAfterExpression();
				Expression deadlineExpression = ((TimedRebecaParentSuffixPrimary)termPrimary.getParentSuffixPrimary()).getDeadlineExpression();
				if (afterExpression != null) {
					retValue += StatementTranslatorContainer.translate(afterExpression, "") + " + _ref_now, ";
				} else {
					retValue += "_ref_now, ";
				}
				if (deadlineExpression != null) {
					retValue += StatementTranslatorContainer.translate(deadlineExpression, "") + " + _ref_now";
				} else {
					retValue += "MAX_TIME";
				}
				retValue = retValue + ")";
				return retValue;
			} else {
				return super.translate(termPrimary, tab);
			}
		}
	}

	public int getNumberOfDelays() {
		return numberOfDelays;
	}

}
