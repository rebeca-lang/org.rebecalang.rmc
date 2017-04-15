package org.rebecalang.rmc.timedrebeca;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MainRebecDefinition;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.UnaryExpression;
import org.rebecalang.compiler.propertycompiler.generalrebeca.objectmodel.PropertyModel;
import org.rebecalang.compiler.propertycompiler.timedrebeca.objectmodel.TCTLDefinition;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.rmc.AbstractStatementTranslator;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.rebecalang.rmc.corerebeca.CoreRebecaFileGenerator;
import org.rebecalang.rmc.timedrebeca.translator.NondetExpressionTranslator;
import org.rebecalang.rmc.timedrebeca.translator.TermPrimaryExpressionTranslator;
import org.rebecalang.rmc.utils.TypeAnalysisException;
import org.rebecalang.rmc.utils.TypesAnalysisUtilities;

public class TimedRebecaFileGenerator extends CoreRebecaFileGenerator {
	
	public void prepare(RebecaModel rebecaModel, PropertyModel propertyModel,
			Set<CompilerFeature> compilerFeatures, 
			Set<AnalysisFeature> analysisFeatures,
			File destinationLocation, 
			Properties properties, 
			ExceptionContainer container) throws CodeCompilationException {

		super.prepare(rebecaModel, propertyModel, compilerFeatures, analysisFeatures,
				destinationLocation, properties, container);

		
		analysisFeaturesNames = getFeaturesNames(analysisFeatures);
		methodBodyConvertor = new TimedMethodBodyConvertor(analysisFeatures);
		StatementTranslatorContainer.registerTranslator(NonDetExpression.class, new NondetExpressionTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(TermPrimary.class, new TermPrimaryExpressionTranslator(cFeatures, aFeatures));
	}

	public void generateFiles() {
		
		try {
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
			
			if (aFeatures.contains(AnalysisFeature.TRACE_GENERATOR)) {
				createTraceGenerator();
			} else { 
				createTimedModelChecker();
			}
			
			if (propertyModel != null) {
				createDecomposedProperty();
			}

		} catch (IOException e) {
			container.addException(e);
		}

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
			createDecomposedProperty(tctlDefinition.getExpression(), fileWriter);
		}
		fileWriter.close();
	}

	protected void createActors(List<String> patches) throws IOException {

		List<String> constructorCallClasses = new LinkedList<String>();
		constructorCallClasses.add("AbstractActor");

		// Translate Each Reactive Class to its CPP & Header files.
		for (ReactiveClassDeclaration reactiveClassDeclaration : rebecaModel
				.getRebecaCode().getReactiveClassDeclaration()) {
			// Get Reactive Class to Generate Code for It.
			List<String> baseClasses = new LinkedList<String>();
			baseClasses.add("AbstractTimedActor");

			super.createAnActor(reactiveClassDeclaration, baseClasses, constructorCallClasses, patches);

		}
	}

	protected void createTimedRebecaBFSHashmap() throws IOException {
		VelocityContext context = new VelocityContext();

		Template template = velocityEngine
				.getTemplate(FilesNames.TIMED_REBECA_BFS_HASHMAP_HEADER_TEMPLATE);
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.TIMED_REBECA_BFS_HASHMAP_OUTPUT_HEADER);
		template.merge(context, fileWriter);
		fileWriter.close();

		template = velocityEngine.getTemplate(FilesNames.TIMED_REBECA_BFS_HASHMAP_CPP_TEMPLATE);
		fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.TIMED_REBECA_BFS_HASHMAP_OUTPUT_CPP);
		template.merge(context, fileWriter);
		fileWriter.close();
	}

	protected void createAbstractTimedActor(List<String> patches) throws IOException {

		VelocityContext context = new VelocityContext();
		context.put("patches", patches);
		
		Template template = velocityEngine
				.getTemplate(FilesNames.ABSTRACT_TIMED_ACTOR_HEADER_TEMPLATE);
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.ABSTRACT_TIMED_ACTOR_OUTPUT_HEADER);
		template.merge(context, fileWriter);
		fileWriter.close();
		template = velocityEngine.getTemplate(FilesNames.ABSTRACT_TIMED_ACTOR_CPP_TEMPLATE);
		fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.ABSTRACT_TIMED_ACTOR_OUTPUT_CPP);
		template.merge(context, fileWriter);
		fileWriter.close();
	}

	protected void createTimedModelChecker() throws IOException {

		super.createAbstractModelChecker();
		
		VelocityContext context = new VelocityContext();
		context.put("aFeatures", analysisFeaturesNames);
		context.put("cFeatures", compilerFeaturesNames);

		List<String> patches = new LinkedList<String>();
		if (aFeatures.contains(AnalysisFeature.TTS))
			patches.add(FilesNames.TTS_PATCH_TEMPLATE);
		else 
			patches.add(FilesNames.FTTS_PATCH_TEMPLATE);
		context.put("patches", patches);
		
		Template template = velocityEngine
				.getTemplate(FilesNames.TIMED_MODEL_CHECKER_HEADER_TEMPLATE);
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.TIMED_MODEL_CHECKER_OUTPUT_HEADER);
		template.merge(context, fileWriter);
		fileWriter.close();
		template = velocityEngine.getTemplate(FilesNames.TIMED_MODEL_CHECKER_CPP_TEMPLATE);
		fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.TIMED_MODEL_CHECKER_OUTPUT_CPP);
		template.merge(context, fileWriter);
		fileWriter.close();

	}

	protected void createAbstractCoreRebecaAnalyzer() throws IOException {

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
		if (propertyModel != null)
			context.put("propertyDefinitions", propertyModel.getDefinitions());
		

		Template template = velocityEngine
				.getTemplate(FilesNames.ABSTRACT_TIMED_REBECA_ANALYZER_HEADER_TEMPLATE);
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.ABSTRACT_TIMED_REBECA_ANALYZER_OUTPUT_HEADER);
		template.merge(context, fileWriter);
		fileWriter.close();
		template = velocityEngine.getTemplate(FilesNames.ABSTRACT_TIMED_REBECA_ANALYZER_CPP_TEMPLATE);
		fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.ABSTRACT_TIMED_REBECA_ANALYZER_OUTPUT_CPP);
		template.merge(context, fileWriter);
		fileWriter.close();
	}

	protected void createTraceGenerator() throws IOException {
		VelocityContext context = new VelocityContext();

		Template template = velocityEngine
				.getTemplate(FilesNames.ABSTRACT_TIMED_TRACE_GENERATOR_HEADER_TEMPLATE);
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.ABSTRACT_TIMED_TRACE_GENERATOR_OUTPUT_HEADER);
		template.merge(context, fileWriter);
		fileWriter.close();

		template = velocityEngine.getTemplate(FilesNames.ABSTRACT_TIMED_TRACE_GENERATOR_CPP_TEMPLATE);
		fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.ABSTRACT_TIMED_TRACE_GENERATOR_OUTPUT_CPP);
		template.merge(context, fileWriter);
		fileWriter.close();
	}

	
	protected int getStateSize(ReactiveClassDeclaration reactiveClassDeclaration) {
		try {
			//In case of TTS four extra bytes are required to store program-counter of msgsrv
			return super.getStateSize(reactiveClassDeclaration) + 
					(aFeatures.contains(AnalysisFeature.TTS) ? 
					TypesAnalysisUtilities.getInstance().getTypeSize(TypesUtilities.INT_TYPE) : 
					0);
		} catch (TypeAnalysisException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
