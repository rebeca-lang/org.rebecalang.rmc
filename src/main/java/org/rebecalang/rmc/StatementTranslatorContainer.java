package org.rebecalang.rmc;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableInitializer;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.rmc.corerebeca.translator.FieldDeclarationStatementTranslator;

public class StatementTranslatorContainer {

	private static Hashtable<Class<? extends Statement>, AbstractStatementTranslator> translatorsRepository = 
			new Hashtable<Class<? extends Statement>, AbstractStatementTranslator>();
	static {
		translatorsRepository.put(Statement.class, getEmptyTranslator());
	}

	private static EmptyStatementTranslator getEmptyTranslator() {
		return new EmptyStatementTranslator(new HashSet<CompilerFeature>(), new HashSet<AnalysisFeature>());
	}
	
	public static void clearTranslator() {
		translatorsRepository = 
				new Hashtable<Class<? extends Statement>, AbstractStatementTranslator>();
		translatorsRepository.put(Statement.class, getEmptyTranslator());
	}

	public static void initialize() {
		for(AbstractStatementTranslator translator : translatorsRepository.values())
			translator.initialize();
	}
	public static ExceptionContainer getExceptions() {
		ExceptionContainer container = new ExceptionContainer();
		
		for(AbstractStatementTranslator translator : translatorsRepository.values()) {
			translator.fillExceptionContainer(container);
		}
		
		return container;
	}
	
	public static void registerTranslator(Class<? extends Statement> type, AbstractStatementTranslator translator) {
		translatorsRepository.put(type, translator);
	}

	public static void unregisterTranslator(Class<? extends Statement> type) {
		translatorsRepository.remove(type);
	}

	public static AbstractStatementTranslator getTranslator(Class<? extends Statement> type) {
		return translatorsRepository.get(type);
	}
	
	public static String translator(FormalParameterDeclaration fpd) {
		return ((FieldDeclarationStatementTranslator)getTranslator(FieldDeclaration.class)).resolveFormalParameterDeclarationStatement(fpd);
	}
	
	public static String translate(VariableInitializer variableInitializer) throws StatementTranslationException {
		return ((FieldDeclarationStatementTranslator)getTranslator(FieldDeclaration.class)).resolveVariableInitializer(variableInitializer);
	}
	
	public static String translate(Statement statement, String tab) throws StatementTranslationException {
		try {
			return translatorsRepository.get(statement.getClass()).translate(statement, tab);
		} catch (NullPointerException e) {
			throw new StatementTranslationException("Unknown translator for statement of type \"" +
					statement.getClass() + "\".", statement.getLineNumber(), statement.getCharacter());
		}
	}
	
	private static class EmptyStatementTranslator extends AbstractStatementTranslator {
		public EmptyStatementTranslator(Set<CompilerFeature> cFeatures,
				Set<AnalysisFeature> aFeatures) {
			super(cFeatures, aFeatures);
		}

		public String translate(Statement statement, String tab)
				throws StatementTranslationException {
			return "";
		}
	}
	
	public void TurnOnSafeMode() {
		for(AbstractStatementTranslator translator : translatorsRepository.values())
			translator.addAnalysisFeature(AnalysisFeature.SAFE_MODE);
	}

	public void TurnOffSafeMode() {
		for(AbstractStatementTranslator translator : translatorsRepository.values())
			translator.removeAnalysisFeature(AnalysisFeature.SAFE_MODE);
	}

}
