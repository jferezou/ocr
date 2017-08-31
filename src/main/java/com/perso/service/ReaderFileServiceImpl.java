package com.perso.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.perso.exception.FichierInvalideException;

@Service("ReaderFileService")
public class ReaderFileServiceImpl implements ReaderFileService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReaderFileServiceImpl.class);
	
	private MediaType APPLICATION_PDF = MediaType.parse("application/pdf");

	@Value("${fichier}")
	private String filePath;
	@Value("${fichierResultat}")
	private String fichierResultat;

	@Value("${monoThread}")
	private boolean monoThreaded;
	@Value("${treadNumber}")
	private int treadNumber;

	@Autowired
	TransformService transformService;

	@Override
	public void readAndLaunch() throws FichierInvalideException, TikaException, IOException {
		LOGGER.info("Début du traitement");
		// Vérifie que le fichier existe
		File file = new File(this.filePath);
		String fileName = file.getName();
		if (!file.exists()) {
			throw new FichierInvalideException("Ce fichier n'existe pas : " + this.filePath);
		}
		// Vérifie que ce n'est pas un répertoire
		else if (file.isDirectory()) {
			throw new FichierInvalideException("C'est un répertoire : " + this.filePath);
		}
		// verifie que ce soit bien un txt
		else {
			InputStream stream = new FileInputStream(file);
			InputStream bufferedInputstream = new BufferedInputStream(stream);
			MediaType fileInfo = this.getFileInfo(fileName, bufferedInputstream);
			if (!this.APPLICATION_PDF.equals(fileInfo.getBaseType())) {
				throw new FichierInvalideException("Le fichier doit être au format " + MediaType.TEXT_PLAIN + " : " + this.filePath);
			}
			// si c'est ok on continue
			else {
				// on créé notre fichier de sortie
				this.multiThreaded(file);
			}
		}

	}

	/**
	 * Traitement multithread à l'aide des Future
	 * 
	 * @param file
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void multiThreaded(File file) throws IOException {

		this.transformService.extract(file);
	}

	/**
	 * Méthode utilisant apache Tika pour récupérer les infos du fichier :
	 * mimetype, charset ....
	 * 
	 * @param fileName
	 * @param stream
	 * @return
	 * @throws TikaException
	 * @throws IOException
	 */
	private MediaType getFileInfo(final String fileName, final InputStream stream) throws TikaException, IOException {
		TikaConfig tika = new TikaConfig();
		Metadata metadata = new Metadata();
		metadata.set(Metadata.RESOURCE_NAME_KEY, fileName);
		return tika.getDetector().detect(stream, metadata);
	}

}
