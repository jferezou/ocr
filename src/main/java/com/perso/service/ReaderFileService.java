package com.perso.service;

import java.io.IOException;
import java.util.List;

import com.perso.utils.ResultatPdf;
import org.apache.tika.exception.TikaException;
import com.perso.exception.FichierInvalideException;

@FunctionalInterface
public interface ReaderFileService {

	/**
	 * Méthode lancant la lecture du fichier et la conversion de chaque ligne
	 */
	List<ResultatPdf> readAndLaunch() throws FichierInvalideException,TikaException, IOException ;
}
