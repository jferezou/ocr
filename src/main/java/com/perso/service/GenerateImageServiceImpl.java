package com.perso.service;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.mime.MediaType;
import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.RendererException;
import org.ghost4j.renderer.SimpleRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.print.attribute.standard.Media;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class GenerateImageServiceImpl implements GenerateImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateImageServiceImpl.class);
    private static final String EXTENSION="png";
    private static final int DPI=300;
    private final MediaType imagePng=MediaType.parse("image/png");

    @Autowired
    private FileServiceImpl fileServiceImpl;
    @Override
    public File generatePng(Path path) {
        File targetFile = null;
        try {
            File file = path.toFile();
            String baseName = FilenameUtils.getBaseName(file.getName());
            targetFile = new File(file.getParent() + "/" + baseName + "." + EXTENSION);
            PDFDocument document = new PDFDocument();
            document.load(file);

            // create renderer
            SimpleRenderer renderer = new SimpleRenderer();

            // set resolution (in DPI)
            renderer.setResolution(DPI);
            // render the images
            List<Image> images = renderer.render(document);
            // write the images to file
            for (int iPage = 0; iPage < images.size(); iPage++) {
                ImageIO.write((RenderedImage) images.get(iPage), EXTENSION, targetFile );
            }
        } catch (IOException | DocumentException | RendererException e) {
            LOGGER.error("Erreur lors de la generation du png", e);
        }
        return targetFile;
    }


    @Override
    public boolean checkIfPng(final File file) {
        boolean isPng = false;
        try {
            isPng = this.fileServiceImpl.checkFileType(file, this.imagePng);
        } catch (TikaException | IOException e) {
           LOGGER.error("Erreur",e);
        }
        return isPng;
    }
}
