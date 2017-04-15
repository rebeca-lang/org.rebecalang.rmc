package org.rebecalang.rmc;

import java.io.File;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.propertycompiler.generalrebeca.objectmodel.PropertyModel;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.rmc.corerebeca.FilesNames;

public abstract class AbstractFileGenerator {

	protected Set<CompilerFeature> cFeatures;
	protected Set<AnalysisFeature> aFeatures;
	protected Set<String> compilerFeaturesNames;
	protected Set<String> analysisFeaturesNames;
	protected PropertyModel propertyModel;

	protected RebecaModel rebecaModel;

	protected File destinationLocation;
	protected ExceptionContainer container;
	protected Properties properties;
	
	protected VelocityEngine velocityEngine;

	protected Set<String> getFeaturesNames(Set<?> features) {
		Set<String> featuresNames = new HashSet<String>();
		for (Object o : features) {
			featuresNames.add(((Enum<?>) o).name());
		}
		return featuresNames;
	}

	public void prepare(RebecaModel rebecaModel, PropertyModel propertyModel, Set<CompilerFeature> compilerFeatures,
			Set<AnalysisFeature> analysisFeatures, File destinationLocation, Properties properties,
			ExceptionContainer container) throws CodeCompilationException {
		this.rebecaModel = rebecaModel;
		this.propertyModel = propertyModel;
		this.destinationLocation = destinationLocation;
		this.cFeatures = compilerFeatures;
		this.compilerFeaturesNames = getFeaturesNames(compilerFeatures);
		this.aFeatures = analysisFeatures;
		this.analysisFeaturesNames = new HashSet<String>();
		this.container = container;
		this.properties = properties;

		destinationLocation.mkdirs();

		this.velocityEngine = new VelocityEngine();
		// Initialize Velocity Library.
		velocityEngine.addProperty("resource.loader", "class");
		velocityEngine.addProperty("file.resource.loader.cache", false);
		// Set vtl loader to the classpath to be able to load vtl files that are
		// embedded in the result jar file
		velocityEngine.addProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.init();
		velocityEngine.setProperty("directive.set.null.allowed", true);
		Template template = velocityEngine.getTemplate(FilesNames.MACROS_TEMPLATE);
		template.merge(new VelocityContext(), new StringWriter());

	}

	public abstract void generateFiles();

	public ExceptionContainer getExceptionContainer() {
		return container;
	}

}
