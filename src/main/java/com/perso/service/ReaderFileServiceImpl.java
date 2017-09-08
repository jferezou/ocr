package com.perso.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
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
import com.perso.utils.CSVUtils;
import com.perso.utils.ResultatPdf;

@Service("ReaderFileService")
public class ReaderFileServiceImpl implements ReaderFileService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReaderFileServiceImpl.class);
	
	private MediaType APPLICATION_PNG = MediaType.parse("image/png");

	@Value("${dossier.entrant}")
	private String filePath;
	@Value("${fichier.resultat}")
	private String fichierResultat;
	@Value("${dossier.temporaire}")
	private String tempDir;

	@Autowired
	private GenerateImageService generateImageService;

	@Autowired
	private PdfService pdfService;

	@Autowired
	TransformService transformService;

	@Override
	public List<ResultatPdf> readAndLaunch() throws FichierInvalideException, TikaException, IOException {
		LOGGER.info("Début du traitement");
		// Vérifie que le fichier existe
		File file = new File(this.filePath);
		String fileName = file.getName();
		List<ResultatPdf> finalResults = new ArrayList<>();
		if (!file.exists()) {
			throw new FichierInvalideException("Ce répertoire n'existe pas : " + this.filePath);
		}
		// Vérifie que ce n'est pas un répertoire
		else if (!file.isDirectory()) {
			throw new FichierInvalideException("Ce doit être un répertoire : " + this.filePath);
		}
		// verifie que ce soit bien un txt
		else {
			// on recupère la liste de fichiers
			List<Path> paths = Files.walk(Paths.get(this.filePath)).collect(Collectors.toList());

			File tempsDir = new File(this.tempDir);
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
				List<Path> pathsTemp = Files.walk(Paths.get(this.tempDir)).collect(Collectors.toList());
				List<File> pngFileList = pathsTemp.stream().filter(myPath -> Files.isRegularFile(myPath)).map(path -> this.generateImageService.generatePng(path)).collect(Collectors.toList());

				finalResults = pngFileList.stream().filter(myFile -> myFile.isFile()).map(pngFile -> this.traitement(pngFile)).collect(Collectors.toList());

				// on ecrit les résultats
//				this.ecritureResultat(finalResults);
			}
			else {
				LOGGER.error("Le répertoire temporaire n'est pas un répertoire : {}", this.tempDir);
			}
		}
		return finalResults;
	}


	/**
	 * Traitement multithread à l'aide des Future
	 * 
	 * @param file
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private ResultatPdf traitement(File file) {
		ResultatPdf result = new ResultatPdf();
		try {
			boolean isPng = this.generateImageService.checkIfPng(file);
			if (isPng) {
				// traitement principal
				LOGGER.debug("Lancement du traiement du fichier {}", file.getPath());
				result = this.transformService.extract(file);
			}
		} catch (TikaException e) {
			LOGGER.error("Erreur lors du traitement du fichier", e);
		} catch (IOException e) {
			LOGGER.error("Erreur lors du traitement du fichier", e);
		}
		return result;
	}



	private void ecritureResultat(List<ResultatPdf> resultList) throws IOException {

		try (final FileWriter fw = new FileWriter(this.fichierResultat)) {
			fw.append("sep=;");
			fw.append("\n");
            CSVUtils.writeLine(fw, Arrays.asList("Echantillon", "Composition","Pourcentage", "Type"));
            // on écrit les résultats dans le fichier
			for (ResultatPdf resultatPdf : resultList) {
					CSVUtils.writeResult(fw, resultatPdf);
			}
        }
	}


}
