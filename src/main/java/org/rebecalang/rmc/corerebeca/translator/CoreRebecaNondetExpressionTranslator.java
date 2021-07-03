package org.rebecalang.rmc.corerebeca.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("CoreRebecaNondetExpressionTranslator")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CoreRebecaNondetExpressionTranslator extends AbstractStatementTranslator {

	@Autowired
	public CoreRebecaNondetExpressionTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		super(statementTranslatorContainer);
	}

	protected int nonDetCounter;
	protected String nonDetTailString;
	protected String nonDetHeadString;
	
	public void initialize() {
		nonDetCounter = 0;
		nonDetTailString = "";
		nonDetHeadString = "";
	}
	
	public boolean hasNonDetStatement() {
		return !nonDetHeadString.equals("");
	}
	public String getNonDetHeadString() {
		return nonDetHeadString;
	}
	
	public String getNonDetTailString() {
		return nonDetTailString;
	}
	
	public void setNonDetHeadString(String nonDetHeadString) {
		this.nonDetHeadString = nonDetHeadString;
	}
	
	public void setNonDetTailString(String nonDetTailString) {
		this.nonDetTailString = nonDetTailString;
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		NonDetExpression ndExpression = (NonDetExpression) statement;
		String retValue = tab;
		String nonDetVariableName = getNondetVariableName();
		if (statementTranslatorContainer.getFileGenerationProperties().isTraceGenerator()) {
			retValue = nondetResolverForTraceGenerator(ndExpression, nonDetVariableName);
		} else {
			int nondetId = ((int)Math.pow(2, nonDetCounter));
			retValue = "(__tNumber|=" + nondetId + ", " + nonDetVariableName + ")";

			prepareCodeForHeaader(ndExpression, nonDetVariableName);

			prepareCodeForTail(ndExpression, nonDetVariableName);

		}
		nonDetCounter++;
		return retValue;
	}

	protected String getNondetVariableName() {
		return "nonDetVariable" + nonDetCounter;
	}
	
	protected void prepareCodeForTail(NonDetExpression ndExpression,
			String nonDetVariableName) throws StatementTranslationException {
		
		int nondetId = ((int)Math.pow(2, nonDetCounter));
		String nonDetTailString = "";
		nonDetTailString += TAB + TAB + "if (__tNumber & " + nondetId + ") {" + NEW_LINE +
				TAB + TAB + TAB + 
				"if (false){/*Dummy 'if' statement to create nested else-if easily!*/}" + NEW_LINE;
		
		for (int cnt = 1; cnt < ndExpression.getChoices().size(); cnt++) {
			nonDetTailString += 
					TAB + TAB + TAB + "else if(" + nonDetVariableName + " == " + 
					statementTranslatorContainer.translate(ndExpression.getChoices().get(cnt - 1), "") + ") {" + NEW_LINE +
					TAB + TAB + TAB + TAB + nonDetVariableName + " = " + 
					statementTranslatorContainer.translate(ndExpression.getChoices().get(cnt), "") + ";" + NEW_LINE;
			if (cnt == ndExpression.getChoices().size())
				nonDetTailString += 
					TAB + TAB + TAB + TAB + "__tNumber &= ~" + nondetId + ";" + NEW_LINE;
			nonDetTailString += 
					TAB + TAB + TAB + "}" + NEW_LINE;
		}
		nonDetTailString += 
				TAB + TAB + TAB + "else if(" + nonDetVariableName + " == " + 
					statementTranslatorContainer.translate(ndExpression.getChoices().get(ndExpression.getChoices().size() - 1), "") +
					") {" + NEW_LINE +
				TAB + TAB + TAB + TAB + nonDetVariableName + " = " + 
					statementTranslatorContainer.translate(ndExpression.getChoices().get(0), "") + ";" + NEW_LINE +
				TAB + TAB + TAB + TAB + "__tNumber &= ~" + nondetId + ";" + NEW_LINE +
				TAB + TAB + TAB + "}" + NEW_LINE;

		nonDetTailString += 
				TAB + TAB + TAB + "if (__tNumber & " + nondetId + ") {" + NEW_LINE +
				TAB + TAB + TAB + TAB + "return __tNumber;" + NEW_LINE +
				TAB + TAB + TAB + "}" + NEW_LINE +
				TAB + TAB + "}" + NEW_LINE;
		this.nonDetTailString = nonDetTailString + this.nonDetTailString;
	}

	protected String nondetResolverForTraceGenerator(NonDetExpression ndExpression, String nonDetVariableName) throws StatementTranslationException {
		if (nonDetHeadString.isEmpty()) {
			nonDetHeadString = TAB + TAB + "long __tNumber = 0;" + NEW_LINE;
		}
		String retValue = "(__tNumber = randint(" + ndExpression.getChoices().size() + "), ";
		for (int cnt = 0; cnt < ndExpression.getChoices().size() - 1; cnt++) {
			Expression choice = ndExpression.getChoices().get(cnt);
			retValue += "__tNumber == " + cnt + " ? " + statementTranslatorContainer.translate(choice, "") + " : ";
		}
		retValue += statementTranslatorContainer.translate(ndExpression.getChoices().get(ndExpression.getChoices().size() - 1), "") + ")";
		return retValue;		
	}
	
	protected void prepareCodeForHeaader(NonDetExpression ndExpression, String nonDetVariableName) throws StatementTranslationException {
		if (nonDetHeadString.isEmpty()) {
			nonDetHeadString = TAB + TAB + "long __tNumber = 0;" + NEW_LINE;
		}
		nonDetHeadString += TAB + TAB + "static " + ndExpression.getType().getTypeName() + 
				" " + nonDetVariableName + " = " + 
				statementTranslatorContainer.translate(ndExpression.getChoices().get(0), "") + ";" + NEW_LINE;
	}

	public int getNonDetCounter() {
		return nonDetCounter;
	}

}
