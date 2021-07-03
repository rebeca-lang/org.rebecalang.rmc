package org.rebecalang.rmc.probabilistictimedrebeca;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.rebecalang.compiler.modelcompiler.abstractrebeca.AbstractTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.probabilisticrebeca.objectmodel.PAltStatement;
import org.rebecalang.compiler.modelcompiler.probabilisticrebeca.objectmodel.ProbabilisticExpression;
import org.rebecalang.compiler.modelcompiler.probabilisticrebeca.statementsemanticchecker.statement.PALTStatementSemanticCheck;
import org.rebecalang.compiler.propertycompiler.generalrebeca.objectmodel.PropertyModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.rmc.FileGeneratorProperties;
import org.rebecalang.rmc.StatementTranslatorContainer;
import org.rebecalang.rmc.corerebeca.CoreRebecaMethodBodyConvertor;
import org.rebecalang.rmc.probabilisticrebeca.translator.PAltStatementTranslator;
import org.rebecalang.rmc.probabilisticrebeca.translator.ProbabilisticExpressionTranslator;
import org.rebecalang.rmc.timedrebeca.TimedRebecaFileGenerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ProbabilisticTimedRebecaFileGenerator extends TimedRebecaFileGenerator {

	
	public ProbabilisticTimedRebecaFileGenerator(@Qualifier("TIMED_REBECA") AbstractTypeSystem typeSystem, 
			@Qualifier("TIMED_REBECA") CoreRebecaMethodBodyConvertor methodBodyConvertor,
			@Qualifier("PROBABILISTIC_TIMED_REBECA") StatementTranslatorContainer statementTranslatorContainer,
			ConfigurableApplicationContext appContext) {
		super(typeSystem, methodBodyConvertor, statementTranslatorContainer, appContext);
	}
	
	@Override
	protected void addTranslators() {
		super.addTranslators();
		statementTranslatorContainer.registerTranslator(PAltStatement.class, 
				appContext.getBean(PAltStatementTranslator.class, statementTranslatorContainer));
		statementTranslatorContainer.registerTranslator(ProbabilisticExpression.class, 
				appContext.getBean(ProbabilisticExpressionTranslator.class, statementTranslatorContainer));
	}

	@Override
	public void generateFiles(RebecaModel rebecaModel, PropertyModel propertyModel, 
			File destinationLocation, Set<CompilerExtension> extension, FileGeneratorProperties fileGenerationProperties) {
		
		try {
			initilizeGeneratingFiles(rebecaModel, propertyModel, destinationLocation, extension, fileGenerationProperties);

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
			
			if (fileGenerationProperties.isTraceGenerator()) {
				super.createTraceGenerator();
			} else { 
				createTimedModelChecker();
			}

		} catch (IOException e) {
			exceptionContainer.addException(e);
		}
	}

	protected void createProbabilisticModelChecker() throws IOException {
		VelocityContext context = new VelocityContext();
		context.put("PROB_ACCURACY", PALTStatementSemanticCheck.PROB_ACCURACY);
		context.put("PROB_PREC", (int)Math.log10(PALTStatementSemanticCheck.PROB_ACCURACY));

		mergeTemplat(context, FilesNames.PROBABILISTIC_TIMED_MODEL_CHECKER_HEADER_TEMPLATE, 
				FilesNames.PROBABILISTIC_TIMED_MODEL_CHECKER_OUTPUT_HEADER);

		mergeTemplat(context, FilesNames.PROBABILISTIC_TIMED_MODEL_CHECKER_CPP_TEMPLATE, 
				FilesNames.PROBABILISTIC_TIMED_MODEL_CHECKER_OUTPUT_CPP);
	}
}
