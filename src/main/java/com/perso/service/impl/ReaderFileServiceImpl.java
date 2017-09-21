package com.perso.service.impl;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import com.perso.service.*;
import com.perso.pojo.residus.ResidusDocument;
import com.perso.utils.AggregatePdf;
import com.perso.utils.response.ListPdfIdResponse;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.perso.exception.FichierInvalideException;
import com.perso.pojo.palynologie.PalynologieDocument;

import javax.annotation.Resource;

@Service
public class ReaderFileServiceImpl implements ReaderFileService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReaderFileServiceImpl.class);


	@Value("${dossier.entrant}")
	private String dossierEntrant;
	@Value("${dossier.temporaire}")
	private String tempDirectory;
	@Value("${dossier.palynologie}")
	private String palynologieDir;
	@Value("${dossier.residus}")
	private String residusDir;

	@Resource
	private GenerateImageService generateImageService;

	@Resource
	private PdfService pdfService;

	@Resource
	PalynologieExtractorService palynologieExtractorService;

	@Resource
	ResidusExtractorService residusExtractorService;
	@Resource
	UpdatedValuesService updatedValuesService;

	@Override
	public Set<ListPdfIdResponse> readAndLaunchPalynologie() throws FichierInvalideException, TikaException, IOException {
		LOGGER.info("Début du traitement");
		String palynologieDir = this.dossierEntrant +"\\"+this.palynologieDir;
		// Vérifie que le fichier existe
		File file = new File(palynologieDir);
		List<PalynologieDocument> finalResults = new ArrayList<>();
		if (!file.exists()) {
			throw new FichierInvalideException("Ce répertoire n'existe pas : " + palynologieDir);
		}
		// Vérifie que ce n'est pas un répertoire
		else if (!file.isDirectory()) {
			throw new FichierInvalideException("Ce doit être un répertoire : " + palynologieDir);
		}
		// verifie que ce soit bien un txt
		else {
			// on recupère la liste de fichiers
			List<Path> paths = Files.walk(Paths.get(palynologieDir)).collect(Collectors.toList());

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
						.map(pngFile -> this.palynologieExtractorService.extract(pngFile))
						.collect(Collectors.toList());

			}
			else {
				LOGGER.error("Le répertoire temporaire n'est pas un répertoire : {}", this.tempDirectory);
			}
		}
		this.updatedValuesService.fillPalynologieMap(finalResults);
		Set<ListPdfIdResponse> returnValue = new HashSet<>();
		for(PalynologieDocument result : this.updatedValuesService.getValeursPalynologie().values()) {
			ListPdfIdResponse listPdfIdResponse = new ListPdfIdResponse();
			listPdfIdResponse.setId(result.getId());
			listPdfIdResponse.setPdfFilePath(result.getPdfFilePath());
			returnValue.add(listPdfIdResponse);
		}
		return returnValue;
	}



	@Override
	public Set<ListPdfIdResponse> readAndLaunchResidus() throws FichierInvalideException, TikaException, IOException {
		LOGGER.info("Début du traitement résidus");
		String residusDir = this.dossierEntrant +"\\"+this.residusDir;
		// Vérifie que le fichier existe
		File file = new File(residusDir);
		List<ResidusDocument> response = new ArrayList<>();
		if (!file.exists()) {
			throw new FichierInvalideException("Ce répertoire n'existe pas : " + residusDir);
		}
		// Vérifie que ce n'est pas un répertoire
		else if (!file.isDirectory()) {
			throw new FichierInvalideException("Ce doit être un répertoire : " + residusDir);
		}
		else {
			List<Path> paths = Files.walk(Paths.get(residusDir)).collect(Collectors.toList());
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
					.map(pdfFile -> this.residusExtractorService.extraire(pdfFile))
					.collect(Collectors.toList());

			}
			else {
				LOGGER.error("Le répertoire temporaire n'est pas un répertoire : {}", this.tempDirectory);
			}
		}
		this.updatedValuesService.fillResidusMap(response);
		Set<ListPdfIdResponse> returnValue = new HashSet<>();
		for(ResidusDocument result : this.updatedValuesService.getValeursResidus().values()) {
			ListPdfIdResponse listPdfIdResponse = new ListPdfIdResponse();
			listPdfIdResponse.setId(result.getId());
			listPdfIdResponse.setPdfFilePath(result.getPdfFilePath());
			returnValue.add(listPdfIdResponse);
		}
		return returnValue;
	}

	@Override
	public File readAndLaunchAggregatePdf() throws FichierInvalideException, TikaException, IOException {
		LOGGER.info("Début du traitement résidus");
		File pdfResultFile = null;
		String residusDir = this.dossierEntrant +"\\"+this.residusDir;
		// Vérifie que le fichier existe
		File file = new File(residusDir);
		if (!file.exists()) {
			throw new FichierInvalideException("Ce répertoire n'existe pas : " + residusDir);
		}
		// Vérifie que ce n'est pas un répertoire
		else if (!file.isDirectory()) {
			throw new FichierInvalideException("Ce doit être un répertoire : " + residusDir);
		}
		else {
			List<Path> paths = Files.walk(Paths.get(residusDir)).collect(Collectors.toList());
			File tempsDir = new File(this.tempDirectory);
			if(tempsDir.isDirectory()) {
				// on supprime le contenu du temp dir
				for(File fileTemp : tempsDir.listFiles()) {
					fileTemp.delete();
				}

				// pour chaque fichier, on supprime les 5 dernieres pages et on le met dans le temps dir
				paths.stream().filter(path -> Files.isRegularFile(path)).forEach(path -> this.pdfService.deletePages(path));

				// on lance un traitement
				List<Path> pathsTemp = Files.walk(Paths.get(this.tempDirectory)).collect(Collectors.toList());

				List<AggregatePdf> pdfList = pathsTemp.stream()
						.filter(myPath -> Files.isRegularFile(myPath))
						.filter(myPath -> this.pdfService.checkIfPdf(myPath.toFile()))
						.map(pdfFile -> this.residusExtractorService.extraireDate(pdfFile)).collect(Collectors.toList());
				Collections.sort(pdfList);

				pdfResultFile = this.pdfService.createPdf(pdfList);
			}
			else {
				LOGGER.error("Le répertoire temporaire n'est pas un répertoire : {}", this.tempDirectory);
			}
		}
		return pdfResultFile;
	}
}
