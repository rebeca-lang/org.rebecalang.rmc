package org.rebecalang.rmc.timedrebeca.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaLabelUtility;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.timedrebeca.TimedRebecaLabelUtility;
import org.rebecalang.compiler.modelcompiler.timedrebeca.objectmodel.TimedRebecaParentSuffixPrimary;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.rebecalang.rmc.corerebeca.translator.CoreRebecaTermPrimaryExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.FieldDeclarationStatementTranslator;
import org.rebecalang.rmc.timedrebeca.TimedRebecaFileGeneratorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TimedRebecaTermPrimaryExpressionTranslator extends CoreRebecaTermPrimaryExpressionTranslator {

	protected int numberOfDelays;
	
	@Autowired
	public TimedRebecaTermPrimaryExpressionTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		super(statementTranslatorContainer);
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
			if (((TimedRebecaFileGeneratorProperties)statementTranslatorContainer.getFileGenerationProperties()).isTTS()) {
				TimedRebecaNondetExpressionTranslator ndTranslator = (TimedRebecaNondetExpressionTranslator)
					statementTranslatorContainer.getTranslator(NonDetExpression.class);
				retValue += TAB + "__res = _ref_now + " + 
					statementTranslatorContainer.translate(termPrimary.getParentSuffixPrimary().getArguments().get(0), "") + ";" + NEW_LINE + 
					TAB + "__pc = " + numberOfDelays + ";" + NEW_LINE + TAB +
					"shift = 0;" + NEW_LINE + ndTranslator.getNonDetTailString() + TAB + "return 0;" + NEW_LINE +
					TAB + "__jumpPoint" + numberOfDelays + ":" + NEW_LINE;
				ndTranslator.setNonDetTailString("");
				FieldDeclarationStatementTranslator fdStatementTranslator = 
						(FieldDeclarationStatementTranslator) statementTranslatorContainer.getTranslator(FieldDeclaration.class);
				if (fdStatementTranslator.getInitialization() != null) {
					exceptionContainer.addException(new StatementTranslationException("You are not allowed to initialize local" +
							" variables while using delay and \"timed transition system\" analysis algorithm.",
							termPrimary.getLineNumber(), termPrimary.getCharacter()));
				}
				return retValue;
			} else {
				String delay = statementTranslatorContainer.translate(termPrimary.getParentSuffixPrimary().getArguments().get(0), "");
				return "currentDelay += " + delay + ";\n_ref_now += " + delay;
			}
		} else {
			if (termPrimary.getParentSuffixPrimary() == null &&
					termPrimary.getLabel() == CoreRebecaLabelUtility.LOCAL_VARIABLE &&
					((TimedRebecaFileGeneratorProperties)statementTranslatorContainer.getFileGenerationProperties()).isTTS() &&
					numberOfDelays > 0) {
				exceptionContainer.addWarning(new StatementTranslationException(
						"Value of local variables do not preserve after \"delay\" statement.",
						termPrimary.getLineNumber(), termPrimary.getCharacter()));
			}

			if (termPrimary.getParentSuffixPrimary() != null && 
					termPrimary.getLabel() == CoreRebecaLabelUtility.MSGSRV) {
				retValue += "_timed_msg_" + termPrimary.getName() + "(myID";
				for (Expression expression : termPrimary.getParentSuffixPrimary().getArguments()) {
					retValue += ", " + statementTranslatorContainer.translate(expression, "");
				}
				retValue += ", ";
				Expression afterExpression = ((TimedRebecaParentSuffixPrimary)termPrimary.getParentSuffixPrimary()).getAfterExpression();
				Expression deadlineExpression = ((TimedRebecaParentSuffixPrimary)termPrimary.getParentSuffixPrimary()).getDeadlineExpression();
				if (afterExpression != null) {
					retValue += statementTranslatorContainer.translate(afterExpression, "") + " + _ref_now, ";
				} else {
					retValue += "_ref_now, ";
				}
				if (deadlineExpression != null) {
					retValue += statementTranslatorContainer.translate(deadlineExpression, "") + " + _ref_now";
				} else {
					retValue += "MAX_TIME";
				}
				retValue += ", currentDelay";
				retValue = retValue + ");";
				retValue += NEW_LINE + "this->currentDelay = 0";
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
