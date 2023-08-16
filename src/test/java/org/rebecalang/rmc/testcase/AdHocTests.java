package org.rebecalang.rmc.testcase;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.rmc.FileGeneratorProperties;
import org.rebecalang.rmc.ModelCheckersFilesGenerator;
import org.rebecalang.rmc.RMCConfig;
import org.rebecalang.rmc.timedrebeca.TimedRebecaFileGeneratorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, RMCConfig.class}) 
@SpringJUnitConfig
public class AdHocTests {
	
	@Autowired
	ModelCheckersFilesGenerator modelCheckersFilesGenerator;
	@Autowired
	public ExceptionContainer exceptionContainer;
	
	private String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/rmc/adhoc/";

	@Test
//	@Disabled
	public void AddHoc() {
		File model = new File(MODEL_FILES_BASE + "EDF.rebeca");
		File property = null;//new File(MODEL_FILES_BASE + "LBE.property");
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		extension.add(CompilerExtension.TIMED_REBECA);
		
		File output = new File("target/EDF-FTTS");
				
		TimedRebecaFileGeneratorProperties properties = new TimedRebecaFileGeneratorProperties();
//		properties.setDebugTrace(true);
//		properties.setDebugAll(true);
		properties.setTTS(false);
		properties.setExportStateSpaceTargetFile("statespace.txt");
		properties.setCoreVersion(CoreVersion.CORE_2_3);
		
		modelCheckersFilesGenerator.generateFiles(model, property, output, extension, properties);
		
		if(!exceptionContainer.getExceptions().isEmpty()) {
			System.out.println(exceptionContainer);
		}
	}
}