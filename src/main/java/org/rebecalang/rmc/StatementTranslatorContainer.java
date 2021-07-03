package org.rebecalang.rmc;

import java.util.Hashtable;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableInitializer;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.rmc.corerebeca.translator.FieldDeclarationStatementTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatementTranslatorContainer {

	private Hashtable<Class<? extends Statement>, AbstractStatementTranslator> translatorsRepository; 

	@Autowired
	ExceptionContainer exceptionContainer;

	private boolean safeMode;

	private FileGeneratorProperties fileGenerationProperties;
	
	public StatementTranslatorContainer() {
		clearTranslator();
	}
	
	public void clearTranslator() {
		translatorsRepository = 
				new Hashtable<Class<? extends Statement>, AbstractStatementTranslator>();
		translatorsRepository.put(Statement.class, getEmptyTranslator());
	}
	
	public void initialize() {
		for(AbstractStatementTranslator translator : translatorsRepository.values())
			translator.initialize();
	}

	public void initialize(FileGeneratorProperties fileGenerationProperties) {
		this.fileGenerationProperties = fileGenerationProperties;
		this.initialize();
	}
	
	public void registerTranslator(Class<? extends Statement> type, AbstractStatementTranslator translator) {
		translatorsRepository.put(type, translator);
	}

	public void unregisterTranslator(Class<? extends Statement> type) {
		translatorsRepository.remove(type);
	}

	public AbstractStatementTranslator getTranslator(Class<? extends Statement> type) {
		return translatorsRepository.get(type);
	}
	
	public String translator(FormalParameterDeclaration fpd) {
		return ((FieldDeclarationStatementTranslator)getTranslator(FieldDeclaration.class)).resolveFormalParameterDeclarationStatement(fpd);
	}
	
	public String translate(VariableInitializer variableInitializer) throws StatementTranslationException {
		return ((FieldDeclarationStatementTranslator)getTranslator(FieldDeclaration.class)).resolveVariableInitializer(variableInitializer);
	}
	
	public String translate(Statement statement, String tab) throws StatementTranslationException {

		if(!translatorsRepository.containsKey(statement.getClass()))
			throw new StatementTranslationException("Unknown translator for statement of type \"" +
					statement.getClass() + "\".", statement.getLineNumber(), statement.getCharacter());
		return translatorsRepository.get(statement.getClass()).translate(statement, tab);
	}
	
	public void TurnOnSafeMode() {
		safeMode = true;
	}

	public void TurnOffSafeMode() {
		safeMode = false;
	}
	
	public boolean isSafeMode() {
		return safeMode;
	}
	
	private EmptyStatementTranslator getEmptyTranslator() {
		return new EmptyStatementTranslator(null);
	}

	private class EmptyStatementTranslator extends AbstractStatementTranslator {
		@Autowired
		public EmptyStatementTranslator(StatementTranslatorContainer statementTranslatorContainer) {
			super(statementTranslatorContainer);
		}

		public String translate(Statement statement, String tab)
				throws StatementTranslationException {
			return "";
		}
	}
	
	public FileGeneratorProperties getFileGenerationProperties() {
		return fileGenerationProperties;
	}
}
