package org.rebecalang.rmc.testcase;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.rmc.FileGeneratorProperties;
import org.rebecalang.rmc.ModelCheckersFilesGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = CompilerConfig.class) 
@SpringJUnitConfig
public class AddHocTests {
	
	@Autowired
	ModelCheckersFilesGenerator modelCheckersFilesGenerator;
	@Autowired
	public ExceptionContainer exceptionContainer;
	
	private String MODEL_FILES_BASE = "";

	@Test
	@Disabled
	public void AddHoc() {
		File model = new File(MODEL_FILES_BASE + "");
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		File output = new File("");
				
		FileGeneratorProperties properties = new FileGeneratorProperties();

		modelCheckersFilesGenerator.generateFiles(model, null, output, extension, properties);
	}
}