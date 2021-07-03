package org.rebecalang.rmc;

import java.io.File;
import java.io.StringWriter;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.rebecalang.compiler.modelcompiler.abstractrebeca.AbstractTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaLabelUtility;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConstructorDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MsgsrvDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ParentSuffixPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.propertycompiler.generalrebeca.objectmodel.PropertyModel;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.rmc.corerebeca.FilesNames;
import org.rebecalang.rmc.corerebeca.CoreRebecaMethodBodyConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractFileGenerator {

	protected PropertyModel propertyModel;

	protected RebecaModel rebecaModel;

	protected File destinationLocation;
	
	protected VelocityEngine velocityEngine;

	@Autowired
	protected ExceptionContainer exceptionContainer;

	protected AbstractTypeSystem typeSystem;

	protected FileGeneratorProperties fileGeneratorProperties;
	
	protected CoreRebecaMethodBodyConvertor methodBodyConvertor;
	
	protected StatementTranslatorContainer statementTranslatorContainer;

	protected ConfigurableApplicationContext appContext;

	@Autowired
	public AbstractFileGenerator(AbstractTypeSystem typeSystem, CoreRebecaMethodBodyConvertor methodBodyConvertor,
			StatementTranslatorContainer statementTranslatorContainer, ConfigurableApplicationContext appContext) {
		this.appContext = appContext;
		this.typeSystem = typeSystem;
		this.methodBodyConvertor = methodBodyConvertor;
		this.statementTranslatorContainer = statementTranslatorContainer;
		this.methodBodyConvertor.setStatementTranslatorContainer(statementTranslatorContainer);

		addTranslators();
		
		this.velocityEngine = new VelocityEngine();
		// Initialize Velocity Library.
		velocityEngine.addProperty("resource.loader", "class");
		velocityEngine.addProperty("file.resource.loader.cache", false);
		// Set vtl loader to the classpath to be able to load vtl files that are
		// embedded in the result jar file
		velocityEngine.addProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
//		velocityEngine.setProperty("directive.set.null.allowed", true);
		velocityEngine.setProperty("runtime.strict_mode.enable", true);
		velocityEngine.init();
		Template template = velocityEngine.getTemplate(FilesNames.MACROS_TEMPLATE);
		template.merge(new VelocityContext(), new StringWriter());
		
	}

	protected abstract void addTranslators();

	public abstract void generateFiles(RebecaModel rebecaModel, PropertyModel propertyModel, 
			File destinationLocation, Set<CompilerExtension> extension,
			FileGeneratorProperties fileGenerationProperties);
	
	public void initilizeGeneratingFiles(RebecaModel rebecaModel, PropertyModel propertyModel, 
			File destinationLocation, Set<CompilerExtension> extension,
			FileGeneratorProperties fileGenerationProperties) {
		this.rebecaModel = rebecaModel;
		this.propertyModel = propertyModel;
		this.fileGeneratorProperties = fileGenerationProperties;
		this.destinationLocation = destinationLocation;
		destinationLocation.mkdirs();
		
		if(fileGenerationProperties.isSafeMode())
			statementTranslatorContainer.TurnOnSafeMode();
		
		addDefaultMethodsToRebecaModel(rebecaModel, fileGenerationProperties.getCoreVersion());
		methodBodyConvertor.initilize(fileGenerationProperties);
	}
	
	
	/*
	 * For the case of CORE_2_0, default constructor is added to reactive class declarations which sends 
	 * initial message to self to make the translation mechanism consistent with the other core versions.
	 */
	private void addDefaultMethodsToRebecaModel(RebecaModel rebecaModel, CoreVersion coreVersion) {
		if (coreVersion == CoreVersion.CORE_2_0) {
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
				initialMessageSend.setType(CoreRebecaTypeSystem.MSGSRV_TYPE);
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
				try {
					self.setType(typeSystem.getType(rcd.getName()));
				} catch (CodeCompilationException e) {
					exceptionContainer.addException(e);
				}
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
	}
}