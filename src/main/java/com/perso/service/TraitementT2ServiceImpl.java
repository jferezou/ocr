package com.perso.service;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredTextEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.perso.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * https://developers.itextpdf.com/examples/content-extraction-and-redaction/clone-parsing-pdfs
 */
@Service
public class TraitementT2ServiceImpl implements TraitementT2Service {


    private static final Logger LOGGER = LoggerFactory.getLogger(TraitementT2ServiceImpl.class);
    private final int taillePage = 840;
    private List<Traitement2Obj> gmsList;
    private List<Traitement2Obj> lmsList;

    @Override
    public ResultatPdf extraire(Path path) {
        LOGGER.debug("Début traitement fichier : {}", path);

        this.gmsList = new ArrayList<>();
        this.lmsList = new ArrayList<>();
        PdfDocument pdfDoc = null;
        try {
            pdfDoc = new PdfDocument(new PdfReader(path.toString()));

            // en point depuis le coin en bas à gauche
            // les valeurs recuperee de gimp sont en point du coin en haut à gauche
            // donc (x1,y1) et (x2,y2) => (x1, taillePage - y2) et (x2, taillePage - y1)
            Zone zoneReference = new Zone(new Point(118, this.taillePage - 224), new Point(366, this.taillePage - 210));
            LOGGER.info("ZoneInterpretation : {}", zoneReference.toString());
            String reference = this.getReference(pdfDoc.getFirstPage(), zoneReference);
            LOGGER.debug("Reference : {}", reference);

            int totalPage = pdfDoc.getNumberOfPages();
            LOGGER.debug("totalPage : {}", totalPage);

            Zone zoneResultat = new Zone(new Point(0, this.taillePage - 742), new Point(301, this.taillePage - 103));
            LOGGER.info("zoneResultat : {}", zoneResultat.toString());
            // on parcours de la page 2 à la page total - 5
            for(int page = 2; page <= totalPage - 5 ; page ++) {
                String resultat = this.getReference(pdfDoc.getPage(page), zoneResultat);
                LOGGER.debug("Resultat : {}", resultat);
                resultat = resultat.replace("trace found", "");
                resultat = resultat.replace("Autres non détectables (<LC)", "");
                resultat = resultat.replace("Autres non détectables >= LC", "");
                resultat = resultat.replace("Substance Accr. Résultat", "");
                resultat = resultat.replace("Aucun produit >= LC", "");
                resultat = resultat.replace("A","(A)");
                resultat = resultat.replaceAll("(?m)^\\s", "");
                String[] resultatSplit = resultat.split("\n");
                List<Traitement2Obj> currentList = this.lmsList;
                boolean isGmt = false;
                for(int lineIndex = 0; lineIndex < resultat.length(); lineIndex ++) {
                    String line = resultatSplit[lineIndex];
                    if(line.contains("LMS - LC-MSMS - Primoris accredited")) {
                        currentList = this.lmsList;
                        isGmt = false;
                        LOGGER.debug("Passage liste LMS");
                    }
                    else if(line.contains("GMS - GC-MSMS - Primoris accredited")) {
                        currentList = this.gmsList;
                        isGmt = true;
                        LOGGER.debug("Passage liste GMS");
                    }
                    else {
                        currentList.add(this.traitementLigne(line, isGmt));
                    }
                }

                LOGGER.debug("Resultat nettoyé : {}", resultat);
            }

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


    private Traitement2Obj traitementLigne(final String line, boolean isGmt) {
        LOGGER.debug("traitement ligne nettoyé : {}", line);
        Traitement2Obj traitementObj = new Traitement2Obj();
        int firstDigit = StringUtilsOcr.getfirstdigitIndex(line);
        // il y a une valeur
        if(firstDigit > 0) {
            String value = line.substring(0, firstDigit);
            value = StringUtils.trim(value);

            double pourcentage = Double.parseDouble(line.substring(firstDigit,line.length()).replace(",","."));
            traitementObj.setValue(value);
            traitementObj.setPourcentage(pourcentage);
        }
        // on cherche la valeur dans nos tables
        else {

        }

        LOGGER.debug("Objet généré : {}", traitementObj);
        return traitementObj;
    }



    private String getReference(final PdfPage pdfPage, final Zone zoneReference) {
        Rectangle rect = new Rectangle(zoneReference.getDebut().getX(), zoneReference.getDebut().getY(), zoneReference.getWidth(), zoneReference.getHeigth());
        TextRegionEventFilter regionFilter = new TextRegionEventFilter(rect);
        ITextExtractionStrategy strategy = new FilteredTextEventListener(new LocationTextExtractionStrategy(), regionFilter);
        return PdfTextExtractor.getTextFromPage(pdfPage, strategy);
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
                    return true;//fontName.endsWith("Bold") || fontName.endsWith("Oblique");
                }
            }
            return false;
        }
    }
}
