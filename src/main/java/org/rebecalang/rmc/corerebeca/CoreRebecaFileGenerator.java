package org.rebecalang.rmc.corerebeca;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.rebecalang.compiler.modelcompiler.abstractrebeca.AbstractTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ArrayType;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BaseClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BreakStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.CastExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConditionalStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ContinueStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ForStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.InstanceofExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.InterfaceDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MainRebecDefinition;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.PlusSubExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReturnStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.SwitchStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TernaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.UnaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.WhileStatement;
import org.rebecalang.compiler.propertycompiler.corerebeca.objectmodel.LTLDefinition;
import org.rebecalang.compiler.propertycompiler.generalrebeca.objectmodel.AssertionDefinition;
import org.rebecalang.compiler.propertycompiler.generalrebeca.objectmodel.Definition;
import org.rebecalang.compiler.propertycompiler.generalrebeca.objectmodel.PropertyModel;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.rmc.AbstractFileGenerator;
import org.rebecalang.rmc.FileGeneratorProperties;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.rebecalang.rmc.corerebeca.ltl.LTLPropertyHandler;
import org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.graph.Graph;
import org.rebecalang.rmc.corerebeca.translator.BinaryExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.BlockStatementTranslator;
import org.rebecalang.rmc.corerebeca.translator.BreakStatementTranslator;
import org.rebecalang.rmc.corerebeca.translator.CastExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.ConditionalStatementTranslator;
import org.rebecalang.rmc.corerebeca.translator.ContinueStatementTranslator;
import org.rebecalang.rmc.corerebeca.translator.CoreRebecaNondetExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.CoreRebecaTermPrimaryExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.DotPrimaryExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.FieldDeclarationStatementTranslator;
import org.rebecalang.rmc.corerebeca.translator.ForStatementTranslator;
import org.rebecalang.rmc.corerebeca.translator.InstanceofExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.LiteralTranslator;
import org.rebecalang.rmc.corerebeca.translator.PlusSubExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.ReturnStatementTranslator;
import org.rebecalang.rmc.corerebeca.translator.SwitchStatementTranslator;
import org.rebecalang.rmc.corerebeca.translator.TernaryExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.UnaryExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.WhileStatementTranslator;
import org.rebecalang.rmc.utils.AnnotationsUtility;
import org.rebecalang.rmc.utils.TypeAnalysisException;
import org.rebecalang.rmc.utils.TypesAnalysisUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaFileGenerator extends AbstractFileGenerator {

	
	private final static Logger logger = Logger.getLogger(CoreRebecaFileGenerator.class);

	@Autowired
	public CoreRebecaFileGenerator(@Qualifier("CORE_REBECA") AbstractTypeSystem typeSystem, 
			@Qualifier("CORE_REBECA") CoreRebecaMethodBodyConvertor methodBodyConvertor,
			@Qualifier("CORE_REBECA") StatementTranslatorContainer statementTranslatorContainer,
			ConfigurableApplicationContext appContext) {
		super(typeSystem, methodBodyConvertor, statementTranslatorContainer, appContext);
	}

	@Override
	protected void addTranslators() {
		statementTranslatorContainer.registerTranslator(InstanceofExpression.class, 
				appContext.getBean(InstanceofExpressionTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(BinaryExpression.class, 
				appContext.getBean(BinaryExpressionTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(BlockStatement.class, 
				appContext.getBean(BlockStatementTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(BreakStatement.class, 
				appContext.getBean(BreakStatementTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(CastExpression.class, 
				appContext.getBean(CastExpressionTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(ConditionalStatement.class, 
				appContext.getBean(ConditionalStatementTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(ContinueStatement.class, 
				appContext.getBean(ContinueStatementTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(DotPrimary.class, 
				appContext.getBean(DotPrimaryExpressionTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(FieldDeclaration.class, 
				appContext.getBean(FieldDeclarationStatementTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(ForStatement.class, 
				appContext.getBean(ForStatementTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(Literal.class, 
				appContext.getBean(LiteralTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(NonDetExpression.class, 
				(CoreRebecaNondetExpressionTranslator)appContext.getBean("CoreRebecaNondetExpressionTranslator", statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(PlusSubExpression.class, 
				appContext.getBean(PlusSubExpressionTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(ReturnStatement.class, 
				appContext.getBean(ReturnStatementTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(SwitchStatement.class, 
				appContext.getBean(SwitchStatementTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(TermPrimary.class, 
				(CoreRebecaTermPrimaryExpressionTranslator)appContext.getBean("CoreRebecaTermPrimaryExpressionTranslator", statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(TernaryExpression.class, 
				appContext.getBean(TernaryExpressionTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(UnaryExpression.class, 
				appContext.getBean(UnaryExpressionTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(WhileStatement.class, 
				appContext.getBean(WhileStatementTranslator.class, statementTranslatorContainer));
	}
	
	@Override
	public void generateFiles(RebecaModel rebecaModel, PropertyModel propertyModel, 
			File destinationLocation, Set<CompilerExtension> extension, FileGeneratorProperties fileGenerationProperties) {
		try {

			initilizeGeneratingFiles(rebecaModel, propertyModel, destinationLocation, extension, fileGenerationProperties);

			createMain(FilesNames.MAIN_PATCH_TEMPLATE);

			createTypeAndConfig(FilesNames.CONFIG_PATCH_TEMPLATE);

			List<String> patches = new LinkedList<String>();
			
			patches.add(FilesNames.STORABLE_ACTOR_PATCH_TEMPLATE);
			createAbstractActor(patches);

			patches.clear();
			patches.add(FilesNames.STORABLE_REACTIVE_CLASS_PATCH_TEMPLATE);
			createActors(patches);


			createCoreRebecaDFSHashmap();


			List<Pair<String, Graph>> graphs = new LinkedList<Pair<String, Graph>>();
			LTLPropertyHandler propertyHandler = new LTLPropertyHandler();
			if (propertyModel != null) {
				for (LTLDefinition ltlDefinition : ((org.rebecalang.compiler.propertycompiler.corerebeca.objectmodel.PropertyModel)propertyModel).getLTLDefinitions()) {
					UnaryExpression theNegationOfFormula = new UnaryExpression();
					theNegationOfFormula.setOperator("!");
					theNegationOfFormula.setExpression(ltlDefinition.getExpression());

					graphs.add(new Pair<String, Graph>(ltlDefinition.getName(), 
							propertyHandler.ltl2BA(theNegationOfFormula)));
					logger.debug("\n" + graphs.get(graphs.size() - 1).getFirst() + ": " +
							LTLPropertyHandler.exportGraph(graphs.get(graphs.size() - 1).getSecond()));

				}
			}

			createAbstractCoreRebecaAnalyzer();
			createCoreRebecaModelChecker(graphs);

		} catch (IOException e) {
			exceptionContainer.addException(e);
		}
	}

	protected void mergeTemplat(VelocityContext context, String templateName, String target) throws IOException {
		Template template = velocityEngine.getTemplate(templateName);
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + target);
		template.merge(context, fileWriter);
		fileWriter.close();
	}

	protected void createMain(String mainPatch) throws IOException {

		VelocityContext context = new VelocityContext();
		context.put("fileGeneratorProperties", fileGeneratorProperties);
		context.put("envVariables", rebecaModel.getRebecaCode()
				.getEnvironmentVariables());
		context.put("translator", statementTranslatorContainer);

		context.put("mainPatch", mainPatch);

		mergeTemplat(context, FilesNames.MAIN_CPP_TEMPLATE, FilesNames.MAIN_OUTPUT_CPP);

		mergeTemplat(context, FilesNames.COMMAND_LINE_PARSER_HEADER_TEMPLATE, FilesNames.COMMAND_LINE_PARSER_OUTPUT_HEADER);

		mergeTemplat(context, FilesNames.COMMAND_LINE_PARSER_CPP_TEMPLATE, FilesNames.COMMAND_LINE_PARSER_OUTPUT_CPP);
	}
	
	protected void createTypeAndConfig(String configPatch) throws IOException {

		VelocityContext context = new VelocityContext();

		context.put("fileGeneratorProperties", fileGeneratorProperties);
		context.put("rebecCount", rebecaModel.getRebecaCode()
				.getMainDeclaration().getMainRebecDefinition().size());
		context.put("envVariables", 
				getEnvironmentVariablesWithoutInitializer(
						rebecaModel.getRebecaCode().getEnvironmentVariables()));
		context.put("translator", statementTranslatorContainer);

		context.put("configPatch", configPatch);

		mergeTemplat(context, FilesNames.TYPES_HEADER_TEMPLATE, FilesNames.TYPES_OUTPUT_HEADER);

		mergeTemplat(context, FilesNames.CONFIG_HEADER_TEMPLATE, FilesNames.CONFIG_OUTPUT_HEADER);
	}

	private Object getEnvironmentVariablesWithoutInitializer(List<FieldDeclaration> environmentVariables) {
		List<FieldDeclaration> environmentVariablesWithoutInitilizer = new LinkedList<FieldDeclaration>();
		for(FieldDeclaration fieldDeclaration : environmentVariables) {
			FieldDeclaration newFiledDeclaration = new FieldDeclaration();
			newFiledDeclaration.setType(fieldDeclaration.getType());
			newFiledDeclaration.getAnnotations().addAll(fieldDeclaration.getAnnotations());
			environmentVariablesWithoutInitilizer.add(newFiledDeclaration);
			for(VariableDeclarator variableDeclarator : fieldDeclaration.getVariableDeclarators()) {
				VariableDeclarator newVariableDeclarator = new VariableDeclarator();
				newVariableDeclarator.setVariableName(variableDeclarator.getVariableName());
				newFiledDeclaration.getVariableDeclarators().add(newVariableDeclarator);
			}
		}
		return environmentVariablesWithoutInitilizer;
	}

	protected void createAbstractActor(List<String> patches) throws IOException {

		VelocityContext context = new VelocityContext();
		context.put("patches", patches);

		List<ReactiveClassDeclaration> reactiveClassDeclarations = 
				rebecaModel.getRebecaCode().getReactiveClassDeclaration();
		int maxKnownRebec = getMaximumNumberOfKnownRebecsOfReactiveClasses(reactiveClassDeclarations);
		int maxMsgsrvs = getMaximumNumberOfMsgsrvsOfReactiveClasses(reactiveClassDeclarations);

		context.put("maxKnownRebec", maxKnownRebec + 1);
		context.put("maxMsgsrvs", maxMsgsrvs);
		context.put("patches", patches);

		
		mergeTemplat(context, FilesNames.ABSTRACT_ACTOR_HEADER_TEMPLATE, FilesNames.ABSTRACT_ACTOR_OUTPUT_HEADER);

		mergeTemplat(context, FilesNames.ABSTRACT_ACTOR_CPP_TEMPLATE, FilesNames.ABSTRACT_ACTOR_OUTPUT_CPP);
	}

	private int getMaximumNumberOfMsgsrvsOfReactiveClasses(List<ReactiveClassDeclaration> reactiveClassDeclarations) {
		int maximum = 0;
		for(ReactiveClassDeclaration reactiveClassDeclaration : reactiveClassDeclarations) {
			maximum = Math.max(maximum, reactiveClassDeclaration.getMsgsrvs().size());
		}
		return maximum;
	}

	private int getMaximumNumberOfKnownRebecsOfReactiveClasses(
			List<ReactiveClassDeclaration> reactiveClassDeclarations) {
		int maximum = 0;
		for(ReactiveClassDeclaration reactiveClassDeclaration : reactiveClassDeclarations) {
			int reactiveClassKnownRebecsNumber = 0;
			for(FieldDeclaration fieldDeclaration : reactiveClassDeclaration.getKnownRebecs())
				reactiveClassKnownRebecsNumber += TypesUtilities
						.getNumberOfVariablesInFieldDeclaration(fieldDeclaration);
			maximum = Math.max(maximum, reactiveClassKnownRebecsNumber);
		}
		return maximum;
	}

	protected void createActors(List<String> patches) throws IOException {

		Set<String> constructorCallClasses = new HashSet<String>();

		for (ReactiveClassDeclaration reactiveClassDeclaration : rebecaModel
				.getRebecaCode().getReactiveClassDeclaration()) {
			Set<String> baseClasses = new HashSet<String>();
			baseClasses.add("AbstractActor");

			createAnActor(reactiveClassDeclaration, baseClasses, constructorCallClasses, patches);

		}
		for (InterfaceDeclaration intd : rebecaModel.getRebecaCode().getInterfaceDeclaration()) {
			Set<String> baseClasses = new HashSet<String>();
			baseClasses.add("AbstractActor");

			createAnActor(intd, baseClasses, constructorCallClasses, patches);
		}
	}
	
	protected void createAnActor(BaseClassDeclaration baseClassDeclaration, 
			Set<String> baseClasses, Set<String> constructorCallClasses, List<String> patches) throws IOException
	{

		String fileName = baseClassDeclaration.getName();

		VelocityContext context = new VelocityContext();

		List<BaseClassDeclaration> allClassDeclarations = new ArrayList<BaseClassDeclaration>();
		allClassDeclarations.addAll(rebecaModel.getRebecaCode().getReactiveClassDeclaration());
		allClassDeclarations.addAll(rebecaModel.getRebecaCode().getInterfaceDeclaration());
		context.put("allClassDeclarations", allClassDeclarations);

		context.put("reactiveClassDeclaration", baseClassDeclaration);
		context.put("TypesAnalysisUtilities",
				TypesAnalysisUtilities.getInstance());
		context.put("ArrayType", new ArrayType());
		context.put("methodBodyConvertor", methodBodyConvertor);
		context.put("translator", statementTranslatorContainer);
		context.put("typeSystem", typeSystem);

		if (baseClassDeclaration instanceof ReactiveClassDeclaration) {
			ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) baseClassDeclaration;
			if (rcd.getExtends() != null) {
				String parentName = rcd.getExtends().getTypeName() + "Actor";
				baseClasses.add(parentName);
				context.put("parentName",parentName);

			}
			if(!rcd.getImplements().isEmpty()) {
				for (Type intdType : rcd.getImplements()) {
					String impName = intdType.getTypeName() + "Actor";
					baseClasses.add(impName);
				}
			}
		} else if (baseClassDeclaration instanceof InterfaceDeclaration) {
			InterfaceDeclaration intd = (InterfaceDeclaration) baseClassDeclaration;
			if (!intd.getExtends().isEmpty()) {
				for (Type intdType : intd.getExtends()) {
					String impName = intdType.getTypeName() + "Actor";
					baseClasses.add(impName);
				}
			}
		}

		context.put("parentMSGSRVCount", parentMethodCounts(baseClassDeclaration));
		context.put("baseClasses", baseClasses);

		getAllConstructorCallNames(baseClassDeclaration, constructorCallClasses);
		constructorCallClasses.removeAll(baseClasses);
		constructorCallClasses.remove(baseClassDeclaration.getName() + "Actor");
		
		context.put("constructorCallClasses", constructorCallClasses);
		context.put("patches", patches);
		int stateSize = getStateSize(baseClassDeclaration);
		context.put("stateSize", stateSize);
		context.put("AnnotationsUtility", AnnotationsUtility.getInstance());
		
		mergeTemplat(context, FilesNames.REACTIVE_CLASS_HEADER_TEMPLATE, fileName + "Actor.h");
		
		mergeTemplat(context, FilesNames.REACTIVE_CLASS_CPP_TEMPLATE, fileName + "Actor.cpp");
		
	}
	
	private void getAllConstructorCallNames(BaseClassDeclaration baseClassDeclaration , Set<String> constructorNames) {

		if (baseClassDeclaration instanceof ReactiveClassDeclaration) {
			ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) baseClassDeclaration;
			if (rcd.getExtends() != null) {
				try {
					getAllConstructorCallNames
					(typeSystem.getMetaData(rcd.getExtends()),constructorNames);
				} catch (CodeCompilationException e) {
					e.printStackTrace();
				}
			} if (!rcd.getImplements().isEmpty()) {
				for (Type impType : rcd.getImplements()) {
					try {
						InterfaceDeclaration idec =(InterfaceDeclaration)(typeSystem.getMetaData(impType));
						getAllConstructorCallNames(idec , constructorNames);
					} catch (CodeCompilationException e) {
						e.printStackTrace();
					}
				}
			}
			constructorNames.add(rcd.getName() + "Actor");
		} else if (baseClassDeclaration instanceof InterfaceDeclaration) {
			InterfaceDeclaration idec = (InterfaceDeclaration) baseClassDeclaration;
			if (!idec.getExtends().isEmpty()) {
				for (Type extType : idec.getExtends()) {
					try {
						InterfaceDeclaration intd =(InterfaceDeclaration)(typeSystem.getMetaData(extType));
						getAllConstructorCallNames(intd , constructorNames);
					} catch (CodeCompilationException e) {
						e.printStackTrace();
					}
				}

			}
			constructorNames.add(idec.getName() + "Actor");
		}
	}
	
	protected void createCoreRebecaDFSHashmap() throws IOException {

		VelocityContext context = new VelocityContext();
		context.put("REBEC_COUNT", rebecaModel.getRebecaCode().getMainDeclaration().getMainRebecDefinition().size());
		context.put("fileGeneratorProperties", fileGeneratorProperties);

		mergeTemplat(context, FilesNames.CORE_REBECA_DFS_HASHMAP_HEADER_TEMPLATE, FilesNames.CORE_REBECA_DFS_HASHMAP_OUTPUT_HEADER);
		
		mergeTemplat(context, FilesNames.CORE_REBECA_DFS_HASHMAP_CPP_TEMPLATE, FilesNames.CORE_REBECA_DFS_HASHMAP_OUTPUT_CPP);
	}

	protected void createBFSHashmapTemplate() throws IOException {
		
		VelocityContext context = new VelocityContext();

		mergeTemplat(context, FilesNames.BFS_HASHMAP_TEMPLATE_HEADER_TEMPLATE, FilesNames.BFS_HASHMAP_TEMPLATE_OUTPUT_HEADER);
	}

	protected void createCoreRebecaBFSHashmap() throws IOException {
		VelocityContext context = new VelocityContext();

		mergeTemplat(context, FilesNames.CORE_REBECA_BFS_HASHMAP_HEADER_TEMPLATE, FilesNames.CORE_REBECA_BFS_HASHMAP_OUTPUT_HEADER);

		mergeTemplat(context, FilesNames.CORE_REBECA_BFS_HASHMAP_CPP_TEMPLATE, FilesNames.CORE_REBECA_BFS_HASHMAP_OUTPUT_CPP);
	}

	protected void createAbstractCoreRebecaAnalyzer() throws IOException {
		createAbstractCoreRebecaAnalyzer(
			FilesNames.ABSTRACT_CORE_REBECA_ANALYZER_HEADER_TEMPLATE, 
			FilesNames.ABSTRACT_CORE_REBECA_ANALYZER_OUTPUT_HEADER,
			FilesNames.ABSTRACT_CORE_REBECA_ANALYZER_CPP_TEMPLATE, 
			FilesNames.ABSTRACT_CORE_REBECA_ANALYZER_OUTPUT_CPP
		);
	}
	
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

		if(fileGeneratorProperties.isSafeMode())
			statementTranslatorContainer.TurnOffSafeMode();
		
		mergeTemplat(context, headerTemplate, headerOutput);

		mergeTemplat(context, cppTemplate, cppOutput);

		if(fileGeneratorProperties.isSafeMode())
			statementTranslatorContainer.TurnOnSafeMode();
	}	

	protected void createCoreRebecaModelChecker(List<Pair<String, Graph>> propertyGraphs) throws IOException {

		List<Definition> definitions = getDefinitionsFromPropertyModel(propertyModel);
		List<AssertionDefinition> assertions = getAssetionDefinitionFromPropertyModel(propertyModel);

		createAbstractModelChecker();

		VelocityContext context = new VelocityContext();
		context.put("propertyGraph", propertyGraphs);
		context.put("propertyDefinitions", definitions);
		context.put("propertyAssertions", assertions);
		context.put("fileGeneratorProperties", fileGeneratorProperties);

		List<String> patches = new LinkedList<String>();
		patches.add(FilesNames.DFS_PATCH_TEMPLATE);
		context.put("patches", patches);

		mergeTemplat(context, FilesNames.CORE_REBECA_MODEL_CHECKER_HEADER_TEMPLATE, 
				FilesNames.CORE_REBECA_MODEL_CHECKER_OUTPUT_HEADER);

		mergeTemplat(context, FilesNames.CORE_REBECA_MODEL_CHECKER_CPP_TEMPLATE, 
				FilesNames.CORE_REBECA_MODEL_CHECKER_OUTPUT_CPP);
	}

	protected List<AssertionDefinition> getAssetionDefinitionFromPropertyModel(PropertyModel propertyModel) {
		if(propertyModel == null)
			return new LinkedList<AssertionDefinition>();
		if(propertyModel.getDefinitions() == null)
			return new LinkedList<AssertionDefinition>();			
		return propertyModel.getAssertionDefinitions();
	}

	protected List<Definition> getDefinitionsFromPropertyModel(PropertyModel propertyModel) {
		if(propertyModel == null)
			return new LinkedList<Definition>();
		if(propertyModel.getDefinitions() == null)
			return new LinkedList<Definition>();			
		return propertyModel.getDefinitions();
	}

	protected int retrieveSizeOfListOfVariables(List<FieldDeclaration> fields)
			throws TypeAnalysisException {
		int size = 0;
		int numberOfBooleanVariables = 0;

		for (FieldDeclaration fieldDeclaration : fields) {
			Type fieldType = fieldDeclaration.getType();
			int numberOfVariables = TypesUtilities.getNumberOfVariablesInFieldDeclaration(fieldDeclaration);

			if (TypesAnalysisUtilities.getBaseType(fieldType) == CoreRebecaTypeSystem.BOOLEAN_TYPE)
				numberOfBooleanVariables += numberOfVariables;
			else {
				size += numberOfVariables * TypesAnalysisUtilities.getInstance().getTypeSize(
						TypesAnalysisUtilities.getBaseType(fieldType));
			}
		}
		return size + (int) (Math.ceil((numberOfBooleanVariables * 1.0) / 8.0));
	}

	protected int retrieveSizeOfListOfParameters(
			List<FormalParameterDeclaration> formalParameters)
					throws TypeAnalysisException {
		List<FieldDeclaration> fields = TypesAnalysisUtilities
				.convertToFieldDeclaration(formalParameters);
		return retrieveSizeOfListOfVariables(fields);
	}

	protected int getStateSize(BaseClassDeclaration baseClassDeclaration) {
		int stateSize = 0;
		if (baseClassDeclaration instanceof InterfaceDeclaration)
			return stateSize;
		ReactiveClassDeclaration reactiveClassDeclaration = (ReactiveClassDeclaration) baseClassDeclaration;
		try {
			ReactiveClassDeclaration rcTemp = reactiveClassDeclaration;
			while (true) {
				ReactiveClassDeclaration parent;
				try {
					stateSize += retrieveSizeOfListOfVariables(rcTemp.getStatevars());
					if (rcTemp.getExtends() == null)
						break;
					parent = (ReactiveClassDeclaration)typeSystem.getMetaData(rcTemp.getExtends());
					rcTemp = parent;
				} catch (CodeCompilationException e) {
					e.printStackTrace();
				}
			}

			stateSize += reactiveClassDeclaration.getQueueSize()
					* (2 + getMaximumParametersSize(reactiveClassDeclaration));

		} catch (TypeAnalysisException e) {
			e.printStackTrace();
		}
		return stateSize;
	}

	protected int getMaximumParametersSize(ReactiveClassDeclaration reactiveClassDeclaration) {
		int maxParamSize = 0;
		for (MethodDeclaration methodDeclaration : reactiveClassDeclaration
				.getMsgsrvs()) {
			try {
				maxParamSize = Math.max(
						retrieveSizeOfListOfParameters(methodDeclaration
								.getFormalParameters()), maxParamSize);
			} catch (TypeAnalysisException e) {
				e.printStackTrace();
			}
		}
		return maxParamSize;
	}

	protected void createAbstractModelChecker() throws IOException {
		VelocityContext context = new VelocityContext();

		mergeTemplat(context, FilesNames.ABSTRACT_MODEL_CHECKER_HEADER_TEMPLATE, 
				FilesNames.ABSTRACT_MODEL_CHECKER_OUTPUT_HEADER);

		mergeTemplat(context, FilesNames.ABSTRACT_MODEL_CHECKER_CPP_TEMPLATE, 
				FilesNames.ABSTRACT_MODEL_CHECKER_OUTPUT_CPP);
	}

	protected void createRebecMgr() throws IOException {

		VelocityContext context = new VelocityContext();

		Hashtable<String, Integer> reactiveClassTypeOrder = new Hashtable<String, Integer>();
		int order = 0;
		for (ReactiveClassDeclaration reactiveClassDeclaration : rebecaModel
				.getRebecaCode().getReactiveClassDeclaration()) {
			reactiveClassTypeOrder.put(reactiveClassDeclaration.getName(),
					order++);
		}
		Hashtable<String, Integer> rebecInstanceOrder = new Hashtable<String, Integer>();
		order = 0;
		for (MainRebecDefinition mainRebecDefinition : rebecaModel
				.getRebecaCode().getMainDeclaration().getMainRebecDefinition()) {
			rebecInstanceOrder.put(mainRebecDefinition.getName(), order++);
		}
		context.put("reactiveClassDeclarations", rebecaModel.getRebecaCode()
				.getReactiveClassDeclaration());
		context.put("mainDefinition", rebecaModel.getRebecaCode()
				.getMainDeclaration());
		context.put("reactiveClassTypeOrder", reactiveClassTypeOrder);
		context.put("rebecInstanceOrder", rebecInstanceOrder);


		mergeTemplat(context, FilesNames.REBECMGR_HEADER_TEMPLATE, FilesNames.REBECMGR_OUTPUT_HEADER);

		mergeTemplat(context, FilesNames.REBECMGR_CPP_TEMPLATE, FilesNames.REBECMGR_OUTPUT_CPP);
	}

	private int parentMethodCounts(BaseClassDeclaration bcd) {

		if (bcd instanceof InterfaceDeclaration)
			return 0;
		ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) bcd;
		//This counter is set to 1 as it has to ignore "0" as the empty message
		int cnt = 1; 
		while(rcd.getExtends() != null) {
			try {
				rcd = (ReactiveClassDeclaration)typeSystem.getMetaData(rcd.getExtends());
				cnt += rcd.getMsgsrvs().size();
			} catch (CodeCompilationException e1) {
				e1.printStackTrace();
			}
		}
		return cnt;
	}

}