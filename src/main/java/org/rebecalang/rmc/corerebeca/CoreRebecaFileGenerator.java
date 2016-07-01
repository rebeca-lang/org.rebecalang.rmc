package org.rebecalang.rmc.corerebeca;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaLabelUtility;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ArrayType;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BreakStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.CastExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConditionalStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConstructorDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ContinueStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ForStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MainRebecDefinition;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MsgsrvDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ParentSuffixPrimary;
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
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.WhileStatement;
import org.rebecalang.compiler.propertycompiler.generalrebeca.objectmodel.Definition;
import org.rebecalang.compiler.propertycompiler.corerebeca.objectmodel.LTLDefinition;
import org.rebecalang.compiler.propertycompiler.generalrebeca.objectmodel.PropertyModel;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.rmc.AbstractFileGenerator;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.MethodBodyConvertor;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.rebecalang.rmc.corerebeca.ltl.LTLPropertyHandler;
import org.rebecalang.rmc.corerebeca.ltl.gov.nasa.ltl.graph.Graph;
import org.rebecalang.rmc.corerebeca.translator.BinaryExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.BlockStatementTranslator;
import org.rebecalang.rmc.corerebeca.translator.BreakStatementTranslator;
import org.rebecalang.rmc.corerebeca.translator.CastExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.ConditionalStatementTranslator;
import org.rebecalang.rmc.corerebeca.translator.ContinueStatementTranslator;
import org.rebecalang.rmc.corerebeca.translator.DotPrimaryExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.FieldDeclarationStatementTranslator;
import org.rebecalang.rmc.corerebeca.translator.ForStatementTranslator;
import org.rebecalang.rmc.corerebeca.translator.LiteralTranslator;
import org.rebecalang.rmc.corerebeca.translator.NondetExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.PlusSubExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.ReturnStatementTranslator;
import org.rebecalang.rmc.corerebeca.translator.SwitchStatementTranslator;
import org.rebecalang.rmc.corerebeca.translator.TermPrimaryExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.TernaryExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.UnaryExpressionTranslator;
import org.rebecalang.rmc.corerebeca.translator.WhileStatementTranslator;
import org.rebecalang.rmc.utils.TypeAnalysisException;
import org.rebecalang.rmc.utils.TypesAnalysisUtilities;

public class CoreRebecaFileGenerator extends AbstractFileGenerator {
	
	protected StatementTranslatorContainer translator = new StatementTranslatorContainer();
	protected MethodBodyConvertor methodBodyConvertor;
	protected Set<String> helperHeaders;
	protected String fileNameOfStateSpaceInXML;
	
	public void prepare(RebecaModel rebecaModel, PropertyModel propertyModel,
			Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures,
			CommandLine commandLine,
			File destinationLocation,
			ExceptionContainer container) throws CodeCompilationException {
		super.prepare(rebecaModel, propertyModel, cFeatures, aFeatures, commandLine,
				destinationLocation, container);
		methodBodyConvertor = new MethodBodyConvertor(aFeatures);
		if (commandLine.hasOption("x")) {
			if ((fileNameOfStateSpaceInXML = commandLine.getOptionValue("statespace")) == null){
				fileNameOfStateSpaceInXML = "statespace.xml";
			}
			aFeatures.add(AnalysisFeature.EXPORT_STATE_SPACE);
		}

		analysisFeaturesNames = getFeaturesNames(aFeatures);

		/*
		 * For the case of CORE_2_0, a constructor is created which sends initial message to self to keep
		 * translation mechanism consistent with the other core versions. 
		 */
		if (cFeatures.contains(CompilerFeature.CORE_2_0)) {
			for (ReactiveClassDeclaration rcd : rebecaModel.getRebecaCode().getReactiveClassDeclaration()) {
				ConstructorDeclaration constructorDeclaration = new ConstructorDeclaration();
				constructorDeclaration.setName(rcd.getName());
				MsgsrvDeclaration initialMsgsrv = null;
				for (MsgsrvDeclaration md : rcd.getMsgsrvs()) {
					if (md.getName().equals("initial"))
						initialMsgsrv = md;
				}
				for (FormalParameterDeclaration initialFPD : initialMsgsrv.getFormalParameters()) {
					FormalParameterDeclaration fpd = new FormalParameterDeclaration();
					fpd.setName(initialFPD.getName());
					fpd.setType(initialFPD.getType());
					constructorDeclaration.getFormalParameters().add(fpd);
				}
				
				TermPrimary initialMessageSend = new TermPrimary();
				initialMessageSend.setName("initial");
				initialMessageSend.setType(TypesUtilities.MSGSRV_TYPE);
				initialMessageSend.setLabel(CoreRebecaLabelUtility.MSGSRV);
				ParentSuffixPrimary psp = new ParentSuffixPrimary();
				for (FormalParameterDeclaration initialFPD : initialMsgsrv.getFormalParameters()) {
					TermPrimary param = new TermPrimary();
					param.setName(initialFPD.getName());
					param.setType(initialFPD.getType());
					psp.getArguments().add(param);		
				}
				initialMessageSend.setParentSuffixPrimary(psp);
				
				TermPrimary self = new TermPrimary();
				self.setType(TypesUtilities.getInstance().getType(rcd.getName()));
				self.setName("self");
				
				DotPrimary sendStatement = new DotPrimary();
				sendStatement.setLeft(self);
				sendStatement.setRight(initialMessageSend);
				
				BlockStatement constructorStatements = new BlockStatement();
				constructorStatements.getStatements().add(sendStatement);
				
				constructorDeclaration.setBlock(constructorStatements);
				
				rcd.getConstructors().add(constructorDeclaration);

			}
		}
		
		StatementTranslatorContainer.registerTranslator(BinaryExpression.class, new BinaryExpressionTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(BlockStatement.class, new BlockStatementTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(BreakStatement.class, new BreakStatementTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(CastExpression.class, new CastExpressionTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(ConditionalStatement.class, new ConditionalStatementTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(ContinueStatement.class, new ContinueStatementTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(DotPrimary.class, new DotPrimaryExpressionTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(FieldDeclaration.class, new FieldDeclarationStatementTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(ForStatement.class, new ForStatementTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(Literal.class, new LiteralTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(NonDetExpression.class, new NondetExpressionTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(PlusSubExpression.class, new PlusSubExpressionTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(ReturnStatement.class, new ReturnStatementTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(SwitchStatement.class, new SwitchStatementTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(TermPrimary.class, new TermPrimaryExpressionTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(TernaryExpression.class, new TernaryExpressionTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(UnaryExpression.class, new UnaryExpressionTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(WhileStatement.class, new WhileStatementTranslator(cFeatures, aFeatures));
	}

	public void generateFiles() {
		try {
			
			createMain(FilesNames.MAIN_PATCH_TEMPLATE);

			createTypeAndConfig(FilesNames.CONFIG_PATCH_TEMPLATE);

			List<String> patches = new LinkedList<String>();
			patches.add(FilesNames.STORABLE_ACTOR_PATCH_TEMPLATE);
			createAbstractActor(patches);

			patches.clear();
			patches.add(FilesNames.STORABLE_REACTIVE_CLASS_PATCH_TEMPLATE);
			createActors(patches);

//			createBFSHashmapTemplate();
//			createCoreRebecaBFSHashmap();
			
			createCoreRebecaDFSHashmap();
			
			
			List<Pair<String, Graph>> graphs = new LinkedList<Pair<String, Graph>>();
			LTLPropertyHandler propertyHandler = new LTLPropertyHandler();
			if (propertyModel != null) {
				for (LTLDefinition ltlDefinition : ((org.rebecalang.compiler.propertycompiler.corerebeca.objectmodel.PropertyModel)propertyModel).getLTLDefinitions()) {
					graphs.add(new Pair<String, Graph>(ltlDefinition.getName(), 
							propertyHandler.ltl2BA(ltlDefinition.getExpression())));
					Pair<String, Graph> pair = graphs.get(graphs.size() - 1);
					boolean booleanAttribute = pair.getSecond().getNode(0).getBooleanAttribute("accepting");
					int a = 10;
					if (a == 20);
				}
			}

			createAbstractCoreRebecaAnalyzer();
			createCoreRebecaModelChecker(graphs);

//
//			createAcAut(null);
//
//			createPropDefinitions();
//			
//			createRBFSMC();
//
//			createCTLStaticFiles();
		} catch (IOException e) {
			container.addException(e);
		}
	}

	
	protected void createAbstractActor(List<String> patches) throws IOException {

		VelocityContext context = new VelocityContext();
		context.put("aFeatures", analysisFeaturesNames);
		context.put("cFeatures", compilerFeaturesNames);
		context.put("patches", patches);
		
		// Finding max number of known rebecs.
		int maxKnownRebec = 0;
		// Finding max number of reactive class methods.
		int maxMethodNumber = 0;
		for (ReactiveClassDeclaration reactiveClassDeclaration : rebecaModel
				.getRebecaCode().getReactiveClassDeclaration()) {
			List<FieldDeclaration> knownRebecs = reactiveClassDeclaration
					.getKnownRebecs();
			int numberOfKnownRebecs = 0;
			for (FieldDeclaration knownRebec : knownRebecs)
				numberOfKnownRebecs += TypesUtilities
						.getNumberOfVariablesInFieldDeclaration(knownRebec);
			maxKnownRebec = Math.max(numberOfKnownRebecs, maxKnownRebec);

			List<MsgsrvDeclaration> msgsrvDeclaration = reactiveClassDeclaration
					.getMsgsrvs();
			maxMethodNumber = Math.max(msgsrvDeclaration.size(),
					maxMethodNumber);
		}

		context.put("maxKnownRebec", maxKnownRebec + 1);
		context.put("maxMethodNumber", maxMethodNumber);
		context.put("aFeatures", analysisFeaturesNames);
		context.put("cFeatures", compilerFeaturesNames);
		context.put("Integer", 0);
		context.put("TypesUtilities", TypesUtilities.getInstance());
		context.put("patches", patches);

		Template template = Velocity
				.getTemplate(FilesNames.ABSTRACT_ACTOR_HEADER_TEMPLATE);
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.ABSTRACT_ACTOR_OUTPUT_HEADER);
		template.merge(context, fileWriter);
		fileWriter.close();
		template = Velocity.getTemplate(FilesNames.ABSTRACT_ACTOR_CPP_TEMPLATE);
		fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.ABSTRACT_ACTOR_OUTPUT_CPP);
		template.merge(context, fileWriter);
		fileWriter.close();
	}

	protected void createActors(List<String> patches) throws IOException {

		List<String> constructorCallClasses = new LinkedList<String>();

		// Translate Each Reactive Class to its CPP & Header files.
		for (ReactiveClassDeclaration reactiveClassDeclaration : rebecaModel
				.getRebecaCode().getReactiveClassDeclaration()) {
			// Get Reactive Class to Generate Code for It.
			List<String> baseClasses = new LinkedList<String>();
			baseClasses.add("AbstractActor");

			createAnActor(reactiveClassDeclaration, baseClasses, constructorCallClasses, patches);

		}
	}

	public void createBFSHashmapTemplate() throws IOException {
		VelocityContext context = new VelocityContext();

		Template template = Velocity
				.getTemplate(FilesNames.BFS_HASHMAP_TEMPLATE_HEADER_TEMPLATE);
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.BFS_HASHMAP_TEMPLATE_OUTPUT_HEADER);
		template.merge(context, fileWriter);
		fileWriter.close();
	}

	public void createCoreRebecaBFSHashmap() throws IOException {
		VelocityContext context = new VelocityContext();

		Template template = Velocity
				.getTemplate(FilesNames.CORE_REBECA_BFS_HASHMAP_HEADER_TEMPLATE);
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.CORE_REBECA_BFS_HASHMAP_OUTPUT_HEADER);
		template.merge(context, fileWriter);
		fileWriter.close();

		template = Velocity.getTemplate(FilesNames.CORE_REBECA_BFS_HASHMAP_CPP_TEMPLATE);
		fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.CORE_REBECA_BFS_HASHMAP_OUTPUT_CPP);
		template.merge(context, fileWriter);
		fileWriter.close();
	}

	public void createCoreRebecaDFSHashmap() throws IOException {

		VelocityContext context = new VelocityContext();
		context.put("REBEC_COUNT", rebecaModel.getRebecaCode().getMainDeclaration().getMainRebecDefinition().size());

		Template template = Velocity
				.getTemplate(FilesNames.CORE_REBECA_DFS_HASHMAP_HEADER_TEMPLATE);
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.CORE_REBECA_DFS_HASHMAP_OUTPUT_HEADER);
		template.merge(context, fileWriter);
		fileWriter.close();

		template = Velocity.getTemplate(FilesNames.CORE_REBECA_DFS_HASHMAP_CPP_TEMPLATE);
		fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.CORE_REBECA_DFS_HASHMAP_OUTPUT_CPP);
		template.merge(context, fileWriter);
		fileWriter.close();
	}

	protected void createAbstractCoreRebecaAnalyzer() throws IOException {

		List<Definition> definitions;
		if (propertyModel == null)
			definitions = new LinkedList<Definition>();
		else
			definitions = propertyModel.getDefinitions();
		VelocityContext context = new VelocityContext();
		context.put("aFeatures", analysisFeaturesNames);
		context.put("cFeatures", compilerFeaturesNames);

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

		context.put("reactiveClassDeclarations", rebecaModel.getRebecaCode()
				.getReactiveClassDeclaration());
		context.put("mainDefinition", rebecaModel.getRebecaCode()
				.getMainDeclaration());
		context.put("reactiveClassTypeOrder", reactiveClassTypeOrder);
		context.put("rebecInstanceOrder", rebecInstanceOrder);
		context.put("aFeatures", analysisFeaturesNames);
		context.put("TypesUtilities", TypesUtilities.getInstance());
		context.put("translator", translator);
		context.put("sizes", sizes);
		context.put("propertyDefinitions", definitions);



		Template template = Velocity
				.getTemplate(FilesNames.ABSTRACT_CORE_REBECA_ANALYZER_HEADER_TEMPLATE);
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.ABSTRACT_CORE_REBECA_ANALYZER_OUTPUT_HEADER);
		template.merge(context, fileWriter);
		fileWriter.close();
		template = Velocity.getTemplate(FilesNames.ABSTRACT_CORE_REBECA_ANALYZER_CPP_TEMPLATE);
		fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.ABSTRACT_CORE_REBECA_ANALYZER_OUTPUT_CPP);
		template.merge(context, fileWriter);
		fileWriter.close();
	}	

	protected void createCoreRebecaModelChecker(List<Pair<String, Graph>> propertyGraphs) throws IOException {

		List<Definition> definitions;
		if (propertyModel == null)
			definitions = new LinkedList<Definition>();
		else
			definitions = propertyModel.getDefinitions();
		createAbstractModelChecker();
		
		VelocityContext context = new VelocityContext();
		context.put("aFeatures", analysisFeaturesNames);
		context.put("cFeatures", compilerFeaturesNames);
		context.put("propertyGraph", propertyGraphs);
		context.put("propertyDefinitions", definitions);
		
		List<String> patches = new LinkedList<String>();
		patches.add(FilesNames.DFS_PATCH_TEMPLATE);
		context.put("patches", patches);
		
		Template template = Velocity
				.getTemplate(FilesNames.CORE_REBECA_MODEL_CHECKER_HEADER_TEMPLATE);
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.CORE_REBECA_MODEL_CHECKER_OUTPUT_HEADER);
		template.merge(context, fileWriter);
		fileWriter.close();
		template = Velocity.getTemplate(FilesNames.CORE_REBECA_MODEL_CHECKER_CPP_TEMPLATE);
		fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.CORE_REBECA_MODEL_CHECKER_OUTPUT_CPP);
		template.merge(context, fileWriter);
		fileWriter.close();

	}


	
	protected int retrieveSizeOfListOfVariables(List<FieldDeclaration> fields)
			throws TypeAnalysisException {
		int size = 0;
		int numberOfBooleanVariables = 0;

		for (FieldDeclaration fieldDeclaration : fields) {
			Type fieldType = fieldDeclaration.getType();
			int numberOfVariables = TypesUtilities.getNumberOfVariablesInFieldDeclaration(fieldDeclaration);

			if (TypesAnalysisUtilities.getBaseType(fieldType) == TypesUtilities.BOOLEAN_TYPE)
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

	protected int getStateSize(ReactiveClassDeclaration reactiveClassDeclaration) {
		int stateSize = 0;
		try {
			stateSize += retrieveSizeOfListOfVariables(reactiveClassDeclaration
					.getStatevars());
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

		context.put("aFeatures", analysisFeaturesNames);
		context.put("cFeatures", compilerFeaturesNames);
		
		//Creating .h and .cpp files of AbstractModelChecker
		Template template = Velocity
				.getTemplate(FilesNames.ABSTRACT_MODEL_CHECKER_HEADER_TEMPLATE);
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.ABSTRACT_MODEL_CHECKER_OUTPUT_HEADER);
		template.merge(context, fileWriter);
		fileWriter.close();
		template = Velocity.getTemplate(FilesNames.ABSTRACT_MODEL_CHECKER_CPP_TEMPLATE);
		fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.ABSTRACT_MODEL_CHECKER_OUTPUT_CPP);
		template.merge(context, fileWriter);
		fileWriter.close();


	}
	
	/**
	 * This function creates AcAut header and cpp files.
	 * 
	 * @throws IOException
	 *             All velocity exceptions that occurs in function.
	 */
	
	public void createAnActor(ReactiveClassDeclaration reactiveClassDeclaration, 
			List<String> baseClasses, List<String> constructorCallClasses, List<String> patches) throws IOException
			{
		
		String fileName = reactiveClassDeclaration.getName();

		VelocityContext context = new VelocityContext();
		context.put("reactiveClassDeclarations", rebecaModel
				.getRebecaCode().getReactiveClassDeclaration());
		context.put("reactiveClassDeclaration", reactiveClassDeclaration);
		context.put("Integer", 0);
		context.put("aFeatures", analysisFeaturesNames);
		context.put("cFeatures", compilerFeaturesNames);
		context.put("TypesUtilities", TypesUtilities.getInstance());
		context.put("TypesAnalysisUtilities",
				TypesAnalysisUtilities.getInstance());
		context.put("ArrayType", new ArrayType());
		context.put("newline", "\n");
		context.put("methodBodyConvertor", methodBodyConvertor);
		context.put("translator", translator);
		context.put("reactiveClassName", reactiveClassDeclaration.getName());
		context.put("baseClasses", baseClasses);
		context.put("constructorCallClasses", constructorCallClasses);
		context.put("patches", patches);
		context.put("stateSize", getStateSize(reactiveClassDeclaration));
		// Create Header File
		Template template = Velocity
				.getTemplate(FilesNames.REACTIVE_CLASS_HEADER_TEMPLATE);
		FileWriter fileWriter = new FileWriter(
				destinationLocation.getPath() + File.separatorChar
						+ fileName + "Actor.h");
		template.merge(context, fileWriter);
		fileWriter.close();

		// Create C++ Class File
		template = Velocity.getTemplate(FilesNames.REACTIVE_CLASS_CPP_TEMPLATE);
		fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + fileName + "Actor.cpp");
		template.merge(context, fileWriter);
		fileWriter.close();
		
	}

	/**
	 * This function creates RebecMgr class according to the rebecs binding.
	 * 
	 * @throws IOException
	 *             All velocity exceptions that occurs in function.
	 */
	protected void createRebecMgr() throws IOException {

		VelocityContext context = new VelocityContext();
		context.put("aFeatures", analysisFeaturesNames);
		context.put("cFeatures", compilerFeaturesNames);

		// Create Header File
		Template template = Velocity
				.getTemplate(FilesNames.REBECMGR_HEADER_TEMPLATE);
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.REBECMGR_OUTPUT_HEADER);
		template.merge(context, fileWriter);
		fileWriter.close();

		// Create C++ File
		context = new VelocityContext();

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
		context.put("aFeatures", analysisFeaturesNames);
		context.put("TypesUtilities", TypesUtilities.getInstance());

		template = Velocity.getTemplate(FilesNames.REBECMGR_CPP_TEMPLATE);
		fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.REBECMGR_OUTPUT_CPP);
		template.merge(context, fileWriter);
		fileWriter.close();

	}

	protected void createTypeAndConfig(String configPatch) throws IOException {

		VelocityContext context = new VelocityContext();

		context.put("aFeatures", analysisFeaturesNames);
		context.put("cFeatures", compilerFeaturesNames);
		
		context.put("rebecCount", rebecaModel.getRebecaCode()
				.getMainDeclaration().getMainRebecDefinition().size());
		List<FieldDeclaration> environmentVariables = rebecaModel.getRebecaCode().getEnvironmentVariables();
		LinkedList<VariableInitializer> variableInitializers = new LinkedList<VariableInitializer>();
		for(FieldDeclaration fieldDeclaration : environmentVariables) {
			for(VariableDeclarator variableDeclarator : fieldDeclaration.getVariableDeclarators()) {
				variableInitializers.add(variableDeclarator.getVariableInitializer());
				variableDeclarator.setVariableInitializer(null);
			}
		}
		context.put("envVariables", rebecaModel.getRebecaCode()
				.getEnvironmentVariables());
		context.put("translator", translator);
		
		context.put("configPatch", configPatch);

		// Create Header File
		Template template = Velocity
				.getTemplate(FilesNames.TYPES_HEADER_TEMPLATE);

		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.TYPES_OUTPUT_HEADER);
		template.merge(context, fileWriter);
		fileWriter.close();

		template = Velocity.getTemplate(FilesNames.CONFIG_HEADER_TEMPLATE);
		fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.CONFIG_OUTPUT_HEADER);

		template.merge(context, fileWriter);
		fileWriter.close();

		for(FieldDeclaration fieldDeclaration : environmentVariables) {
			for(VariableDeclarator variableDeclarator : fieldDeclaration.getVariableDeclarators()) {
				variableDeclarator.setVariableInitializer(variableInitializers.remove());
			}
		}
	}

	/**
	 * This function just copy from template to output file (Because Main.cpp
	 * structure is fix)
	 * 
	 * @throws IOException
	 *             All velocity exceptions that occurs in function.
	 */
	protected void createMain(String mainPatch) throws IOException {

		VelocityContext context = new VelocityContext();
		context.put("aFeatures", analysisFeaturesNames);
		context.put("cFeatures", compilerFeaturesNames);
		context.put("envVariables", rebecaModel.getRebecaCode()
				.getEnvironmentVariables());
		context.put("translator", translator);

		context.put("mainPatch", mainPatch);
		
		// Create Header File
		Template template = Velocity.getTemplate(FilesNames.MAIN_CPP_TEMPLATE);
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.MAIN_OUTPUT_CPP);

		template.merge(context, fileWriter);
		fileWriter.close();

		//Creating .h and .cpp files of CommandLineParser
		template = Velocity
				.getTemplate(FilesNames.COMMAND_LINE_PARSER_HEADER_TEMPLATE);
		fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.COMMAND_LINE_PARSER_OUTPUT_HEADER);
		template.merge(context, fileWriter);
		fileWriter.close();
		template = Velocity.getTemplate(FilesNames.COMMAND_LINE_PARSER_CPP_TEMPLATE);
		fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.COMMAND_LINE_PARSER_OUTPUT_CPP);
		template.merge(context, fileWriter);
		fileWriter.close();

	}

	
	
	/**
	 * This method sets property buchi-automata start point and creates RMC
	 * file.
	 * 
	 * @throws IOException
	 *             All velocity exceptions that occures in function.
	 */
//	protected void createRBFSMC() throws IOException {
//
//		VelocityContext context = new VelocityContext();
//		if (propertyModel != null) {
////			LTLPropertyHandler handler = new LTLPropertyHandler();
////			context.put("ltlSpecifications", handler.getLTLSpecification(null));
//		}
//
//		context.put("REBEC_COUNT", rebecaModel.getRebecaCode().getMainDeclaration().getMainRebecDefinition().size());
//		context.put("aFeatures", analysisFeaturesNames);
//		context.put("reactiveClassDeclarations", rebecaModel.getRebecaCode().getReactiveClassDeclaration());
//		context.put("mainDeclaration", rebecaModel.getRebecaCode().getMainDeclaration().getMainRebecDefinition());
////		context.put("guardNameSeperator", new GuardNameSeperator());
//
//		// Create C++ File
//		Template template = Velocity.getTemplate(FilesNames.BODERE_CPP_TEMPLATE);
//		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar + FilesNames.RBFSMC_OUTPUT_CPP);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//		// Create Header File
//		template = Velocity.getTemplate(FilesNames.BODERE_HEADER_TEMPLATE);
//		fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar + FilesNames.RBFSMC_OUTPUT_HEADER);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//
//		// Create Header File
//		template = Velocity
//				.getTemplate(FilesNames.BFSHASHMAP_HEADER_TEMPLATE);
//		fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar + FilesNames.BFSHASHMAP_OUTPUT_HEADER);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//
//		// Create C++ File
//		template = Velocity.getTemplate(FilesNames.BFSHASHMAP_CPP_TEMPLATE);
//		fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar + FilesNames.BFSHASHMAP_OUTPUT_CPP);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//
//		// Create Header File
//		template = Velocity
//				.getTemplate(FilesNames.BACKWARD_BFS_STATE_HEADER_TEMPLATE);
//		fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar
//				+ FilesNames.BACKWARD_BFS_STATE_OUTPUT_HEADER);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//
//
//		// Create C++ File
//		template = Velocity.getTemplate(FilesNames.MODERE_CPP_TEMPLATE);
//		fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar + FilesNames.RMC_OUTPUT_CPP);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//
//		// Create Header File
//		template = Velocity
//				.getTemplate(FilesNames.HASHMAP_HEADER_TEMPLATE);
//		fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar + FilesNames.HASHMAP_OUTPUT_HEADER);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//
//		// Create C++ File
//		template = Velocity.getTemplate(FilesNames.HASHMAP_CPP_TEMPLATE);
//		fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar + FilesNames.HASHMAP_OUTPUT_CPP);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//
//		// Create Header File
//		template = Velocity
//				.getTemplate(FilesNames.CLAIMAUT_HEADER_TEMPLATE);
//		fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar + FilesNames.CLAIMAUT_OUTPUT_HEADER);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//
//		// Create C++ File
//		template = Velocity.getTemplate(FilesNames.CLAIMAUT_CPP_TEMPLATE);
//		fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar + FilesNames.CLAIMAUT_OUTPUT_CPP);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//	}

	/**
	 * This method creates ctl static files according to the velocity templates.
	 * 
	 * @throws IOException
	 *             All velocity exceptions that occures in function.
	 */
//	protected void createCTLStaticFiles() throws IOException {
//
//		VelocityContext context = new VelocityContext();
//		// Create C++ File
//		Template template = Velocity.getTemplate(FilesNames.CTLMC_CPP_TEMPLATE);
//		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar + FilesNames.CTLMC_OUTPUT_CPP);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//
//		template = Velocity.getTemplate(FilesNames.CTLMC_HEADER_TEMPLATE);
//		fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar + FilesNames.CTLMC_OUTPUT_HEADER);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//
//		template = Velocity.getTemplate(FilesNames.CTLPROP_HEADER_TEMPLATE);
//		fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar + FilesNames.CTLPROP_OUTPUT_HEADER);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//
//		template = Velocity.getTemplate(FilesNames.CTLPROP_CPP_TEMPLATE);
//		fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar + FilesNames.CTLPROP_OUTPUT_CPP);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//
//		template = Velocity.getTemplate(FilesNames.CTLSS_CPP_TEMPLATE);
//		fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar + FilesNames.CTLSS_OUTPUT_CPP);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//
//		template = Velocity.getTemplate(FilesNames.CTLSS_HEADER_TEMPLATE);
//		fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar + FilesNames.CTLSS_OUTPUT_HEADER);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//
//		template = Velocity.getTemplate(FilesNames.CTLHASHMAP_CPP_TEMPLATE);
//		fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar + FilesNames.CTLHASHMAP_OUTPUT_CPP);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//
//		template = Velocity.getTemplate(FilesNames.CTLHASHMAP_HEADER_TEMPLATE);
//		fileWriter = new FileWriter(destinationLocation.getPath()
//				+ File.separatorChar + FilesNames.CTLHASHMAP_OUTPUT_HEADER);
//		template.merge(context, fileWriter);
//		fileWriter.close();
//
//	}

//	public void updateConfigFile(File outputLocation,
//			Set<CompilerFeature> cFeatures, Set<AnalysisFeature> aFeatures)
//			throws IOException {
//		VelocityContext context = new VelocityContext();
//
//		context.put("aFeatures", analysisFeaturesNames);
//
//		// Create Header File
//		Template template = Velocity
//				.getTemplate(FilesNames.CONFIG_HEADER_TEMPLATE);
//		FileWriter fileWriter = new FileWriter(outputLocation.getPath()
//				+ File.separatorChar + FilesNames.CONFIG_OUTPUT_HEADER);
//
//		template.merge(context, fileWriter);
//		fileWriter.close();
//	}

	@SuppressWarnings("static-access")
	public static OptionGroup getOptions() {
		OptionGroup group = new OptionGroup();
		group.addOption(
				OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("Rebeca model property file.")
                .withLongOpt("property").create('p')
				);
		return group;
	}

}
