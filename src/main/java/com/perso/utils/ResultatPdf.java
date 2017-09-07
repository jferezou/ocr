package com.perso.utils;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ResultatPdf implements Serializable {

	private static int IDENTIFIANT = 0;

	/** Numéro de sérialisation */
	private static final long serialVersionUID = -1;

	private String pdfFilePath;
	private String pdfFileName;
	private String echantillon;
	private int id;
	private List<CompositionObj> compositions;

	public ResultatPdf() {
		IDENTIFIANT++;
		this.id = new Integer(IDENTIFIANT);
	}
}
