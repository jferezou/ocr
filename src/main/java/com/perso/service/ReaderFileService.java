package com.perso.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.perso.utils.response.ListPdfIdResponse;
import org.apache.tika.exception.TikaException;
import com.perso.exception.FichierInvalideException;

public interface ReaderFileService {

	/**
	 * Méthode lancant la lecture du fichier et la conversion de chaque ligne
	 */
	List<ListPdfIdResponse> readAndLaunchPalynologie() throws FichierInvalideException,TikaException, IOException ;


	List<ListPdfIdResponse> readAndLaunchResidus() throws FichierInvalideException,TikaException, IOException ;
	File readAndLaunchAggregatePdf() throws FichierInvalideException, TikaException, IOException;
}
