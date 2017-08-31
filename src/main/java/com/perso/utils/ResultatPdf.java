package com.perso.utils;

public class ResultatPdf {

	private String echantillon;
	private String interpretation;
	private String zone1;
	public String getEchantillon() {
		return echantillon;
	}
	public void setEchantillon(String echantillon) {
		this.echantillon = echantillon;
	}
	public String getInterpretation() {
		return interpretation;
	}
	public void setInterpretation(String interpretation) {
		this.interpretation = interpretation;
	}
	public String getZone1() {
		return zone1;
	}
	public void setZone1(String zone1) {
		this.zone1 = zone1;
	}
	@Override
	public String toString() {
		return "ResultatPdf [echantillon=" + echantillon + ", zone1=" + zone1 + ", interpretation=" + interpretation + "]";
	}

	
	
	
	
	
}
