package org.rebecalang.rmc.probabilistictimedrebeca;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.probabilisticrebeca.objectmodel.PAltStatement;
import org.rebecalang.compiler.modelcompiler.probabilisticrebeca.objectmodel.ProbabilisticExpression;
import org.rebecalang.compiler.modelcompiler.probabilisticrebeca.statementsemanticchecker.statement.PALTStatementSemanticCheck;
import org.rebecalang.compiler.propertycompiler.generalrebeca.objectmodel.PropertyModel;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.rebecalang.rmc.probabilisticrebeca.translator.PAltStatementTranslator;
import org.rebecalang.rmc.probabilisticrebeca.translator.ProbabilisticExpressionTranslator;
import org.rebecalang.rmc.timedrebeca.TimedRebecaFileGenerator;

public class ProbabilisticTimedRebecaFileGenerator extends TimedRebecaFileGenerator {

	public void prepare(RebecaModel rebecaModel, 
			PropertyModel propertyModel,
			Set<CompilerFeature> cFeatures,
			Set<AnalysisFeature> aFeatures,
			File destinationLocation,
			Properties properties,
			ExceptionContainer container) throws CodeCompilationException {
		
		super.prepare(rebecaModel, propertyModel, cFeatures, aFeatures,
				destinationLocation, properties, container);

		StatementTranslatorContainer.registerTranslator(PAltStatement.class, new PAltStatementTranslator(cFeatures, aFeatures));
		StatementTranslatorContainer.registerTranslator(ProbabilisticExpression.class, new ProbabilisticExpressionTranslator(cFeatures, aFeatures));
	}

	public void generateFiles() {
		
		try {
			super.createMain(FilesNames.MAIN_PATCH_TEMPLATE);
			super.createTypeAndConfig(org.rebecalang.rmc.timedrebeca.FilesNames.CONFIG_PATCH_TEMPLATE);

			List<String> patches = new LinkedList<String>();
			patches.add(org.rebecalang.rmc.corerebeca.FilesNames.STORABLE_ACTOR_PATCH_TEMPLATE);
			super.createAbstractActor(patches);

			patches.clear();
			patches.add(org.rebecalang.rmc.timedrebeca.FilesNames.STORABLE_ACTOR_PATCH_TEMPLATE);
			super.createAbstractTimedActor(patches);
			
			patches.clear();
			patches.add(org.rebecalang.rmc.corerebeca.FilesNames.STORABLE_REACTIVE_CLASS_PATCH_TEMPLATE);
			patches.add(org.rebecalang.rmc.timedrebeca.FilesNames.STORABLE_REACTIVE_CLASS_PATCH_TEMPLATE);
			patches.add(org.rebecalang.rmc.timedrebeca.FilesNames.REACTIVE_CLASS_PATCH_TEMPLATE);
			patches.add(FilesNames.REACTIVE_CLASS_PATCH_TEMPLATE);
			super.createActors(patches);

			super.createBFSHashmapTemplate();
			super.createTimedRebecaBFSHashmap();
			
			super.createAbstractCoreRebecaAnalyzer();
			
			createProbabilisticModelChecker();
			createTimedModelChecker();
			
			if (aFeatures.contains(AnalysisFeature.TRACE_GENERATOR)) {
				super.createTraceGenerator();
			} else { 
				createTimedModelChecker();
			}

		} catch (IOException e) {
			container.addException(e);
		}
	}

	protected void createProbabilisticModelChecker() throws IOException {
		VelocityContext context = new VelocityContext();
		context.put("PROB_ACCURACY", PALTStatementSemanticCheck.PROB_ACCURACY);
		context.put("PROB_PREC", (int)Math.log10(PALTStatementSemanticCheck.PROB_ACCURACY));

		Template template = velocityEngine
				.getTemplate(FilesNames.PROBABILISTIC_TIMED_MODEL_CHECKER_HEADER_TEMPLATE);
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.PROBABILISTIC_TIMED_MODEL_CHECKER_OUTPUT_HEADER);
		template.merge(context, fileWriter);
		fileWriter.close();

		template = velocityEngine.getTemplate(FilesNames.PROBABILISTIC_TIMED_MODEL_CHECKER_CPP_TEMPLATE);
		fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar + FilesNames.PROBABILISTIC_TIMED_MODEL_CHECKER_OUTPUT_CPP);
		template.merge(context, fileWriter);
		fileWriter.close();
	}

}
