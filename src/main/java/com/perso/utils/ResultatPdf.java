package com.perso.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResultatPdf {

	private String pdfFilePath;
	private String echantillon;
	private String interpretation;
	private List<ZoneDroiteObj> dominant;
	private List<ZoneDroiteObj> accompagnement;
	private List<ZoneDroiteObj> isole;


	@Override
	public String toString() {
		return this.echantillon+";"+ this.dominant+";"+ this.accompagnement+";"+ this.isole +";"+this.interpretation;
	}

	
	
	
	
	
}
