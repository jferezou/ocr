package com.perso.service;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.perso.utils.ResultatPdf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * https://developers.itextpdf.com/examples/content-extraction-and-redaction/clone-parsing-pdfs
 */
public class TraitementT2ServiceImpl implements TraitementT2Service {


    private static final Logger LOGGER = LoggerFactory.getLogger(TraitementT2ServiceImpl.class);
    @Override
    public ResultatPdf extraire(Path path) {
        LOGGER.debug("Début traitement fichier : {}", path);

        PdfDocument pdfDoc = null;
        try {
            pdfDoc = new PdfDocument(new PdfReader(path.toString()));
            Rectangle rect = new Rectangle(36, 750, 523, 56);

            FontFilter fontFilter = new FontFilter(rect);
            FilteredEventListener listener = new FilteredEventListener();
            LocationTextExtractionStrategy extractionStrategy = listener.attachEventListener(new LocationTextExtractionStrategy(), fontFilter);
            new PdfCanvasProcessor(listener).processPageContent(pdfDoc.getFirstPage());

            String actualText = extractionStrategy.getResultantText();
            LOGGER.debug("Valeur : {}",actualText);
        }
        catch (IOException e) {
            LOGGER.error("Erreur", e);
        } finally {
            if(pdfDoc != null) {
                pdfDoc.close();
            }
        }

        LOGGER.debug("Fin traitement fichier : {}", path);
        return null;
    }



    class FontFilter extends TextRegionEventFilter {
        public FontFilter(Rectangle filterRect) {
            super(filterRect);
        }

        @Override
        public boolean accept(IEventData data, EventType type) {
            if (type.equals(EventType.RENDER_TEXT)) {
                TextRenderInfo renderInfo = (TextRenderInfo) data;

                PdfFont font = renderInfo.getFont();
                if (null != font) {
                    String fontName = font.getFontProgram().getFontNames().getFontName();
                    return fontName.endsWith("Bold") || fontName.endsWith("Oblique");
                }
            }
            return false;
        }
    }
}
