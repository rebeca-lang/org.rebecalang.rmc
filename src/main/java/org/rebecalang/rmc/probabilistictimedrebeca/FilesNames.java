package org.rebecalang.rmc.probabilistictimedrebeca;

public interface FilesNames {
	public final static String BASE = "vtl/probabilistictimedrebeca/";
	public final static String ACTOR_BASE = BASE +"actor/";
	public final static String ANALYZER_BASE = BASE + "analyzer/";
	public final static String PROBABILISTIC_BASE = "vtl/probabilisticrebeca/";

    public final static  String REACTIVE_CLASS_PATCH_TEMPLATE = PROBABILISTIC_BASE + "ReactiveClassPatchTemplate.vm";

    public static final String MAIN_PATCH_TEMPLATE = BASE + "MainPatch.vm";

    public final static String PROBABILISTIC_TIMED_MODEL_CHECKER_OUTPUT_HEADER = "ProbabilisticTimedModelChecker.h";
    public final static String PROBABILISTIC_TIMED_MODEL_CHECKER_HEADER_TEMPLATE = BASE + "ProbabilisticTimedModelCheckerHeaderTemplate.vm";
    public final static String PROBABILISTIC_TIMED_MODEL_CHECKER_OUTPUT_CPP = "ProbabilisticTimedModelChecker.cpp";
    public final static String PROBABILISTIC_TIMED_MODEL_CHECKER_CPP_TEMPLATE = BASE + "ProbabilisticTimedModelCheckerCPPTemplate.vm";

}
