package com.perso.service;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.perso.utils.response.ListPdfIdResponse;
import org.apache.tika.exception.TikaException;
import com.perso.exception.FichierInvalideException;

public interface ReaderFileService {

	/**
	 * Méthode lancant la lecture du fichier et la conversion de chaque ligne
	 */
	Set<ListPdfIdResponse> readAndLaunchPalynologie() throws FichierInvalideException,TikaException, IOException ;


	Set<ListPdfIdResponse> readAndLaunchResidus() throws FichierInvalideException,TikaException, IOException ;
	File readAndLaunchAggregatePdf() throws FichierInvalideException, TikaException, IOException;
}
