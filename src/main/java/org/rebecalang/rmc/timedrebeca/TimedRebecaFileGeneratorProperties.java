package org.rebecalang.rmc.timedrebeca;

import org.rebecalang.rmc.FileGeneratorProperties;

public class TimedRebecaFileGeneratorProperties extends FileGeneratorProperties {
	
	protected boolean TTS;
	
	protected boolean compactDTG;
	
	public TimedRebecaFileGeneratorProperties(FileGeneratorProperties fileGenerationProperties) {
		this.safeMode = fileGenerationProperties.isSafeMode();
		this.reduction = fileGenerationProperties.isReduction();
		this.coreVersion = fileGenerationProperties.getCoreVersion();
		
		this.debugTrace = fileGenerationProperties.isDebugTrace();
		this.debugAll = fileGenerationProperties.isDebugAll();
	}
	
	public TimedRebecaFileGeneratorProperties() {
		
	}
	
	public boolean isTTS() {
		return TTS;
	}
	public void setTTS(boolean TTS) {
		this.TTS = TTS;
	}
	
	public boolean isCompactDTG() {
		return compactDTG;
	}
	public void setCompactDTG(boolean compactDTG) {
		this.compactDTG = compactDTG;
	}

}
