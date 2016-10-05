package org.rebecalang.rmc;

public enum AnalysisFeature {
	//General
	TRACE_GENERATOR, DEBUG, DEBUG_LEVEL_2, EXPORT_STATE_SPACE, SAFE_MODE, SIMPLIFIED_STATESPACE,
	//Timed Systems
	RT_MAUDE, TTS, COMPACT_DTG,
	
	//Unused 
	SHALIFIER, SYSFIER, 
	PARTIAL_ORDER_REDUCTION, INTER_REBEC_SYMMETRY_REDUCTION, INTRA_REBEC_SYMMETRY_REDUCTION, 
    SIMULATOR, DISTRIBUTED_BFS, DISTRIBUTED_BFS_SERVER, STATE_DISTRIBUTION_INFORMATION, 
    
}
