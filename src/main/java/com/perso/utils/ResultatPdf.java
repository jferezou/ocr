package com.perso.utils;

import java.util.List;

public class ResultatPdf {

	private String echantillon;
	private String interpretation;
	private List<String> dominant;
	private List<String> accompagnement;
	private List<String> isole;
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
	@Override
	public String toString() {
		return this.echantillon+";"+ this.dominant+";"+ this.accompagnement+";"+ this.isole +";"+this.interpretation;
	}
	public List<String> getDominant() {
		return dominant;
	}
	public void setDominant(List<String> dominant) {
		this.dominant = dominant;
	}
	public List<String> getAccompagnement() {
		return accompagnement;
	}
	public void setAccompagnement(List<String> accompagnement) {
		this.accompagnement = accompagnement;
	}
	public List<String> getIsole() {
		return isole;
	}
	public void setIsole(List<String> isole) {
		this.isole = isole;
	}

	
	
	
	
	
}
