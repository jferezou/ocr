package com.perso.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.perso.utils.ResponseTraitement2;
import com.perso.utils.ResultatPdf;
import com.perso.utils.response.ListPdfIdResponse;
import org.apache.tika.exception.TikaException;
import com.perso.exception.FichierInvalideException;

public interface ReaderFileService {

	/**
	 * Méthode lancant la lecture du fichier et la conversion de chaque ligne
	 */
	Set<ListPdfIdResponse> readAndLaunch() throws FichierInvalideException,TikaException, IOException ;


	Set<ListPdfIdResponse>  readAndLaunchT2() throws FichierInvalideException,TikaException, IOException ;
}
