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
import com.perso.bdd.dao.ParamMoleculesGmsDao;
import com.perso.bdd.dao.ParamMoleculesLmsDao;
import com.perso.bdd.dao.ResidusDocumentDao;
import com.perso.bdd.entity.parametrage.MoleculeEntity;
import com.perso.exception.BddException;
import com.perso.pojo.ocr.Point;
import com.perso.pojo.ocr.Zone;
import com.perso.pojo.residus.*;
import com.perso.service.ResidusExtractorService;
import com.perso.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.bcel.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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


    @Resource
    private ParamMoleculesGmsDao paramMoleculesGmsDao;

    @Resource
    private ParamMoleculesLmsDao paramMoleculesLmsDao;

    @Resource
    private ResidusDocumentDao residusDocumentDao;

    @Override
    public ResidusDocument extraire(final Path path) {
        LOGGER.debug("Début traitement fichier : {}", path);
        ResidusDocument residusDocument = new ResidusDocument();
        residusDocument.setGmsDataList(this.paramMoleculesGmsDao.getAllMoleculesGms());
        residusDocument.setLmsDataList(this.paramMoleculesLmsDao.getAllMoleculesLms());
        residusDocument.setPdfFilePath(path.toString());
        residusDocument.setPdfName(path.getFileName().toString());

        try (PdfReader reader = new PdfReader(path.toString()) ; PdfDocument pdfDoc =  new PdfDocument(reader)) {
            this.extraireInformationsGeneralesResidu(residusDocument, pdfDoc);

            int totalPage = pdfDoc.getNumberOfPages();
            LOGGER.debug("totalPage : {}", totalPage);

            Zone zoneResultatP1 = new Zone(new Point(0, this.taillePage - 567), new Point(345, this.taillePage - 454));
            LOGGER.info("zoneResultat : {}", zoneResultatP1.toString());

            Zone zoneResultat = new Zone(new Point(0, this.taillePage - 742), new Point(345, this.taillePage - 103));
            LOGGER.info("zoneResultat : {}", zoneResultat.toString());
            // TODO mieux gerer ça !
            String valeurPrecedente = Constantes.EMPTYSTRING;
            // on parcours de la page 1 à la page total - 5
            for(int page = 1; page <= totalPage - 5 ; page ++) {
                String resultat;
                // cas particulier de la page 1 :
                if(page == 1) {
                    resultat = this.extractValue(pdfDoc.getPage(page), zoneResultatP1);
                }
                else {
                    resultat = this.extractValue(pdfDoc.getPage(page), zoneResultat);
                }
                LOGGER.debug("Resultat : {}", resultat);
                resultat = this.nettoyerResultat(resultat);
                String[] resultatSplit = resultat.split(Constantes.NEWLINE);
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
                        Molecule molecule = this.traitementLigne(line, isGms, valeurPrecedente);
                        valeurPrecedente = StringUtilsOcr.trimEtSupprimeTiretalaFin(line);
                        if(molecule != null) {
                            // si la liste continet deja cette molécule, on lui assigne le pourcentage max
                            if(currentList.contains(molecule)) {
                              Molecule currentMolecule = currentList.get(currentList.indexOf(molecule));
                              currentMolecule.setPourcentage(Math.max(molecule.getPourcentage(), currentMolecule.getPourcentage()));
                              this.setLimite(molecule, currentMolecule);

                              // on passe en erreur car il y a eut de la bidouille (exemple : molécule sur plusieurs lignes)
                              currentMolecule.setErreur(true);
                            }
                            else if(molecule.getValue() != null && !Constantes.EMPTYSTRING.equals(molecule.getValue())) {
                                currentList.add(molecule);
                            }
                        }
                    }
                }
                LOGGER.debug("Resultat nettoyé : {}", resultat);
            }

        }
        catch (IOException e) {
            LOGGER.error("Erreur", e);
        }

        if(residusDocument.getMoleculesGms().isEmpty()) {
            residusDocument.getMoleculesGms().add(this.createRASMolecule());
        }
        if(residusDocument.getMoleculesLms().isEmpty()) {
            residusDocument.getMoleculesLms().add(this.createRASMolecule());
        }

        // on verifie si le doc n'est pas deja en base
        boolean dejaPresentEnBDD = (this.residusDocumentDao.findByIdentifiant(residusDocument.getReference()) != null);
        residusDocument.setDejaPresentEnBDD(dejaPresentEnBDD);

        LOGGER.debug("Fin traitement fichier : {}", path);
        return residusDocument;
    }

    private Molecule createRASMolecule() {
        Molecule molecule = new Molecule();
        molecule.setValue("RAS");
        molecule.setPourcentage(0.0);
        molecule.setLimite("");
        molecule.setTrace(true);
        return molecule;
    }

    /**
     * permet de choisir la limite si la môlécule est deja présente dans la liste :
     * si * alors on prend ça
     * sinon si la limite est un entier en ] 0 ; 1 [ on prend cet entier
     * @param molecule
     * @param currentMolecule
     */
    private void setLimite(final Molecule molecule, final Molecule currentMolecule) {
        String limite = molecule.getLimite();
        if ("*".equals(limite)){
            currentMolecule.setLimite(limite);
            currentMolecule.setTrace(molecule.isTrace());
        }
        else {
            try {
              double limiteD = Double.parseDouble(limite.replace(Constantes.VIRGULE, Constantes.POINT));
              if(limiteD < 1 && limiteD > 0) {
                  currentMolecule.setLimite(limite);
                  currentMolecule.setTrace(molecule.isTrace());
              }
            } catch(NumberFormatException e) {
                LOGGER.warn("la limite n'est pas un entier");
            }
        }
    }

    /**
     * Méthode permettant d'extraire les infos générales pour un doc pdf résidus : poids, référence et certificat analyse
     * @param residusDocument
     * @param pdfDoc
     */
    private void extraireInformationsGeneralesResidu(final ResidusDocument residusDocument, final PdfDocument pdfDoc) {
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
    }

    /**
     * Permet de nettoyer le resultat d'une page avec les éléments non utiles
     * @param resultat
     * @return
     */
    public String nettoyerResultat(String resultat) {
        resultat = resultat.replace("trace found", Constantes.EMPTYSTRING);
        resultat = resultat.replace("Autres non détectables (<LC)", Constantes.EMPTYSTRING);
        resultat = resultat.replace("Autres non détectables >= LC", Constantes.EMPTYSTRING);
        resultat = resultat.replace("Substance Accr. Résultat Limites", Constantes.EMPTYSTRING);
        resultat = resultat.replace("Aucun produit >= LC", Constantes.EMPTYSTRING);
        resultat = resultat.replace(Constantes.NEWLINE + Constantes.A,Constantes.AENTREPARENTHESE + Constantes.ESPACE);
        resultat = resultat.replace(Constantes.A + Constantes.ESPACE,Constantes.AENTREPARENTHESE + Constantes.ESPACE);
        resultat = resultat.replaceAll("(?m)^\\s", Constantes.EMPTYSTRING);
        return resultat;
    }

    /**
     * Recupèrel av lkauer du poids dans le document
     * @param pdfDoc
     * @param zonePoids
     * @return
     */
    private double getPoids(final PdfDocument pdfDoc, final Zone zonePoids) {
        String poidsString = this.extractValue(pdfDoc.getFirstPage(), zonePoids);
        poidsString = poidsString.replace(Constantes.VIRGULE,Constantes.POINT);
        double poids = 0.0;
        try {
            poids = Double.parseDouble(poidsString);
        }
        catch (NumberFormatException e) {
            LOGGER.error("Erreur de formatage du poids",e);
        }
        return poids;
    }


    /**
     * Permet d'aggreger des pdf
     * @param path
     * @return
     */
    @Override
    public AggregatePdf extraireDate(final Path path) {
    LOGGER.debug("Traitement fichier : {}", path.getFileName());
        // donc (x1,y1) et (x2,y2) => (x1, taillePage - y2) et (x2, taillePage - y1)
        Zone zoneDate = new Zone(new Point(118, this.taillePage - 224), new Point(366, this.taillePage - 210));
        LOGGER.info("ZoneInterpretation : {}", zoneDate.toString());
        Date debut = null;
        PdfDocument pdfDoc = null;
        PdfReader reader = null;
        String ruche = null;
        Integer rucher = null;
        String matrice = null;
        try {
            reader = new PdfReader(path.toString());
            pdfDoc = new PdfDocument(reader);
            String dates = this.extractValue(pdfDoc.getFirstPage(), zoneDate);
            LOGGER.debug("Dates = {}", dates);
            String[] dateList = dates.split(Constantes.TIRET);
            String periodeDebut = dateList[3];
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            debut = sdf.parse(periodeDebut);

            ruche = dateList[2];
            rucher = Integer.parseInt(dateList[1]);
            matrice = dateList[0];
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
        aggregatePdf.setRuche(ruche);
        aggregatePdf.setRucher(rucher);
        aggregatePdf.setMatrice(matrice);
        return aggregatePdf;
    }


    /**
     * Méthode principale qui permet d'extraire les données pour une molécule en fonction de la ligne passée en paramètre
     * @param line
     * @param isGms
     * @return
     */
    protected Molecule traitementLigne(final String line, final boolean isGms, final String valeurPrecedente) {
        LOGGER.debug("traitement ligne nettoyée : {}", line);
        String tempLine = line;
        // on essaye d'extraire la limite qui est normalement la dernière valeur, donc après le dernier espace
        int lastSpaceindex = StringUtilsOcr.getLastSpace(tempLine);
        String limite = Constantes.EMPTYSTRING;
        if(lastSpaceindex > 1) {
            limite = line.substring(lastSpaceindex + 1, line.length());
            // on créé une ligne 'temporaire', sans la valeur limite théorique
            tempLine = line.substring(0, lastSpaceindex);
        }
        Molecule traitementObj;
        int lastSpace = StringUtilsOcr.getLastSpace(tempLine);

        boolean isDouble = false;
        try {
            if(lastSpace > 0) {
                Double.parseDouble(tempLine.substring(lastSpace, tempLine.length()).replace(Constantes.VIRGULE, Constantes.POINT));
                isDouble = true;
            }
        } catch (NumberFormatException e){
            isDouble = false;
        }

        // il y a une valeur numérique
        if(isDouble) {
            traitementObj = this.getMoleculeFirstDigitFound(tempLine, isGms, lastSpace, valeurPrecedente);
        }
        // on cherche la valeur dans nos tables
        else {
            traitementObj = this.getMoleculeNoDigit(tempLine, isGms, valeurPrecedente);
        }
        if(traitementObj != null) {
            traitementObj.setLimite(limite);
        }

        LOGGER.debug("Objet généré : {}", traitementObj);
        return traitementObj;
    }

    private Molecule getMoleculeNoDigit(final String line, final boolean isGms, final String valeurPrecedente) {
        Molecule traitementObj = new Molecule();
        String value = StringUtils.trim(line);
        MoleculeEntity moleculeEntity = null;
        try {
            if (isGms) {
                moleculeEntity = this.paramMoleculesGmsDao.findByName(value);
            } else {
                moleculeEntity = this.paramMoleculesLmsDao.findByName(value);
            }
        }
        catch(BddException e) {
            LOGGER.error("Erreur", e);
        }

        double valeurTrace = (moleculeEntity != null) ? moleculeEntity.getValeurTrace() : -1;
        if(valeurTrace != -1) {
            traitementObj.setValue(moleculeEntity.getNom());
            traitementObj.setPourcentage(valeurTrace);
            traitementObj.setTrace(true);
        }
        else {
            // on supprime le - à la fin pour éviter les soucis de retour à la ligne
            String valeurCourante = StringUtils.removeEnd(value,Constantes.TIRET);
            this.findMoleculeContainingName(isGms, traitementObj, valeurCourante, valeurPrecedente);
        }
        return traitementObj;
    }


    private Molecule getMoleculeFirstDigitFound(final String line, final boolean isGms, final int firstDigit, final String valeurPrecedente) {
        String value = line.substring(0, firstDigit);
        value = StringUtils.trim(value);
        MoleculeEntity moleculeEntity;
        Molecule traitementObj = null;
        try {
            double pourcentage = Double.parseDouble(line.substring(firstDigit, line.length()).replace(Constantes.VIRGULE, Constantes.POINT));
            traitementObj = new Molecule();
            traitementObj.setPourcentage(pourcentage);
            try {
                if (isGms) {
                    moleculeEntity = this.paramMoleculesGmsDao.findByName(value);
                } else {
                    moleculeEntity = this.paramMoleculesLmsDao.findByName(value);
                }
                traitementObj.setValue(moleculeEntity.getNom());
            }
            catch(BddException e) {
                LOGGER.error("Erreur", e);
                traitementObj.setErreur(true);
                this.findMoleculeContainingName(isGms, traitementObj, value, valeurPrecedente);
            }

        }
        catch(NumberFormatException e) {
            LOGGER.error("Erreur", e);
        }
        return traitementObj;
    }

    private void findMoleculeContainingName(final boolean isGms, Molecule traitementObj, final String valeurCourante, final String valeurPrecedente) {
        MoleculeEntity moleculeEntity = null;
        if(!Constantes.EMPTYSTRING.equals(valeurPrecedente)) {
            try {
                if (isGms) {
                    moleculeEntity = this.paramMoleculesGmsDao.findByNameContaining(valeurPrecedente);
                } else {
                    moleculeEntity = this.paramMoleculesLmsDao.findByNameContaining(valeurPrecedente);
                }
            } catch (BddException ne) {
                LOGGER.error("Erreur", ne);
            }

            try {
                if(moleculeEntity == null) {
                    String nom = (valeurPrecedente + Constantes.ESPACE + valeurCourante).replace(Constantes.AENTREPARENTHESE,Constantes.EMPTYSTRING);
                    nom = StringUtils.removeEnd(nom,Constantes.TIRET);
                    if (isGms) {
                        moleculeEntity = this.paramMoleculesGmsDao.findByNameContaining(nom);
                    } else {
                        moleculeEntity = this.paramMoleculesLmsDao.findByNameContaining(nom);
                    }
                }

                traitementObj.setValue(moleculeEntity.getNom());
                if (traitementObj.getPourcentage() == null || traitementObj.getPourcentage() < 0) {
                    traitementObj.setPourcentage(moleculeEntity.getValeurTrace());
                    traitementObj.setTrace(true);
                }
            } catch (BddException ne) {
                LOGGER.error("Erreur : {}", ne.getMessage());
                traitementObj = null;
            }
        }
    }


    private String extractValue(final PdfPage pdfPage, final Zone zoneReference) {
        Rectangle rect = new Rectangle(zoneReference.getDebut().getX(), zoneReference.getDebut().getY(), zoneReference.getWidth(), zoneReference.getHeigth());
        TextRegionEventFilter regionFilter = new TextRegionEventFilter(rect);
        ITextExtractionStrategy strategy = new FilteredTextEventListener(new LocationTextExtractionStrategy(), regionFilter);
        return PdfTextExtractor.getTextFromPage(pdfPage, strategy);
    }
}
