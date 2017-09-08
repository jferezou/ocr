package com.perso.service;

import org.apache.commons.io.FilenameUtils;
import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.RendererException;
import org.ghost4j.renderer.SimpleRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
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
    @Override
    public File generatePng(Path path) {
        File targetFile = null;
        if(Files.isRegularFile(path)) {
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

//            BufferedImage bufferedImage = new BufferedImage(200, 200,
//                    BufferedImage.TYPE_BYTE_INDEXED);
//            bufferedImage = ImageIO.read(new File("D:\\dev\\ocr\\0.png"));
//
//            float scaleFactor = 1.3f;
//            RescaleOp op = new RescaleOp(scaleFactor, 20.0f, null);
//            op.filter(bufferedImage, bufferedImage);
            } catch (IOException | DocumentException | RendererException e) {
                LOGGER.error("Erreur lors de la generation du png", e);
            }

        }
        return targetFile;
    }
}
