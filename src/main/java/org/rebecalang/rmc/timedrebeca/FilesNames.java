/*                          In the name of Allah                         */
/*                           The Best will come                          */

package org.rebecalang.rmc.timedrebeca;

/**
 * Output files and velocity template files mapping.
 */
public interface FilesNames {

	public final static String BASE = "vtl/timedrebeca/";
	public final static String ACTOR_BASE = BASE +"actor/";
	public final static String ANALYZER_BASE = BASE + "analyzer/";
	public final static String COMMON = org.rebecalang.rmc.corerebeca.FilesNames.COMMON;

    public final static  String STORABLE_ACTOR_PATCH_TEMPLATE = BASE + "StorableTimedActorPatchTemplate.vm";
    public final static  String STORABLE_REACTIVE_CLASS_PATCH_TEMPLATE = BASE + "StorableReactiveClassPatchTemplate.vm";
    public final static  String REACTIVE_CLASS_PATCH_TEMPLATE = BASE + "ReactiveClassPatchTemplate.vm";

    public final static String ABSTRACT_TIMED_ACTOR_OUTPUT_HEADER = "AbstractTimedActor.h";
    public final static String ABSTRACT_TIMED_ACTOR_HEADER_TEMPLATE = ACTOR_BASE + "AbstractTimedActorHeaderTemplate.vm";
    public final static String ABSTRACT_TIMED_ACTOR_OUTPUT_CPP = "AbstractTimedActor.cpp";
    public final static String ABSTRACT_TIMED_ACTOR_CPP_TEMPLATE = ACTOR_BASE + "AbstractTimedActorCPPTemplate.vm";
    
    public final static String TIMED_REBECA_BFS_HASHMAP_OUTPUT_HEADER = "TimedRebecaBFSHashmap.h";
    public final static String TIMED_REBECA_BFS_HASHMAP_HEADER_TEMPLATE = ANALYZER_BASE + "TimedRebecaBFSHashmapHeaderTemplate.vm";
    public final static String TIMED_REBECA_BFS_HASHMAP_OUTPUT_CPP = "TimedRebecaBFSHashmap.cpp";
    public final static String TIMED_REBECA_BFS_HASHMAP_CPP_TEMPLATE = ANALYZER_BASE + "TimedRebecaBFSHashmapCPPTemplate.vm";
    //
	
	public static final String TIMED_MODEL_CHECKER_HEADER_TEMPLATE = ANALYZER_BASE + "TimedModelCheckerHeaderTemplate.vm";
	public static final String TIMED_MODEL_CHECKER_OUTPUT_HEADER = "TimedModelChecker.h";
	public static final String TIMED_MODEL_CHECKER_CPP_TEMPLATE = ANALYZER_BASE + "TimedModelCheckerCPPTemplate.vm";
	public static final String TIMED_MODEL_CHECKER_OUTPUT_CPP = "TimedModelChecker.cpp";

	public static final String FTTS_PATCH_TEMPLATE = ANALYZER_BASE + "FTTSPatchTemplate.vm";
	public static final String TTS_PATCH_TEMPLATE = ANALYZER_BASE + "TTSPatchTemplate.vm";

	public static final String ABSTRACT_TIMED_REBECA_ANALYZER_HEADER_TEMPLATE = ANALYZER_BASE + "AbstractTimedRebecaAnalyzerHeaderTemplate.vm";
	public static final String ABSTRACT_TIMED_REBECA_ANALYZER_OUTPUT_HEADER = "AbstractTimedRebecaAnalyzer.h";
	public static final String ABSTRACT_TIMED_REBECA_ANALYZER_CPP_TEMPLATE = ANALYZER_BASE + "AbstractTimedRebecaAnalyzerCPPTemplate.vm";
	public static final String ABSTRACT_TIMED_REBECA_ANALYZER_OUTPUT_CPP = "AbstractTimedRebecaAnalyzer.cpp";

	public static final String ABSTRACT_TIMED_TRACE_GENERATOR_HEADER_TEMPLATE = ANALYZER_BASE + "AbstractTimedTraceGeneratorHeaderTemplate.vm";
	public static final String ABSTRACT_TIMED_TRACE_GENERATOR_OUTPUT_HEADER = "AbstractTimedTraceGenerator.h";
	public static final String ABSTRACT_TIMED_TRACE_GENERATOR_CPP_TEMPLATE = ANALYZER_BASE + "AbstractTimedTraceGeneratorCPPTemplate.vm";
	public static final String ABSTRACT_TIMED_TRACE_GENERATOR_OUTPUT_CPP = "AbstractTimedTraceGenerator.cpp";

	public static final String MAIN_PATCH_TEMPLATE = BASE + "MainPatch.vm";
	public static final String CONFIG_PATCH_TEMPLATE = BASE + "ConfigPatchTemplate.vm";

}