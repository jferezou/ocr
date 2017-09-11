package com.perso.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.perso.utils.EstimateTime;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
public class PdfServiceImpl implements PdfService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfServiceImpl.class);
    private MediaType applicationPdf = MediaType.parse("application/pdf");
    @Value("${dossier.temporaire}")
    private String tempDir;

    @Value("${dossier.entrant}")
    private String filePath;
    @Autowired
    private FileServiceImpl fileServiceImpl;


    private boolean checkIfPdf(final File file) throws TikaException, IOException {
        boolean isPdf = this.fileServiceImpl.checkFileType(file, this.applicationPdf);
        return isPdf;
    }

    @Override
    public void splitPdf(final Path path) {
        if (Files.isRegularFile(path)) {
            try {
                File inFile = path.toFile();
                boolean isPdf = this.checkIfPdf(inFile);
                if (isPdf && inFile.isFile()) {
                    PdfDocument pdfDoc = new PdfDocument(new PdfReader(inFile.getPath()));
                    int nbPages = pdfDoc.getNumberOfPages();
                    String fileName = inFile.getName().split(".pdf")[0];

                    for (int page = 1; page <= nbPages; page++) {
                        String name = this.tempDir + "//" + fileName + "_" + page + ".pdf";
                        PdfDocument newPdfDoc = new PdfDocument(new PdfWriter(name));
                        pdfDoc.copyPagesTo(page, page, newPdfDoc);
                        newPdfDoc.close();

                    }
                    pdfDoc.close();
                }
            } catch (TikaException | IOException e) {
                LOGGER.error("Erreur lors du traitement du fichier", e);
            }
        }
    }


    @Override
    public EstimateTime estimateTime() {
        int nbPage = 0;
        Date date = new Date();
        try {
            // on recupère la liste de fichiers
            List<Path> paths = Files.walk(Paths.get(this.filePath)).collect(Collectors.toList());
            for (Path path : paths) {
                if (Files.isRegularFile(path)) {
                        File inFile = path.toFile();
                        boolean isPdf = this.checkIfPdf(inFile);
                        if (isPdf && inFile.isFile()) {
                            PdfDocument pdfDoc = new PdfDocument(new PdfReader(inFile.getPath()));
                            nbPage = nbPage + pdfDoc.getNumberOfPages();
                            pdfDoc.close();
                        }
                }
            }
        } catch (TikaException | IOException e) {
            LOGGER.error("Erreur lors du traitement du fichier", e);
        }

        EstimateTime estimateTime = new EstimateTime();
        estimateTime.setMinutes(Math.round((nbPage * 6)/60)+1);
        date = DateUtils.addMinutes(date, estimateTime.getMinutes());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        estimateTime.setEstimatedDate(df.format(date));
        return  estimateTime;
    }
}
