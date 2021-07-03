package org.rebecalang.rmc;

import org.rebecalang.compiler.utils.CoreVersion;

public class FileGeneratorProperties {
	protected boolean safeMode;
	protected boolean reduction;
	
	protected String exportStateSpaceTargetFile;
	
	protected boolean debugTrace;
	protected boolean debugAll;
	
	protected boolean progressReport;
	
	protected CoreVersion coreVersion;
	
	protected boolean traceGenerator;
	
	public boolean isSafeMode() {
		return safeMode;
	}
	public void setSafeMode(boolean safeMode) {
		this.safeMode = safeMode;
	}
	
	public boolean isReduction() {
		return reduction;
	}
	public void setReduction(boolean reduction) {
		this.reduction = reduction;
	}
	
	public boolean isDebugTrace() {
		return debugTrace;
	}
	public void setDebugTrace(boolean debugTrace) {
		this.debugTrace = debugTrace;
	}
	
	public boolean isDebugAll() {
		return debugAll;
	}
	public void setDebugAll(boolean debugAll) {
		this.debugAll = debugAll;
	}
	
	public CoreVersion getCoreVersion() {
		return coreVersion;
	}
	public void setCoreVersion(CoreVersion coreVersion) {
		this.coreVersion = coreVersion;
	}
	
	public boolean isExportStateSpace() {
		return exportStateSpaceTargetFile != null;
	}
	public void setExportStateSpaceTargetFile(String exportStateSpace) {
		this.exportStateSpaceTargetFile = exportStateSpace;
	}
	
	public boolean isProgressReport() {
		return progressReport;
	}
	public void setProgressReport(boolean progressReport) {
		this.progressReport = progressReport;
	}
	
	public boolean isTraceGenerator() {
		return traceGenerator;
	}
	public void setTraceGenerator(boolean traceGenerator) {
		this.traceGenerator = traceGenerator;
	}
}