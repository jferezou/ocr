package com.perso.service.impl;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredTextEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.perso.pojo.ocr.Point;
import com.perso.pojo.ocr.Zone;
import com.perso.pojo.residus.*;
import com.perso.service.ResidusExtractorService;
import com.perso.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * https://developers.itextpdf.com/examples/content-extraction-and-redaction/clone-parsing-pdfs
 */
@Service
public class ResidusExtractorServiceImpl implements ResidusExtractorService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ResidusExtractorServiceImpl.class);
    private final int taillePage = 840;
    private MoleculesGmsList gmsList = new MoleculesGmsList();
    private MoleculesLmsList lmsList = new MoleculesLmsList();

    @Override
    public ResidusDocument extraire(Path path) {
        LOGGER.debug("Début traitement fichier : {}", path);
        ResidusDocument residusDocument = new ResidusDocument();
        residusDocument.setGmsDataList(new MoleculesGmsList().toList());
        residusDocument.setLmsDataList(new MoleculesLmsList().toList());
        residusDocument.setPdfFilePath(path.toString());

        residusDocument.setPdfName(path.getFileName().toString());
        PdfDocument pdfDoc = null;
        PdfReader reader = null;
        try {
            reader = new PdfReader(path.toString());
            pdfDoc = new PdfDocument(reader);

            // en point depuis le coin en bas à gauche
            // les valeurs recuperee de gimp sont en point du coin en haut à gauche
            // donc (x1,y1) et (x2,y2) => (x1, taillePage - y2) et (x2, taillePage - y1)
            Zone zoneReference = new Zone(new Point(118, this.taillePage - 224), new Point(366, this.taillePage - 210));
            LOGGER.info("ZoneInterpretation : {}", zoneReference.toString());
            String reference = this.extractValue(pdfDoc.getFirstPage(), zoneReference);
            LOGGER.debug("Reference : {}", reference);
            residusDocument.setReference(reference);

            Zone zoneCertificatAnalyse = new Zone(new Point(0, this.taillePage - 148), new Point(157, this.taillePage - 127));
            LOGGER.info("ZoneInterpretation : {}", zoneCertificatAnalyse.toString());
            String certificatAnalyse = this.extractValue(pdfDoc.getFirstPage(), zoneCertificatAnalyse);
            LOGGER.debug("certificatAnalyse : {}", certificatAnalyse);
            residusDocument.setCertificatAnalyses(certificatAnalyse);

            Zone zonePoids = new Zone(new Point(408, this.taillePage - 312), new Point(493, this.taillePage - 301));
            LOGGER.info("ZoneInterpretation : {}", zoneCertificatAnalyse.toString());
            double poids = getPoids(pdfDoc, zonePoids);
            LOGGER.debug("poids : {}", poids);
            residusDocument.setPoids(poids);

            int totalPage = pdfDoc.getNumberOfPages();
            LOGGER.debug("totalPage : {}", totalPage);

            Zone zoneResultat = new Zone(new Point(0, this.taillePage - 742), new Point(301, this.taillePage - 103));
            LOGGER.info("zoneResultat : {}", zoneResultat.toString());
            // on parcours de la page 2 à la page total - 5
            for(int page = 2; page <= totalPage - 5 ; page ++) {
                String resultat = this.extractValue(pdfDoc.getPage(page), zoneResultat);
                LOGGER.debug("Resultat : {}", resultat);
                resultat = resultat.replace("trace found", "");
                resultat = resultat.replace("Autres non détectables (<LC)", "");
                resultat = resultat.replace("Autres non détectables >= LC", "");
                resultat = resultat.replace("Substance Accr. Résultat", "");
                resultat = resultat.replace("Aucun produit >= LC", "");
                resultat = resultat.replace("A","(A)");
                resultat = resultat.replaceAll("(?m)^\\s", "");
                String[] resultatSplit = resultat.split("\n");
                List<Molecule> currentList = residusDocument.getMoleculesLms();
                boolean isGms = false;
                for(int lineIndex = 0; lineIndex < resultatSplit.length; lineIndex ++) {
                    String line = resultatSplit[lineIndex];
                    if(line.contains("LMS - LC-MSMS - Primoris accredited")) {
                        currentList = residusDocument.getMoleculesLms();
                        isGms = false;
                        LOGGER.debug("Passage liste LMS");
                    }
                    else if(line.contains("GMS - GC-MSMS - Primoris accredited")) {
                        currentList = residusDocument.getMoleculesGms();
                        isGms = true;
                        LOGGER.debug("Passage liste GMS");
                    }
                    else {
                        currentList.add(this.traitementLigne(line, isGms));
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
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("erreur",e);
                }
            }
        }
        LOGGER.debug("Fin traitement fichier : {}", path);
        return residusDocument;
    }

    private double getPoids(PdfDocument pdfDoc, Zone zonePoids) {
        String poidsString = this.extractValue(pdfDoc.getFirstPage(), zonePoids);
        poidsString = poidsString.replace(",",".");
        double poids = 0.0;
        try {
            poids = Double.parseDouble(poidsString);
        }
        catch (NumberFormatException e) {
            LOGGER.error("Erreur de formatage du poids",e);
        }
        return poids;
    }


    @Override
    public AggregatePdf extraireDate(Path path) {
    LOGGER.debug("Traitement fichier : {}", path.getFileName());
        // donc (x1,y1) et (x2,y2) => (x1, taillePage - y2) et (x2, taillePage - y1)
//        Zone zoneDate = new Zone(new Point(120, this.taillePage - 402), new Point(278, this.taillePage - 390));
        Zone zoneDate = new Zone(new Point(118, this.taillePage - 224), new Point(366, this.taillePage - 210));
        LOGGER.info("ZoneInterpretation : {}", zoneDate.toString());
        Date debut = null;
        PdfDocument pdfDoc = null;
        PdfReader reader = null;
        try {
            reader = new PdfReader(path.toString());
            pdfDoc = new PdfDocument(reader);
            String dates = this.extractValue(pdfDoc.getFirstPage(), zoneDate);
            LOGGER.debug("Dates = {}", dates);
            String[] dateList = dates.split("-");
            String periodeDebut = dateList[3];
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            debut = sdf.parse(periodeDebut);
            LOGGER.debug("debut : {}", debut);
        }
        catch (IOException | ParseException e) {
                LOGGER.error("Erreur", e);
            }
        finally {
                if(pdfDoc != null) {
                    pdfDoc.close();
                }
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        LOGGER.error("erreur",e);
                    }
                }
            }
        AggregatePdf aggregatePdf = new AggregatePdf();
        aggregatePdf.setDate(debut);
        aggregatePdf.setPath(path);
        return aggregatePdf;
    }


    private Molecule traitementLigne(final String line, boolean isGmt) {
        LOGGER.debug("traitement ligne nettoyé : {}", line);
        Molecule traitementObj = new Molecule();
        int firstDigit = StringUtilsOcr.getfirstdigitIndex(line);
        // il y a une valeur
        if(firstDigit > 0) {
            String value = line.substring(0, firstDigit);
            value = StringUtils.trim(value);

            try {
                double pourcentage = Double.parseDouble(line.substring(firstDigit, line.length()).replace(",", "."));
                traitementObj.setValue(value);
                traitementObj.setPourcentage(pourcentage);
            }
            catch(NumberFormatException e) {
                LOGGER.error("Erreur", e);
                traitementObj.setErreur(true);
            }

        }
        // on cherche la valeur dans nos tables
        else {
            String value = StringUtils.trim(line);
            double pourcentage = -1;
            if(isGmt) {
                pourcentage = getPourcentageFromElement(value, gmsList);
            }
            else {
                pourcentage = getPourcentageFromElement(value, lmsList);
            }
            traitementObj.setValue(value);
            traitementObj.setPourcentage(pourcentage);
            traitementObj.setTrace(true);
        }

        // enfin on verifie que l'élément existe bien dans nos listes, sinon on le mets enb erreur :
        if (!gmsList.getValues().containsKey(traitementObj.getValue()) && !lmsList.getValues().containsKey(traitementObj.getValue())) {
            traitementObj.setErreur(true);
        }

        LOGGER.debug("Objet généré : {}", traitementObj);
        return traitementObj;
    }

    private double getPourcentageFromElement(String value, MoleculesList moleculesList) {
        double pourcentage = -1;
        if (moleculesList.getValues().containsKey(value)) {
            pourcentage = moleculesList.getValues().get(value);
        }
        return pourcentage;
    }


    private String extractValue(final PdfPage pdfPage, final Zone zoneReference) {
        Rectangle rect = new Rectangle(zoneReference.getDebut().getX(), zoneReference.getDebut().getY(), zoneReference.getWidth(), zoneReference.getHeigth());
        TextRegionEventFilter regionFilter = new TextRegionEventFilter(rect);
        ITextExtractionStrategy strategy = new FilteredTextEventListener(new LocationTextExtractionStrategy(), regionFilter);
        return PdfTextExtractor.getTextFromPage(pdfPage, strategy);
    }
}
