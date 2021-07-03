/*                          In the name of Allah                         */
/*                           The Best will come                          */

package org.rebecalang.rmc.corerebeca;

/**
 * Output files and velocity template files mapping.
 */
public interface FilesNames {

	public final static String BASE = "vtl/corerebeca/";
	public final static String COMMON = "vtl/common/";
	public final static String ANALYZER_BASE = BASE + "analyzer/";
	
	public static final String MACROS_TEMPLATE = COMMON + "Macros.vm";

	public final static  String STORABLE_ACTOR_PATCH_TEMPLATE = COMMON + "StorableActorPatchTemplate.vm";
    public final static  String STORABLE_REACTIVE_CLASS_PATCH_TEMPLATE = COMMON + "StorableReactiveClassPatchTemplate.vm";

	public final static String TYPES_OUTPUT_HEADER = "Types.h";
    public final static  String TYPES_HEADER_TEMPLATE = COMMON + "TypesHeaderTemplate.vm";
    
    public final static  String MAIN_OUTPUT_CPP = "Main.cpp";
    public final static  String MAIN_CPP_TEMPLATE = COMMON + "MainCPPTemplate.vm";
    public final static  String CONFIG_OUTPUT_HEADER = "Config.h";
    public final static  String CONFIG_HEADER_TEMPLATE = COMMON + "ConfigHeaderTemplate.vm";

    public static final String MAIN_PATCH_TEMPLATE = BASE + "MainPatch.vm";
	public static final String CONFIG_PATCH_TEMPLATE = BASE + "ConfigPatchTemplate.vm";

    //'core' Files
    public final static  String ABSTRACT_ACTOR_OUTPUT_HEADER = "AbstractActor.h";
    public final static  String ABSTRACT_ACTOR_HEADER_TEMPLATE = COMMON + "AbstractActorHeaderTemplate.vm";
    public final static  String ABSTRACT_ACTOR_OUTPUT_CPP = "AbstractActor.cpp";
    public final static  String ABSTRACT_ACTOR_CPP_TEMPLATE = COMMON + "AbstractActorCPPTemplate.vm";
    //
    //
    public final static  String REACTIVE_CLASS_CPP_TEMPLATE = COMMON + "ReactiveClassCPPTemplate.vm";
    public final static  String REACTIVE_CLASS_HEADER_TEMPLATE = COMMON + "ReactiveClassHeaderTemplate.vm";
    //
    public final static  String BFS_HASHMAP_TEMPLATE_OUTPUT_HEADER = "BFSHashmapTemplate.h";
    public final static  String BFS_HASHMAP_TEMPLATE_HEADER_TEMPLATE = COMMON + "BFSHashmapTemplateHeaderTemplate.vm";
    //
    public final static  String CORE_REBECA_BFS_HASHMAP_OUTPUT_CPP = "CoreRebecaBFSHashmap.cpp";
    public final static  String CORE_REBECA_BFS_HASHMAP_CPP_TEMPLATE = BASE + "analyzer/CoreRebecaBFSHashmapCPPTemplate.vm";
    public final static  String CORE_REBECA_BFS_HASHMAP_OUTPUT_HEADER = "CoreRebecaBFSHashmap.h";
    public final static  String CORE_REBECA_BFS_HASHMAP_HEADER_TEMPLATE = BASE + "analyzer/CoreRebecaBFSHashmapHeaderTemplate.vm";
    
    public final static  String CORE_REBECA_DFS_HASHMAP_OUTPUT_CPP = "CoreRebecaDFSHashmap.cpp";
    public final static  String CORE_REBECA_DFS_HASHMAP_CPP_TEMPLATE = BASE + "analyzer/CoreRebecaDFSHashmapCPPTemplate.vm";
    public final static  String CORE_REBECA_DFS_HASHMAP_OUTPUT_HEADER = "CoreRebecaDFSHashmap.h";
    public final static  String CORE_REBECA_DFS_HASHMAP_HEADER_TEMPLATE = BASE + "analyzer/CoreRebecaDFSHashmapHeaderTemplate.vm";
    
	public static final String ABSTRACT_CORE_REBECA_ANALYZER_HEADER_TEMPLATE = ANALYZER_BASE + "AbstractCoreRebecaAnalyzerHeaderTemplate.vm";
	public static final String ABSTRACT_CORE_REBECA_ANALYZER_OUTPUT_HEADER = "AbstractCoreRebecaAnalyzer.h";
	public static final String ABSTRACT_CORE_REBECA_ANALYZER_CPP_TEMPLATE = ANALYZER_BASE + "AbstractCoreRebecaAnalyzerCPPTemplate.vm";
	public static final String ABSTRACT_CORE_REBECA_ANALYZER_OUTPUT_CPP = "AbstractCoreRebecaAnalyzer.cpp";

	public static final String CORE_REBECA_MODEL_CHECKER_HEADER_TEMPLATE = ANALYZER_BASE + "CoreRebecaModelCheckerHeaderTemplate.vm";
	public static final String CORE_REBECA_MODEL_CHECKER_OUTPUT_HEADER = "CoreRebecaModelChecker.h";
	public static final String CORE_REBECA_MODEL_CHECKER_CPP_TEMPLATE = ANALYZER_BASE + "CoreRebecaModelCheckerCPPTemplate.vm";
	public static final String CORE_REBECA_MODEL_CHECKER_OUTPUT_CPP = "CoreRebecaModelChecker.cpp";
    
	public static final String DFS_PATCH_TEMPLATE = ANALYZER_BASE + "DFSPatchTemplate.vm";
        
    public final static  String REBECMGR_OUTPUT_HEADER = "RebecMgr.h";
    public final static  String REBECMGR_HEADER_TEMPLATE = BASE + "RebecMgrHeaderTemplate.vm";
    public final static  String REBECMGR_OUTPUT_CPP = "RebecMgr.cpp";
    public final static  String REBECMGR_CPP_TEMPLATE = BASE + "RebecMgrCPPTemplate.vm";

	public static final String ABSTRACT_MODEL_CHECKER_HEADER_TEMPLATE = COMMON + "AbstractModelCheckerHeaderTemplate.vm";
	public static final String ABSTRACT_MODEL_CHECKER_OUTPUT_HEADER = "AbstractModelChecker.h";
	public static final String ABSTRACT_MODEL_CHECKER_CPP_TEMPLATE = COMMON + "AbstractModelCheckerCPPTemplate.vm";
	public static final String ABSTRACT_MODEL_CHECKER_OUTPUT_CPP = "AbstractModelChecker.cpp";
	
	public static final String COMMAND_LINE_PARSER_HEADER_TEMPLATE = COMMON + "CommandLineParserHeaderTemplate.vm";
	public static final String COMMAND_LINE_PARSER_CPP_TEMPLATE = COMMON + "CommandLineParserCPPTemplate.vm";
	public static final String COMMAND_LINE_PARSER_OUTPUT_HEADER = "CommandLineParser.h";
	public static final String COMMAND_LINE_PARSER_OUTPUT_CPP = "CommandLineParser.cpp";

}