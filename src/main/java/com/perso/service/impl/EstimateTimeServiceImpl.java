package com.perso.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.perso.service.EstimateTimeService;
import com.perso.service.PdfService;
import com.perso.utils.EstimateTime;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstimateTimeServiceImpl implements EstimateTimeService {

    @Value("${dossier.traitement1}")
    private String traitement1Directory;
    @Value("${dossier.traitement2}")
    private String traitement2Directory;
    @Value("${dossier.entrant}")
    private String filePath;
    @Resource
    PdfService pdfService;

    private static final Logger LOGGER = LoggerFactory.getLogger(EstimateTimeServiceImpl.class);

    @Override
    public EstimateTime estimateTime(final String multiplicateur, final boolean ist1) {
        int nbPage = 0;
        Date date = new Date();
        try {
            // on recupère la liste de fichiers
            String fileP = this.filePath+"\\"+this.traitement2Directory;
            if(ist1) {
                fileP = this.filePath+"\\"+this.traitement1Directory;
            }
            List<Path> paths = Files.walk(Paths.get(fileP)).collect(Collectors.toList());
            for (Path path : paths) {
                if (Files.isRegularFile(path)) {
                    File inFile = path.toFile();
                    boolean isPdf = this.pdfService.checkIfPdf(inFile);
                    if (isPdf && inFile.isFile()) {
                        PdfDocument pdfDoc = new PdfDocument(new PdfReader(inFile.getPath()));
                        nbPage = nbPage + pdfDoc.getNumberOfPages();
                        pdfDoc.close();
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Erreur lors du traitement du fichier", e);
        }

        EstimateTime estimateTime = new EstimateTime();
        int mult = Integer.parseInt(multiplicateur);
        estimateTime.setMinutes(Math.round((nbPage * mult)/60)+1);
        date = DateUtils.addMinutes(date, estimateTime.getMinutes());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        estimateTime.setEstimatedDate(df.format(date));
        return  estimateTime;
    }
}
