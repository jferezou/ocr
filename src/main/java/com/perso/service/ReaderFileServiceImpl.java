package com.perso.service;

import java.io.*;
import java.nio.file.*;
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
	@Value("${dossier.traitement1}")
	private String traitement1Directory;
	@Value("${dossier.traitement2}")
	private String traitement2Directory;

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
		String t1Dir = this.inputDirectory+"\\"+this.traitement1Directory;
		// Vérifie que le fichier existe
		File file = new File(t1Dir);
		List<ResultatPdf> finalResults = new ArrayList<>();
		if (!file.exists()) {
			throw new FichierInvalideException("Ce répertoire n'existe pas : " + t1Dir);
		}
		// Vérifie que ce n'est pas un répertoire
		else if (!file.isDirectory()) {
			throw new FichierInvalideException("Ce doit être un répertoire : " + t1Dir);
		}
		// verifie que ce soit bien un txt
		else {
			// on recupère la liste de fichiers
			List<Path> paths = Files.walk(Paths.get(t1Dir)).collect(Collectors.toList());

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
		String t2Dir = this.inputDirectory+"\\"+this.traitement2Directory;
		// Vérifie que le fichier existe
		File file = new File(t2Dir);
		List<ResponseTraitement2> response = new ArrayList<>();
		if (!file.exists()) {
			throw new FichierInvalideException("Ce répertoire n'existe pas : " + t2Dir);
		}
		// Vérifie que ce n'est pas un répertoire
		else if (!file.isDirectory()) {
			throw new FichierInvalideException("Ce doit être un répertoire : " + t2Dir);
		}
		else {
			List<Path> paths = Files.walk(Paths.get(t2Dir)).collect(Collectors.toList());
			File tempsDir = new File(this.tempDirectory);
			if(tempsDir.isDirectory()) {
				// on supprime le contenu du temp dir
				for(File fileTemp : tempsDir.listFiles()) {
					fileTemp.delete();
				}

				// pour chaque fichier, on les split en page dans le temp dir
				for(Path path : paths) {
					if(Files.isRegularFile(path)) {
						Path targetDir = Paths.get(this.tempDirectory+"\\"+path.getFileName());
						Files.copy(path, targetDir, StandardCopyOption.REPLACE_EXISTING);
					}
				}
			// on lance un traitement
			List<Path> pathsTemp = Files.walk(Paths.get(this.tempDirectory)).collect(Collectors.toList());

			response = pathsTemp.stream()
					.filter(myPath -> Files.isRegularFile(myPath))
					.filter(myPath -> this.pdfService.checkIfPdf(myPath.toFile()))
					.map(pdfFile -> this.traitementT2Service.extraire(pdfFile))
					.collect(Collectors.toList());

			}
			else {
				LOGGER.error("Le répertoire temporaire n'est pas un répertoire : {}", this.tempDirectory);
			}
		}
		return response;
	}



}
