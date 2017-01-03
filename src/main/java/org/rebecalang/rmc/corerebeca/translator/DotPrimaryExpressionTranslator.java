package org.rebecalang.rmc.corerebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ArrayType;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.rebecalang.rmc.utils.TypesAnalysisUtilities;

public class DotPrimaryExpressionTranslator extends AbstractStatementTranslator {
	
	int tempCounter = 0;
	private String safeModeBeforeUsageDefinitions = "";
	
	public DotPrimaryExpressionTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		this.tempCounter = 0;
		this.safeModeBeforeUsageDefinitions = "";
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		String retValue = tab;
		DotPrimary dotPrimary = (DotPrimary) statement;
		retValue = StatementTranslatorContainer.translate(dotPrimary.getLeft(), tab);
		Type baseType = dotPrimary.getLeft().getType();
		if (baseType instanceof ArrayType) {
			baseType = ((ArrayType)baseType).getPrimitiveType();
		}
		if (TypesUtilities.getInstance().canTypeCastTo(
				baseType, TypesUtilities.REACTIVE_CLASS_TYPE)) {
			if (aFeatures.contains(AnalysisFeature.SAFE_MODE)) {
				String tempVariable = "temp" + (tempCounter++);
				setSafeModeBeforeUsageDefinitions(getSafeModeBeforeUsageDefinitions()
						+ TypesUtilities.getTypeName(TypesAnalysisUtilities.getBaseType(
								dotPrimary.getLeft().getType())) + "Actor *" + tempVariable + ";");
				retValue = tab + "(" +  tempVariable + "=" + retValue.trim();
				retValue += ", _synchmethod_assertion(" + tempVariable + "!= null, "
						+ "\"Null Pointer Exception in method \" + reactiveClassName + " +
						"\".\" + methodName + \"line " +
						dotPrimary.getLineNumber() +
						"\"), " + tempVariable + ")";
			}
			retValue += "->";
		} else {
			retValue += ".";
		}
		retValue += StatementTranslatorContainer.translate(((DotPrimary) dotPrimary).getRight(), "");
		return retValue;
	}

	public String getSafeModeBeforeUsageDefinitions() {
		return safeModeBeforeUsageDefinitions;
	}

	public void setSafeModeBeforeUsageDefinitions(
			String safeModeBeforeUsageDefinitions) {
		this.safeModeBeforeUsageDefinitions = safeModeBeforeUsageDefinitions;
	}
}