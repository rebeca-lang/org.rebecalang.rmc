package org.rebecalang.rmc;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.propertycompiler.corerebeca.objectmodel.PropertyModel;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;

public abstract class AbstractFileGenerator {
	
	protected Set<CompilerFeature> cFeatures;
	protected Set<AnalysisFeature> aFeatures;
	protected Set<String> compilerFeaturesNames;
	protected Set<String> analysisFeaturesNames;
	protected PropertyModel propertyModel;
	
	protected RebecaModel rebecaModel;
	
	protected File destinationLocation;
	protected ExceptionContainer container;
	
	protected Set<String> getFeaturesNames(Set<?> features) {
		Set<String> featuresNames = new HashSet<String>();
		for (Object o : features) {
			featuresNames.add(((Enum<?>)o).name());
		}
		return featuresNames;
	}
	
	public void prepare(RebecaModel rebecaModel, PropertyModel propertyModel, 
			Set<CompilerFeature> compilerFeatures, Set<AnalysisFeature> analysisFeatures, CommandLine commandLine,
			File destinationLocation, ExceptionContainer container) throws CodeCompilationException {
		this.rebecaModel = rebecaModel;
		this.propertyModel = propertyModel;
		this.destinationLocation = destinationLocation;
		this.cFeatures = compilerFeatures;
		this.compilerFeaturesNames = getFeaturesNames(compilerFeatures);
		this.aFeatures = analysisFeatures;
		this.analysisFeaturesNames = new HashSet<String>();
		this.container = container;
		
		if(commandLine.hasOption("debug"))
			analysisFeatures.add(AnalysisFeature.DEBUG);
		if(commandLine.hasOption("debug2"))
			analysisFeatures.add(AnalysisFeature.DEBUG_LEVEL_2);
		destinationLocation.mkdirs();
	}
	
	public abstract void generateFiles();
	
	public ExceptionContainer getExceptionContainer() {
		return container;
	}
	
}
