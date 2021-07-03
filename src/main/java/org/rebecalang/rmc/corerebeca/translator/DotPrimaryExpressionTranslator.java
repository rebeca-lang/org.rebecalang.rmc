package org.rebecalang.rmc.corerebeca.translator;

import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ArrayType;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.rebecalang.rmc.utils.TypesAnalysisUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DotPrimaryExpressionTranslator extends AbstractStatementTranslator {
	
	int tempCounter = 0;
	private String safeModeBeforeUsageDefinitions = "";
	
	@Autowired
	public DotPrimaryExpressionTranslator(StatementTranslatorContainer statementTranslatorContainer) {
		super(statementTranslatorContainer);
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
		retValue = statementTranslatorContainer.translate(dotPrimary.getLeft(), tab);
		Type baseType = dotPrimary.getLeft().getType();
		if (baseType instanceof ArrayType) {
			baseType = ((ArrayType)baseType).getOrdinaryPrimitiveType();
		}
		if (baseType.canTypeUpCastTo(CoreRebecaTypeSystem.REACTIVE_CLASS_TYPE)) {
			if (statementTranslatorContainer.isSafeMode()) {
				String tempVariable = "temp" + (tempCounter++);
				setSafeModeBeforeUsageDefinitions(getSafeModeBeforeUsageDefinitions()
						+ TypesAnalysisUtilities.getBaseType(dotPrimary.getLeft().getType()).getTypeName() 
						+ "Actor *" + tempVariable + ";");
				retValue = tab + "(" +  tempVariable + "=" + retValue.trim();
				retValue += ", assertion(" + tempVariable + "!= null, "
						+ "\"Null Pointer Exception in method \" + reactiveClassName + " +
						"\".\" + methodName + \"line " +
						dotPrimary.getLineNumber() +
						"\"), " + tempVariable + ")";
			}
			retValue += "->";
		} else {
			retValue += ".";
		}
		retValue += statementTranslatorContainer.translate(((DotPrimary) dotPrimary).getRight(), "");
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