package com.perso.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.perso.service.PdfService;
import com.perso.utils.AggregatePdf;
import com.perso.utils.EstimateTime;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.mime.MediaType;
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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfServiceImpl implements PdfService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfServiceImpl.class);
    private MediaType applicationPdf = MediaType.parse("application/pdf");
    @Value("${dossier.temporaire}")
    private String tempDir;
    @Resource
    private FileServiceImpl fileServiceImpl;


     @Override
    public boolean checkIfPdf(final File file) {
         boolean isPdf = false;
         try {
             isPdf = this.fileServiceImpl.checkFileType(file, this.applicationPdf);
         } catch (TikaException | IOException e) {
             LOGGER.error("Erreur",e);
         }
         return isPdf;
    }

    @Override
    public void splitPdf(final Path path) {
        if (Files.isRegularFile(path)) {
            PdfDocument pdfDoc = null;
            try {
                File inFile = path.toFile();
                boolean isPdf = this.checkIfPdf(inFile);
                if (isPdf && inFile.isFile()) {
                    pdfDoc = new PdfDocument(new PdfReader(inFile.getPath()));
                    int nbPages = pdfDoc.getNumberOfPages();
                    String fileName = inFile.getName().split(".pdf")[0];

                    for (int page = 1; page <= nbPages; page++) {
                        String name = this.tempDir + "//" + fileName + "_" + page + ".pdf";
                        PdfDocument newPdfDoc = null;
                        try {
                            newPdfDoc = new PdfDocument(new PdfWriter(name));
                            pdfDoc.copyPagesTo(page, page, newPdfDoc);
                            newPdfDoc.close();
                        }
                        finally {
                            if(newPdfDoc != null) {
                                newPdfDoc.close();
                            }
                        }

                    }
                    pdfDoc.close();
                }
            } catch (IOException e) {
                LOGGER.error("Erreur lors du traitement du fichier", e);
            }
            finally {
                if(pdfDoc != null) {
                    pdfDoc.close();
                }
            }
        }
    }


    @Override
    public void deletePages(final Path path) {
         LOGGER.debug("Suppression pages document {}", path.getFileName());
        int pageToremove = 5;
        PdfDocument pdfDoc = null;
        PdfDocument newPdfDoc = null;
        if (Files.isRegularFile(path)) {
            try {
                File inFile = path.toFile();
                boolean isPdf = this.checkIfPdf(inFile);
                if (isPdf && inFile.isFile()) {
                    pdfDoc = new PdfDocument(new PdfReader(inFile.getPath()));
                    int nbPages = pdfDoc.getNumberOfPages();

                    String name = this.tempDir + "//" + inFile.getName();
                    newPdfDoc = new PdfDocument(new PdfWriter(name));
                    for (int page = 1; page <= nbPages - pageToremove; page++) {
                        pdfDoc.copyPagesTo(page, page, newPdfDoc);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Erreur lors du traitement du fichier", e);
            }
            finally {
                if(pdfDoc != null) {
                    pdfDoc.close();
                }
                if(newPdfDoc != null) {
                    newPdfDoc.close();
                }
            }
        }
    }

    @Override
    public File createPdf(final List<AggregatePdf> aggregatePdfs, String ruche) {
        String name = this.tempDir + "//pdfResultat_"+ruche+".pdf";
        PdfDocument newPdfDoc = null;
        try {
            newPdfDoc = new PdfDocument(new PdfWriter(name));
            for(AggregatePdf aggregatePdf : aggregatePdfs) {
                Path path = aggregatePdf.getPath();
                PdfDocument pdfDoc = null;
                if (Files.isRegularFile(path)) {
                    try {
                        File inFile = path.toFile();
                        boolean isPdf = this.checkIfPdf(inFile);
                        if (isPdf && inFile.isFile()) {
                            pdfDoc = new PdfDocument(new PdfReader(inFile.getPath()));
                            int nbPages = pdfDoc.getNumberOfPages();

                            for (int page = 1; page <= nbPages; page++) {
                                pdfDoc.copyPagesTo(page, page, newPdfDoc);
                            }
                        }
                    } catch (IOException e) {
                        LOGGER.error("Erreur lors du traitement du fichier", e);
                    } finally {
                        if (pdfDoc != null) {
                            pdfDoc.close();
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Erreur lors du traitement du fichier", e);
        }
        finally {
            if (newPdfDoc != null) {
                newPdfDoc.close();
            }
        }

        return new File(name);
    }


}
