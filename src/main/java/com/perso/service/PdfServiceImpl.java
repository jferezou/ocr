package com.perso.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
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

@Service
public class PdfServiceImpl implements PdfService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfServiceImpl.class);
    private MediaType applicationPdf = MediaType.parse("application/pdf");
    @Value("${dossier.temporaire}")
    private String tempDir;

    @Autowired
    private FileServiceImpl fileServiceImpl;


    private boolean checkIfPdf(final File file) throws TikaException, IOException {
        boolean isPdf = this.fileServiceImpl.checkFileType(file, this.applicationPdf);
        return isPdf;
    }

    @Override
    public void splitPdf(final Path path){
        if(Files.isRegularFile(path)) {
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
            } catch (TikaException e) {
                LOGGER.error("Erreur lors du traitement du fichier", e);
            } catch (IOException e) {
                LOGGER.error("Erreur lors du traitement du fichier", e);
            }
        }
    }
}
