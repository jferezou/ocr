package com.perso.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.perso.utils.ResponseTraitement2;
import org.apache.tika.exception.TikaException;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.perso.exception.FichierInvalideException;
import com.perso.utils.ResultatPdf;

import javax.annotation.Resource;

@Service("ReaderFileService")
public class ReaderFileServiceImpl implements ReaderFileService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReaderFileServiceImpl.class);
	
	private MediaType APPLICATION_PNG = MediaType.parse("image/png");

	@Value("${dossier.entrant}")
	private String inputDirectory;
	@Value("${dossier.temporaire}")
	private String tempDirectory;

	@Resource
	private GenerateImageService generateImageService;

	@Resource
	private PdfService pdfService;

	@Resource
	TransformService transformService;

	@Resource
	TraitementT2Service traitementT2Service;

	@Override
	public List<ResultatPdf> readAndLaunch() throws FichierInvalideException, TikaException, IOException {
		LOGGER.info("Début du traitement");
		// Vérifie que le fichier existe
		File file = new File(this.inputDirectory);
		String fileName = file.getName();
		List<ResultatPdf> finalResults = new ArrayList<>();
		if (!file.exists()) {
			throw new FichierInvalideException("Ce répertoire n'existe pas : " + this.inputDirectory);
		}
		// Vérifie que ce n'est pas un répertoire
		else if (!file.isDirectory()) {
			throw new FichierInvalideException("Ce doit être un répertoire : " + this.inputDirectory);
		}
		// verifie que ce soit bien un txt
		else {
			// on recupère la liste de fichiers
			List<Path> paths = Files.walk(Paths.get(this.inputDirectory)).collect(Collectors.toList());

			File tempsDir = new File(this.tempDirectory);
			if(tempsDir.isDirectory()) {
				// on supprime le contenu du temp dir
				for(File fileTemp : tempsDir.listFiles()) {
					fileTemp.delete();
				}

				// pour chaque fichier, on les split en page dans le temp dir
				for(Path path : paths) {
					this.pdfService.splitPdf(path);
				}

				// on lance un traitement
				List<Path> pathsTemp = Files.walk(Paths.get(this.tempDirectory)).collect(Collectors.toList());
				List<File> pngFileList = pathsTemp.stream()
						.filter(myPath -> Files.isRegularFile(myPath))
						.map(path -> this.generateImageService.generatePng(path))
						.collect(Collectors.toList());

				finalResults = pngFileList.stream()
						.filter(myFile -> myFile.isFile())
						.filter(myFile -> this.generateImageService.checkIfPng(myFile))
						.map(pngFile -> this.transformService.extract(pngFile))
						.collect(Collectors.toList());

			}
			else {
				LOGGER.error("Le répertoire temporaire n'est pas un répertoire : {}", this.tempDirectory);
			}
		}
		return finalResults;
	}



	@Override
	public List<ResponseTraitement2> readAndLaunchT2() throws FichierInvalideException, TikaException, IOException {
		LOGGER.info("Début du traitement t2");
		// Vérifie que le fichier existe
		File file = new File(this.inputDirectory);
		List<ResponseTraitement2> response = new ArrayList<>();
		if (!file.exists()) {
			throw new FichierInvalideException("Ce répertoire n'existe pas : " + this.inputDirectory);
		}
		// Vérifie que ce n'est pas un répertoire
		else if (!file.isDirectory()) {
			throw new FichierInvalideException("Ce doit être un répertoire : " + this.inputDirectory);
		}
		else {
			// on lance un traitement
			List<Path> pathsTemp = Files.walk(Paths.get(this.inputDirectory)).collect(Collectors.toList());

			response = pathsTemp.stream()
					.filter(myPath -> Files.isRegularFile(myPath))
					.filter(myPath -> this.pdfService.checkIfPdf(myPath.toFile()))
					.map(pdfFile -> this.traitementT2Service.extraire(pdfFile))
					.collect(Collectors.toList());
		}
		return response;
	}



}
