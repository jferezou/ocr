package com.perso.utils;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ResultatPdf implements Serializable {

	/** Numéro de sérialisation */
	private static final long serialVersionUID = -1;

	private String pdfFilePath;
	private String echantillon;
	private List<CompositionObj> compositions;

}
