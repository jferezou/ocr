package com.perso.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

			// on lance un traitement parallèle
			Function<Path, List<ResultatPdf>> myfunction = (a -> this.traitement(a));
			List<List<ResultatPdf>> finalResults = IntStream.range(0, paths.size()).parallel().mapToObj(i -> myfunction.apply(paths.get(i))).collect(Collectors.toList());

			// on ecrit les résultats
			this.ecritureResultat(finalResults);
		}

	}

	/**
	 * Traitement multithread à l'aide des Future
	 * 
	 * @param path
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private List<ResultatPdf> traitement(Path path) {
		List<ResultatPdf> resultList = new ArrayList<>();
		if(Files.isRegularFile(path)) {
			try {
				File file = path.toFile();
				boolean isPdf = checkIsPdfFile(file);

				if (isPdf) {
					// traitement principal
					resultList = this.transformService.extract(file);
				}
			} catch (TikaException e) {
				LOGGER.error("Erreur lors du traitement du fichier", e);
			} catch (IOException e) {
				LOGGER.error("Erreur lors du traitement du fichier", e);
			}
		}
		return resultList;
	}


	private boolean checkIsPdfFile(File file) throws TikaException, IOException {
		boolean isPdf = true;
		InputStream stream = new FileInputStream(file);
		InputStream bufferedInputstream = new BufferedInputStream(stream);
		MediaType fileInfo = this.getFileInfo(file.getName(), bufferedInputstream);
		if (!this.APPLICATION_PDF.equals(fileInfo.getBaseType())) {
            LOGGER.warn("Le fichier n'est pas traité car il doit être au format " + APPLICATION_PDF + " : " + file.getPath());
            isPdf = false;
        }
        return isPdf;
	}

	private void ecritureResultat(List<List<ResultatPdf>> resultList) throws IOException {

		try (final FileWriter fw = new FileWriter(this.fichierResultat)) {
			fw.append("sep=;");
			fw.append("\n");
            CSVUtils.writeLine(fw, Arrays.asList("Echantillon", "Dominant","Erreur", "Accompagnement","Erreur", "Isole","Erreur", "interpretation"));
            // on écrit les résultats dans le fichier
			for(List<ResultatPdf> premiereListe : resultList) {
				for (ResultatPdf resultatPdf : premiereListe) {
					CSVUtils.writeResult(fw, resultatPdf);
				}
			}
        }
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
