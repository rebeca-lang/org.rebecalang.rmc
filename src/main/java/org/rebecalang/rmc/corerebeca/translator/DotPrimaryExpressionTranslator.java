package org.rebecalang.rmc.corerebeca.translator;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslatorContainer;

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
		if (TypesUtilities.getInstance().canTypeCastTo(
				dotPrimary.getLeft().getType(), TypesUtilities.REACTIVE_CLASS_TYPE)) {
			if (aFeatures.contains(AnalysisFeature.SAFE_MODE)) {
				String tempVariable = "temp" + (tempCounter++);
				setSafeModeBeforeUsageDefinitions(getSafeModeBeforeUsageDefinitions()
						+ TypesUtilities.getTypeName(
								dotPrimary.getLeft().getType()) + "Actor *" + tempVariable + ";");
				retValue = tab + "(" +  tempVariable + "=" + retValue.trim();
				retValue += ", _synchmethod_assertion(" + tempVariable + "!= null, \"Null Pointer Exception\"), " + tempVariable + ")";
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