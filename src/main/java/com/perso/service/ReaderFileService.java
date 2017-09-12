package com.perso.service;

import java.io.IOException;
import java.util.List;

import com.perso.utils.ResponseTraitement2;
import com.perso.utils.ResultatPdf;
import org.apache.tika.exception.TikaException;
import com.perso.exception.FichierInvalideException;

public interface ReaderFileService {

	/**
	 * Méthode lancant la lecture du fichier et la conversion de chaque ligne
	 */
	List<ResultatPdf> readAndLaunch() throws FichierInvalideException,TikaException, IOException ;


	List<ResponseTraitement2> readAndLaunchT2() throws FichierInvalideException,TikaException, IOException ;
}
