package org.rebecalang.rmc.timedrebeca;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.apache.velocity.VelocityContext;
import org.rebecalang.compiler.modelcompiler.abstractrebeca.AbstractTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.compiler.modelcompiler.timedrebeca.PriorityType;
import org.rebecalang.compiler.modelcompiler.timedrebeca.objectmodel.TimedMainRebecDefinition;
import org.rebecalang.compiler.modelcompiler.timedrebeca.objectmodel.TimedRebecaCode;
import org.rebecalang.compiler.propertycompiler.generalrebeca.objectmodel.AssertionDefinition;
import org.rebecalang.compiler.propertycompiler.generalrebeca.objectmodel.Definition;
import org.rebecalang.compiler.propertycompiler.generalrebeca.objectmodel.PropertyModel;
import org.rebecalang.compiler.propertycompiler.timedrebeca.objectmodel.TCTLDefinition;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.FileGeneratorProperties;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.rebecalang.rmc.corerebeca.CoreRebecaFileGenerator;
import org.rebecalang.rmc.corerebeca.CoreRebecaMethodBodyConvertor;
import org.rebecalang.rmc.timedrebeca.translator.TimedRebecaNondetExpressionTranslator;
import org.rebecalang.rmc.timedrebeca.translator.TimedRebecaTermPrimaryExpressionTranslator;
import org.rebecalang.rmc.utils.TypeAnalysisException;
import org.rebecalang.rmc.utils.TypesAnalysisUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class TimedRebecaFileGenerator extends CoreRebecaFileGenerator {

	@Autowired
	public TimedRebecaFileGenerator(@Qualifier("TIMED_REBECA") AbstractTypeSystem typeSystem, 
			@Qualifier("TIMED_REBECA") CoreRebecaMethodBodyConvertor methodBodyConvertor,
			@Qualifier("TIMED_REBECA") StatementTranslatorContainer statementTranslatorContainer,
			ConfigurableApplicationContext appContext) {
		super(typeSystem, methodBodyConvertor, statementTranslatorContainer, appContext);
	}
	
	@Override
	protected void addTranslators() {
		super.addTranslators();
		statementTranslatorContainer.registerTranslator(NonDetExpression.class, 
				appContext.getBean(TimedRebecaNondetExpressionTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(TermPrimary.class, 
				appContext.getBean(TimedRebecaTermPrimaryExpressionTranslator.class, statementTranslatorContainer));
	}
	
	@Override
	public void generateFiles(RebecaModel rebecaModel, PropertyModel propertyModel, 
			File destinationLocation, Set<CompilerExtension> extension, FileGeneratorProperties fileGenerationProperties) {
		
		try {
			
			initilizeGeneratingFiles(rebecaModel, propertyModel, destinationLocation, extension, fileGenerationProperties);

			super.createMain(FilesNames.MAIN_PATCH_TEMPLATE);

			super.createTypeAndConfig(FilesNames.CONFIG_PATCH_TEMPLATE);

			List<String> patches = new LinkedList<String>();
			patches.add(org.rebecalang.rmc.corerebeca.FilesNames.STORABLE_ACTOR_PATCH_TEMPLATE);
			super.createAbstractActor(patches);

			patches.clear();
			patches.add(FilesNames.STORABLE_ACTOR_PATCH_TEMPLATE);
			createAbstractTimedActor(patches);
			
			patches.clear();
			patches.add(org.rebecalang.rmc.corerebeca.FilesNames.STORABLE_REACTIVE_CLASS_PATCH_TEMPLATE);
			patches.add(FilesNames.STORABLE_REACTIVE_CLASS_PATCH_TEMPLATE);
			patches.add(FilesNames.REACTIVE_CLASS_PATCH_TEMPLATE);
			createActors(patches);

			super.createBFSHashmapTemplate();
			createTimedRebecaBFSHashmap();
			
			createAbstractCoreRebecaAnalyzer();
			createMailBox();
			if (fileGenerationProperties.isTraceGenerator()) {
				createTraceGenerator();
			} else { 
				createTimedModelChecker();
			}
			
			if (propertyModel != null) {
				createDecomposedProperty();
			}

		} catch (IOException e) {
			exceptionContainer.addException(e);
		}

	}

	protected void createActors(List<String> patches) throws IOException {

		for (ReactiveClassDeclaration reactiveClassDeclaration : rebecaModel
				.getRebecaCode().getReactiveClassDeclaration()) {
			Set<String> constructorCallClasses = new HashSet<String>();
			constructorCallClasses.add("AbstractActor");
			Set<String> baseClasses = new HashSet<String>();
			baseClasses.add("AbstractTimedActor");

			super.createAnActor(reactiveClassDeclaration, baseClasses, constructorCallClasses, patches);

		}
		for (InterfaceDeclaration intd : rebecaModel.getRebecaCode().getInterfaceDeclaration()) {
			Set<String> constructorCallClasses = new HashSet<String>();
			constructorCallClasses.add("AbstractActor");
			Set<String> baseClasses = new HashSet<String>();
			baseClasses.add("AbstractTimedActor");

			super.createAnActor(intd, baseClasses, constructorCallClasses, patches);
		}
	}

	protected void createTimedRebecaBFSHashmap() throws IOException {
		VelocityContext context = new VelocityContext();

		mergeTemplat(context, FilesNames.TIMED_REBECA_BFS_HASHMAP_HEADER_TEMPLATE, FilesNames.TIMED_REBECA_BFS_HASHMAP_OUTPUT_HEADER);

		mergeTemplat(context, FilesNames.TIMED_REBECA_BFS_HASHMAP_CPP_TEMPLATE, FilesNames.TIMED_REBECA_BFS_HASHMAP_OUTPUT_CPP);
	}

	protected void createMailBox() throws IOException {
		TimedRebecaCode timedRebecaCode = (TimedRebecaCode) rebecaModel.getRebecaCode();
		MailBoxBodyConvertor mailBoxBodyConvertor = new MailBoxBodyConvertor();
		mailBoxBodyConvertor.setTimedRebecaCode(timedRebecaCode);
		VelocityContext context = new VelocityContext();
		context.put("mailboxDeclaration", timedRebecaCode.getMailboxDeclaration());
		context.put("mailBoxBodyConvertor", mailBoxBodyConvertor);
		mergeTemplat(context, FilesNames.ORDER_SPEC_HEADER_TEMPLATE, FilesNames.ORDER_SPEC_OUTPUT_HEADER);

		mergeTemplat(context, FilesNames.ORDER_SPEC_CPP_TEMPLATE, FilesNames.ORDER_SPEC_OUTPUT_CPP);
	}

	protected void createAbstractTimedActor(List<String> patches) throws IOException {

		PriorityType priorityType = getPriorityType();
		VelocityContext context = new VelocityContext();
		context.put("priorityType", priorityType.name());
		context.put("patches", patches);
		
		mergeTemplat(context, FilesNames.ABSTRACT_TIMED_ACTOR_HEADER_TEMPLATE, 
				FilesNames.ABSTRACT_TIMED_ACTOR_OUTPUT_HEADER);
		
		mergeTemplat(context, FilesNames.ABSTRACT_TIMED_ACTOR_CPP_TEMPLATE, 
				FilesNames.ABSTRACT_TIMED_ACTOR_OUTPUT_CPP);
	}

	private PriorityType getPriorityType() {
		for (ReactiveClassDeclaration rcd : rebecaModel.getRebecaCode().getReactiveClassDeclaration()) {
			for (MethodDeclaration md : rcd.getMsgsrvs()) {
				for (Annotation annotation : md.getAnnotations()) {
					if(annotation.getIdentifier().equals("globalPriority"))
						return PriorityType.global;
				}
			}
		}
		return PriorityType.local;
	}

	@Override
	protected void createAbstractCoreRebecaAnalyzer(String headerTemplate, String headerOutput,
													String cppTemplate, String cppOutput) throws IOException {

		List<Definition> definitions = null;
		List<AssertionDefinition> assertions = null;
		if (propertyModel != null) {
			definitions = propertyModel.getDefinitions();
			assertions = propertyModel.getAssertionDefinitions();
		}

		Hashtable<String, Integer> reactiveClassTypeOrder = new Hashtable<String, Integer>();
		Hashtable<String, Pair<Integer, Integer>> sizes= new Hashtable<String, Pair<Integer,Integer>>();
		int order = 0;
		for (ReactiveClassDeclaration reactiveClassDeclaration : rebecaModel
				.getRebecaCode().getReactiveClassDeclaration()) {
			String name = reactiveClassDeclaration.getName();
			reactiveClassTypeOrder.put(name,
					order++);
			sizes.put(name, new Pair<Integer, Integer>
					(reactiveClassDeclaration.getQueueSize(), getMaximumParametersSize(reactiveClassDeclaration)));
		}

		Hashtable<String, Integer> rebecInstanceOrder = new Hashtable<String, Integer>();
		order = 0;
		for (MainRebecDefinition mainRebecDefinition : rebecaModel
				.getRebecaCode().getMainDeclaration().getMainRebecDefinition()) {
			rebecInstanceOrder.put(mainRebecDefinition.getName(), order++);
		}

		VelocityContext context = new VelocityContext();
		context.put("reactiveClassDeclarations", rebecaModel.getRebecaCode()
				.getReactiveClassDeclaration());
		context.put("mainDefinition", rebecaModel.getRebecaCode()
				.getMainDeclaration());
		context.put("reactiveClassTypeOrder", reactiveClassTypeOrder);
		context.put("rebecInstanceOrder", rebecInstanceOrder);
		context.put("translator", statementTranslatorContainer);
		context.put("sizes", sizes);
		context.put("propertyDefinitions", (definitions == null ? new LinkedList<Definition>() : definitions));
		context.put("propertyAssertions", (assertions == null ? new LinkedList<AssertionDefinition>() : assertions));
		context.put("fileGeneratorProperties", fileGeneratorProperties);
		for(ReactiveClassDeclaration rd: rebecaModel.getRebecaCode()
				.getReactiveClassDeclaration()){
			context.put(rd.getName()+"StateSize", getStateSize(rd));

		}
		TimedRebecaCode timedRebecaCode = (TimedRebecaCode) rebecaModel.getRebecaCode();
		MailBoxBodyConvertor mailBoxBodyConvertor = new MailBoxBodyConvertor();
		mailBoxBodyConvertor.setTimedRebecaCode(timedRebecaCode);
		context.put("mailBoxBodyConvertor", mailBoxBodyConvertor);
//		TermPrimary mailboxTermPrimary = (TermPrimary) timedMainRebecDefinition.getMailbox();
//		mailboxTermPrimary.getName();
//		timedRebecaCode.getMailboxDeclaration().get(0).

		if(fileGeneratorProperties.isSafeMode())
			statementTranslatorContainer.TurnOffSafeMode();

		mergeTemplat(context, headerTemplate, headerOutput);

		mergeTemplat(context, cppTemplate, cppOutput);

		if(fileGeneratorProperties.isSafeMode())
			statementTranslatorContainer.TurnOnSafeMode();
	}

	protected void createAbstractCoreRebecaAnalyzer() throws IOException {
		createAbstractCoreRebecaAnalyzer(
				FilesNames.ABSTRACT_TIMED_REBECA_ANALYZER_HEADER_TEMPLATE,
				FilesNames.ABSTRACT_TIMED_REBECA_ANALYZER_OUTPUT_HEADER,
				FilesNames.ABSTRACT_TIMED_REBECA_ANALYZER_CPP_TEMPLATE,
				FilesNames.ABSTRACT_TIMED_REBECA_ANALYZER_OUTPUT_CPP
				);		
	}

	protected void createTimedModelChecker() throws IOException {

		super.createAbstractModelChecker();
		
		VelocityContext context = new VelocityContext();

		List<Definition> definitions = getDefinitionsFromPropertyModel(propertyModel);
		List<AssertionDefinition> assertions = getAssetionDefinitionFromPropertyModel(propertyModel);

		context.put("propertyDefinitions", definitions);
		context.put("propertyAssertions", assertions);

		List<String> patches = new LinkedList<String>();
		if (((TimedRebecaFileGeneratorProperties)fileGeneratorProperties).isTTS())
			patches.add(FilesNames.TTS_PATCH_TEMPLATE);
		else 
			patches.add(FilesNames.FTTS_PATCH_TEMPLATE);
		context.put("patches", patches);
		
		mergeTemplat(context, FilesNames.TIMED_MODEL_CHECKER_HEADER_TEMPLATE, 
				FilesNames.TIMED_MODEL_CHECKER_OUTPUT_HEADER);
		
		mergeTemplat(context, FilesNames.TIMED_MODEL_CHECKER_CPP_TEMPLATE, 
				FilesNames.TIMED_MODEL_CHECKER_OUTPUT_CPP);
	}

	protected void createTraceGenerator() throws IOException {
		VelocityContext context = new VelocityContext();

		mergeTemplat(context, FilesNames.ABSTRACT_TIMED_TRACE_GENERATOR_HEADER_TEMPLATE, 
				FilesNames.ABSTRACT_TIMED_TRACE_GENERATOR_OUTPUT_HEADER);

		mergeTemplat(context, FilesNames.ABSTRACT_TIMED_TRACE_GENERATOR_CPP_TEMPLATE, 
				FilesNames.ABSTRACT_TIMED_TRACE_GENERATOR_OUTPUT_CPP);
	}

	
	@Override
	protected int getStateSize(BaseClassDeclaration baseClassDeclaration) {
		try {
			//In case of TTS four extra bytes are required to store program-counter of msgsrv
			return super.getStateSize(baseClassDeclaration) + 
					(((TimedRebecaFileGeneratorProperties)fileGeneratorProperties).isTTS() ? 
					TypesAnalysisUtilities.getInstance().getTypeSize(CoreRebecaTypeSystem.INT_TYPE) : 
					0);
		} catch (TypeAnalysisException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private void createDecomposedProperty(Expression expression, FileWriter fileWriter) throws IOException {
		if (expression instanceof BinaryExpression) {
			fileWriter.write(((BinaryExpression)expression).getOperator() + AbstractStatementTranslator.NEW_LINE);
			fileWriter.write("(" + AbstractStatementTranslator.NEW_LINE);
			createDecomposedProperty(((BinaryExpression)expression).getLeft(), fileWriter); 
			fileWriter.write(")" + AbstractStatementTranslator.NEW_LINE);
			fileWriter.write("(" + AbstractStatementTranslator.NEW_LINE);
			createDecomposedProperty(((BinaryExpression)expression).getRight(), fileWriter);
			fileWriter.write(")" + AbstractStatementTranslator.NEW_LINE);
		} else if (expression instanceof UnaryExpression) {
			fileWriter.write(((UnaryExpression)expression).getOperator() + AbstractStatementTranslator.NEW_LINE);
			createDecomposedProperty(((UnaryExpression)expression).getExpression(), fileWriter); 
		} else if (expression instanceof Literal) {
			fileWriter.write(((Literal)expression).getLiteralValue() + AbstractStatementTranslator.NEW_LINE);
		} else if (expression instanceof TermPrimary) {
			TermPrimary term = (TermPrimary) expression;
			if (term.getParentSuffixPrimary() == null) {
				fileWriter.write(term.getName() + AbstractStatementTranslator.NEW_LINE);
			} else {
				BinaryExpression timePart = (BinaryExpression) term.getParentSuffixPrimary().getArguments().get(0);
				fileWriter.write(term.getName() + ", " + timePart.getOperator() + ", " +
						((Literal)timePart.getRight()).getLiteralValue() + AbstractStatementTranslator.NEW_LINE);
				fileWriter.write("(" + AbstractStatementTranslator.NEW_LINE);
				createDecomposedProperty(term.getParentSuffixPrimary().getArguments().get(1), fileWriter);
				if (term.getParentSuffixPrimary().getArguments().size() == 3)
					createDecomposedProperty(term.getParentSuffixPrimary().getArguments().get(2), fileWriter);
				fileWriter.write(")" + AbstractStatementTranslator.NEW_LINE);
			}
		}
	}
	
	private void createDecomposedProperty() throws IOException {
		
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.TCTL_DECOMPOSED_OUTPUT_SPEC);
		List<TCTLDefinition> tctlDefinitions =
		((org.rebecalang.compiler.propertycompiler.timedrebeca.objectmodel.PropertyModel) propertyModel).getTCTLDefinitions();
		for (TCTLDefinition tctlDefinition : tctlDefinitions) {
			fileWriter.write(tctlDefinition.getName() + ":" + AbstractStatementTranslator.NEW_LINE);
			createDecomposedProperty(tctlDefinition.getExpression(), fileWriter);
		}
		fileWriter.close();
	}
}
