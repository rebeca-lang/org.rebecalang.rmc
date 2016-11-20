package org.rebecalang.rmc.corerebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslatorContainer;

public class NondetExpressionTranslator extends AbstractStatementTranslator {

	public NondetExpressionTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}

	protected int nonDetCounter;
	protected String nonDetTailString;
	protected String nonDetHeadString;
	
	public void initialize() {
		nonDetCounter = 0;
		nonDetTailString = "";
		nonDetHeadString = "";
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
		String nonDetVariableName = "nonDetVariable" + nonDetCounter;
		if (aFeatures.contains(AnalysisFeature.TRACE_GENERATOR)) {
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
	
	protected void prepareCodeForTail(NonDetExpression ndExpression,
			String nonDetVariableName) throws StatementTranslationException {
		
		int nondetId = ((int)Math.pow(2, nonDetCounter));

		nonDetTailString += TAB + TAB + "if (__tNumber & " + nondetId + ") {" + NEW_LINE +
				TAB + TAB + TAB + 
				"if (false){/*Dummy 'if' statement to create nested else-if easily!*/}" + NEW_LINE;
		
		for (int cnt = 1; cnt < ndExpression.getChoices().size(); cnt++) {
			nonDetTailString += 
					TAB + TAB + TAB + "else if(" + nonDetVariableName + " == " + 
					StatementTranslatorContainer.translate(ndExpression.getChoices().get(cnt - 1), "") + ") {" + NEW_LINE +
					TAB + TAB + TAB + TAB + nonDetVariableName + " = " + 
					StatementTranslatorContainer.translate(ndExpression.getChoices().get(cnt), "") + ";" + NEW_LINE;
			if (cnt == ndExpression.getChoices().size())
				nonDetTailString += 
					TAB + TAB + TAB + TAB + "__tNumber &= ~" + nondetId + ";" + NEW_LINE;
			nonDetTailString += 
					TAB + TAB + TAB + "}" + NEW_LINE;
		}
		nonDetTailString += 
				TAB + TAB + TAB + "else if(" + nonDetVariableName + " == " + 
					StatementTranslatorContainer.translate(ndExpression.getChoices().get(ndExpression.getChoices().size() - 1), "") +
					") {" + NEW_LINE +
				TAB + TAB + TAB + TAB + nonDetVariableName + " = " + 
					StatementTranslatorContainer.translate(ndExpression.getChoices().get(0), "") + ";" + NEW_LINE +
				TAB + TAB + TAB + TAB + "__tNumber &= ~" + nondetId + ";" + NEW_LINE +
				TAB + TAB + TAB + "}" + NEW_LINE;

		nonDetTailString += 
				TAB + TAB + TAB + "if (__tNumber & " + nondetId + ") {" + NEW_LINE +
				TAB + TAB + TAB + TAB + "return __tNumber;" + NEW_LINE +
				TAB + TAB + TAB + "}" + NEW_LINE +
				TAB + TAB + "}" + NEW_LINE;
	}

	protected String nondetResolverForTraceGenerator(NonDetExpression ndExpression, String nonDetVariableName) throws StatementTranslationException {
		if (nonDetHeadString.isEmpty()) {
			nonDetHeadString = TAB + TAB + "long __tNumber = 0;" + NEW_LINE;
		}
		String retValue = "(__tNumber = randint(" + ndExpression.getChoices().size() + "), ";
		for (int cnt = 0; cnt < ndExpression.getChoices().size() - 1; cnt++) {
			Expression choice = ndExpression.getChoices().get(cnt);
			retValue += "__tNumber == " + cnt + " ? " + StatementTranslatorContainer.translate(choice, "") + " : ";
		}
		retValue += StatementTranslatorContainer.translate(ndExpression.getChoices().get(ndExpression.getChoices().size() - 1), "") + ")";
		return retValue;		
	}
	
	protected void prepareCodeForHeaader(NonDetExpression ndExpression, String nonDetVariableName) throws StatementTranslationException {
		if (nonDetHeadString.isEmpty()) {
			nonDetHeadString = TAB + TAB + "long __tNumber = 0;" + NEW_LINE;
		}
		nonDetHeadString += TAB + TAB + "static " + TypesUtilities.getTypeName(ndExpression.getType()) + 
				" " + nonDetVariableName + " = " + 
				StatementTranslatorContainer.translate(ndExpression.getChoices().get(0), "") + ";" + NEW_LINE;
	}

	public int getNonDetCounter() {
		return nonDetCounter;
	}

}
