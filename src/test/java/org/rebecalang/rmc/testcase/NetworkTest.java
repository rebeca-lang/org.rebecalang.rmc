package org.rebecalang.rmc.testcase;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.rmc.ModelCheckersFilesGenerator;
import org.rebecalang.rmc.RMCConfig;
import org.rebecalang.rmc.timedrebeca.TimedRebecaFileGeneratorProperties;
import org.rebecalang.rmc.timedrebeca.network.RebecaModelNetworkDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@ContextConfiguration(classes = {CompilerConfig.class, RMCConfig.class})
@SpringJUnitConfig
public class NetworkTest {
    @Autowired
    RebecaModelCompiler compiler;
    @Autowired
    ModelCheckersFilesGenerator modelCheckersFilesGenerator;

    @Autowired
    public ExceptionContainer exceptionContainer;
    private String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/rmc/testcase/network/";
    private String RMC_OUTPUT = "target/";

    @Test
    void dummy() {
        String modelName = "TicketService";
        File model = new File(MODEL_FILES_BASE + modelName + ".rebeca");
        File output = new File(RMC_OUTPUT + modelName);
        Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
        extension.add(CompilerExtension.TIMED_REBECA);
        Pair<RebecaModel, SymbolTable> result = compiler.compileRebecaFile(model, extension, CoreVersion.CORE_2_1);
        TimedRebecaFileGeneratorProperties properties = new TimedRebecaFileGeneratorProperties();
        properties.setSafeMode(true);
        properties.setCoreVersion(CoreVersion.CORE_2_1);
        properties.setExportStateSpaceTargetFile("statespace.xml");

//        try {
//            modelCheckersFilesGenerator.generateFiles(model, null, output, extension, properties);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        RebecaModelNetworkDecorator modelNetworkDecorator = new RebecaModelNetworkDecorator(result.getFirst());
        modelNetworkDecorator.decorate();
    }
}
