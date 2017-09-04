package com.perso.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResultatPdf {

	private String pdfFilePath;
	private String echantillon;
	private List<CompositionObj> compositions;

}
