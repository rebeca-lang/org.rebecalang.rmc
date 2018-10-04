package org.rebecalang.rmc.corerebeca.translator;

import java.util.List;
import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ArrayVariableInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.OrdinaryVariableInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableInitializer;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslationException;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.rebecalang.rmc.utils.TypesAnalysisUtilities;

public class FieldDeclarationStatementTranslator extends AbstractStatementTranslator {

	protected VariableInitializer initialization;
	
	public void initialize() {
		initialization = null;
	}
	
	public FieldDeclarationStatementTranslator(Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures) {
		super(cFeatures, aFeatures);
	}

	public String translate(Statement statement, String tab)
			throws StatementTranslationException {
		FieldDeclaration fieldDeclaration = (FieldDeclaration) statement;
		String retValue = "";
		for (VariableDeclarator vd : fieldDeclaration.getVariableDeclarators()) {
			FormalParameterDeclaration formalParameterDeclaration = new FormalParameterDeclaration();
			formalParameterDeclaration.setType(fieldDeclaration.getType());
			formalParameterDeclaration.setName(vd.getVariableName());
			retValue += tab + resolveFormalParameterDeclarationStatement(formalParameterDeclaration);
			if (vd.getVariableInitializer() != null)
				retValue += " = " + resolveVariableInitializer(vd.getVariableInitializer());
			retValue += ";" + NEW_LINE;
		}
		return retValue;
	}

	public String resolveFormalParameterDeclarationStatement(FormalParameterDeclaration formalParameterDeclaration) {
		return TypesAnalysisUtilities.getCPPTypeName(formalParameterDeclaration.getType()) +
			   " _ref_" + formalParameterDeclaration.getName();
	}

	public String resolveVariableInitializer(
			VariableInitializer variableInitializer) throws StatementTranslationException {
		initialization = variableInitializer;
		String retValue = "";
		if (variableInitializer instanceof OrdinaryVariableInitializer) {
			retValue = StatementTranslatorContainer.translate(((OrdinaryVariableInitializer) variableInitializer).getValue(), "");
		} else if (variableInitializer instanceof ArrayVariableInitializer) {
			retValue = "{{";
			List<VariableInitializer> values = ((ArrayVariableInitializer) variableInitializer).getValues();
			for (VariableInitializer vi : values)
				retValue += resolveVariableInitializer(vi) + ",";
			retValue += "}}";
		} else {
			throw new StatementTranslationException("Unknown translation rule for initializer type " 
					+ variableInitializer.getClass(), variableInitializer.getLineNumber(), variableInitializer.getCharacter());
		}
		return retValue;
	}

	public Object getInitialization() {
		return initialization;
	}
}