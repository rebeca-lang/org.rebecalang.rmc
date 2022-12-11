package org.rebecalang.rmc.testcase;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.rmc.FileGeneratorProperties;
import org.rebecalang.rmc.ModelCheckersFilesGenerator;
import org.rebecalang.rmc.RMC;
import org.rebecalang.rmc.RMCConfig;
import org.rebecalang.rmc.timedrebeca.TimedRebecaFileGeneratorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, RMCConfig.class}) 
@SpringJUnitConfig
public class GeneralTest {
	
	@Autowired
	ModelCheckersFilesGenerator modelCheckersFilesGenerator;
	@Autowired
	public ExceptionContainer exceptionContainer;
	
	private String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/rmc/testcase/";
	private String RMC_OUTPUT = "target/";

	@Test
	public void generateDiningPhilosophers() {
		String modelName = "DiningPhilosophers";
		File model = new File(MODEL_FILES_BASE + modelName + ".rebeca");
		File property = new File(MODEL_FILES_BASE + modelName + ".property");
		File output = new File(RMC_OUTPUT + modelName);
		
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		
		FileGeneratorProperties properties = new FileGeneratorProperties();
		properties.setDebugAll(true);
		properties.setSafeMode(true);
		properties.setCoreVersion(CoreVersion.CORE_2_1);
		

		modelCheckersFilesGenerator.generateFiles(model, property, output, extension, properties);
		
	}

	@Test
	public void generateInterfaceDeclaration() {
		String modelName = "SimpleActorsUpgraded";
		File model = new File(MODEL_FILES_BASE + modelName + ".rebeca");
		File output = new File(RMC_OUTPUT + modelName);
		
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		
		FileGeneratorProperties properties = new FileGeneratorProperties();
		properties.setDebugAll(true);
		properties.setSafeMode(true);
		properties.setCoreVersion(CoreVersion.CORE_2_3);
		

		modelCheckersFilesGenerator.generateFiles(model, null, output, extension, properties);
		
		VerificationUtils.assertExceptionContainerIsEmpty(exceptionContainer);
	}

	@Test
	public void generateTinyOSMACB() {
		String modelName = "TinyOS-MACB";
		File model = new File(MODEL_FILES_BASE + modelName + ".rebeca");
		File output = new File(RMC_OUTPUT + modelName);
		
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		extension.add(CompilerExtension.TIMED_REBECA);
		
		TimedRebecaFileGeneratorProperties properties = new TimedRebecaFileGeneratorProperties();
		properties.setSafeMode(true);
		properties.setCoreVersion(CoreVersion.CORE_2_1);

		try {
			modelCheckersFilesGenerator.generateFiles(model, null, output, extension, properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Test
	public void generateTicketService() {
		String modelName = "TicketService";
		File model = new File(MODEL_FILES_BASE + modelName + ".rebeca");
		File output = new File(RMC_OUTPUT + modelName);
		
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		extension.add(CompilerExtension.TIMED_REBECA);
		
		TimedRebecaFileGeneratorProperties properties = new TimedRebecaFileGeneratorProperties();
		properties.setSafeMode(true);
		properties.setTTS(true);
		properties.setCoreVersion(CoreVersion.CORE_2_1);

		try {
			modelCheckersFilesGenerator.generateFiles(model, null, output, extension, properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void generateProbabilisticSensorNetwork() {
		String modelName = "Probabilistic-Sensor-Network";
		File model = new File(MODEL_FILES_BASE + modelName + ".rebeca");
		File output = new File(RMC_OUTPUT + modelName);
		
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		extension.add(CompilerExtension.TIMED_REBECA);
		extension.add(CompilerExtension.PROBABILISTIC_REBECA);
		
		TimedRebecaFileGeneratorProperties properties = new TimedRebecaFileGeneratorProperties();
		properties.setSafeMode(true);
		properties.setTTS(true);
		properties.setCoreVersion(CoreVersion.CORE_2_1);

		try {
			modelCheckersFilesGenerator.generateFiles(model, null, output, extension, properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMain() {
		String[] parameters = new String[] {
				"--source", MODEL_FILES_BASE + "DiningPhilosophers.rebeca",
				"-p", MODEL_FILES_BASE + "DiningPhilosophers.property",
				"-e", "CORE_REBECA",
				"-v", "2.1",
				"-o", RMC_OUTPUT + "DiningPhilosophersMain",
				"-debug",
				"-debug2",
				"-x",
		};
		
		RMC.main(parameters);
	}
}