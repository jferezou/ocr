package com.perso.service.impl;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.perso.bdd.dao.ParamEspeceDao;
import com.perso.bdd.entity.parametrage.EspeceEntity;
import com.perso.pojo.ocr.Zone;
import com.perso.service.PalynologieExtractorService;
import com.perso.utils.*;
import com.perso.pojo.ocr.Point;
import com.perso.pojo.palynologie.Palynologie;
import com.perso.pojo.palynologie.PalynologieDocument;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PalynologieExtractorServiceImpl implements PalynologieExtractorService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PalynologieExtractorServiceImpl.class);

    @Value("${dossier.tesseract}")
    private String tesseractDir;

	@Resource
	private ParamEspeceDao paramEspeceDao;

	private List<String> listeEspece;

	@Override
	/**
	 * Permet de reconnaitre le texte
	 */
	public PalynologieDocument extract(final File pngFile) {
        this.generateListeFleurs();
		LOGGER.info("Début du traitement du fichier {}", pngFile.getPath());

		Zone zoneAppelationDemandeur = new Zone(new Point(1646, 330), new Point(2448, 416));
		LOGGER.info("zoneEchantillon : {}", zoneAppelationDemandeur.toString());

		Zone zone1 = new Zone(new Point(1562, 1110), new Point(2402, 2568));
		LOGGER.info("Zone1 : {}", zone1.toString());

		Zone zoneInterpretation = new Zone(new Point(0, 2562), new Point(2414, 3000));
		LOGGER.info("ZoneInterpretation : {}", zoneInterpretation.toString());
//        Zone zoneEchantillon = new Zone(new Point(2744, 576), new Point(4060, 688));
//        LOGGER.info("zoneEchantillon : {}", zoneEchantillon.toString());
//
//        Zone zone1 = new Zone(new Point(2600, 1860), new Point(4044, 4204));
//        LOGGER.info("Zone1 : {}", zone1.toString());
//
//        Zone zoneInterpretation = new Zone(new Point(0, 4268), new Point(3872, 5000));
//		LOGGER.info("ZoneInterpretation : {}", zoneInterpretation.toString());

        Zone zoneEchantillon = new Zone(new Point(324, 400), new Point(648, 465));
        LOGGER.info("zoneEchantillon : {}", zoneEchantillon.toString());

		String zoneAppelationDemandeurValue = this.zoneReading(pngFile, zoneAppelationDemandeur);
		String zone1Value = this.zoneReading(pngFile, zone1);
		String zoneInterpretationValue = this.zoneReading(pngFile, zoneInterpretation);
		String zoneEchantillonValue = this.zoneReading(pngFile, zoneEchantillon);

		PalynologieDocument result = new PalynologieDocument();
		result.setFleurs(this.listeEspece);

        String baseName = FilenameUtils.getBaseName(pngFile.getName())+".pdf";
        result.setEchantillon(zoneEchantillonValue);
        result.setPdfFileName(baseName);
        result.setPdfFilePath(pngFile.getParentFile()+"\\"+baseName);
        String zoneEchantillonValueTempName = this.fillAppelationDemandeur(zoneAppelationDemandeurValue);


        result.setAppelationDemandeur(zoneEchantillonValueTempName);

        List<Palynologie> compositionList = this.fillComposition(zone1Value);
        this.fillInterpretation(zoneInterpretationValue, compositionList);

        result.setCompositions(compositionList);
		LOGGER.info("Fin du traitement du fichier {}", pngFile.getName());
		return result;
	}

	private void generateListeFleurs() {
	    List<EspeceEntity> listeEspeceEntity = this.paramEspeceDao.getAllEspeces();
	    this.listeEspece = new ArrayList<>();
	    for(EspeceEntity espece : listeEspeceEntity) {
	        this.listeEspece.add(espece.getNom());
        }
    }

    private void fillInterpretation(final String zoneInterpretationValue, final List<Palynologie> compositionList) {
        // on repere la ligne d'interpretation (il doit y avoir des % et des ,)
        final String[] splitedInterpretationLine =  zoneInterpretationValue.split("\n");

        for (int j = 0 ; j < splitedInterpretationLine.length ;j++) {
            String line = splitedInterpretationLine[j];
            if(line.contains("%")) {
                LOGGER.debug("line = {}", line);
                // on supprime les ,
                line = line.replace(",","");
                // on splite par rapport aux %
                String[] splitedLine = line.split("%");

                // en theorie on doit se retouver avec qq de la forme {Nom} {Valeur}
                for (int i = 0 ; i < splitedLine.length ;i++) {
                    String currentValue = splitedLine[i];
                    LOGGER.debug("currentValue = {}", currentValue);
                    // on supprime tous les espaces
                    currentValue = currentValue.replace(" ","");
                    // Dans le cas du 1er caractère, on le supprime
                    if(i ==0 && currentValue.length() > 1) {
                        currentValue = currentValue.substring(1);
                        LOGGER.debug("Debut de ligne, le 1er caractère est supprimé : {}", currentValue);
                    }
                    // on recupere l'indexe du 1er digit
                    int lastSpaceIndex = StringUtilsOcr.getfirstdigitIndex(currentValue);
                    if(lastSpaceIndex > -1) {
                        String name = currentValue.substring(0, lastSpaceIndex);
                        name = name.replace(".", "");
                        name = StringUtils.trim(name);
                        if(name.length() > 2) {
                            for(String fleur : this.listeEspece) {
                                if(fleur.toLowerCase().replace(" ","").contains(name)) {
                                    name = fleur;
                                }
                            }
                        }

                        LOGGER.debug("name = {}", name);
                        String percentValue = currentValue.substring(lastSpaceIndex, currentValue.length());

                        double percent = 0;
                        try {
                            percent = Double.parseDouble(percentValue.replace(" ",""));
                            // tant qu'on est supérieur à 100
                            while (percent > 100) {
                                percent = percent / 10;
                            }
                            LOGGER.debug("Poucentage : {}", percent);
                        }
                        catch(NumberFormatException e) {
                            LOGGER.warn("Erreur de parsing du nombre {}",percentValue);
                        }
                        String tempName = StringUtils.stripAccents(name.toLowerCase());
                        tempName = StringUtils.trim(tempName);
                        for (Palynologie comp : compositionList) {
                            if (StringUtils.stripAccents(comp.getValue().toLowerCase()).equals(tempName)) {
                                comp.setPercentage(percent);
                            }
                        }
                    }
                }

            }
        }

        for (Palynologie comp : compositionList) {
            comp.calculateType();
        }
    }


    private List<Palynologie> fillComposition(final String zone1Value) {
        String[] tempZone1 = zone1Value.split("\n");

        List<Palynologie> compositionList = new ArrayList<>();
        for (int j = 0 ; j < tempZone1.length ;j++) {
            if(!StringUtils.stripStart(tempZone1[j]," ").isEmpty()) {
                Palynologie zoneObj = new Palynologie();
                boolean valid = false;
                for(String valeurPossible : this.listeEspece) {
                    String tempP = StringUtils.stripAccents(valeurPossible.toLowerCase().replace(" ",""));
                    String currentValueTemp = StringUtils.stripAccents(tempZone1[j].replace("ﬂ","fl").replace(" ","").toLowerCase());
                    if(tempP.equals(currentValueTemp)) {
                        zoneObj.setValue(valeurPossible);
                        valid = true;
                    }
                }

                if(!valid) {
                    zoneObj.setValue(tempZone1[j]);
                }
                zoneObj.setValid(valid);
                compositionList.add(zoneObj);
            }
        }
        return compositionList;
    }

    private String fillAppelationDemandeur(final String zoneEchantillonValue) {
        String zoneEchantillonValueTempName = StringUtils.stripStart(zoneEchantillonValue, " ");
        zoneEchantillonValueTempName = StringUtils.stripEnd(zoneEchantillonValueTempName, " ");
        zoneEchantillonValueTempName = zoneEchantillonValueTempName.replace("\n","");
        int firstSpace = StringUtilsOcr.getFirstSpace(zoneEchantillonValueTempName);
        if(firstSpace > -1 ) {
            zoneEchantillonValueTempName = zoneEchantillonValueTempName.substring(firstSpace, zoneEchantillonValueTempName.length());
        }
        return zoneEchantillonValueTempName;
    }


    private String zoneReading(final File doc, final Zone zone) {
	    LOGGER.debug("Debut traitement ocr fichier {} pour la zone {}", doc.getPath(), zone);
        ITesseract instance = new Tesseract();
        String result= "";
        try {
            instance.setLanguage("fra");
            instance.setDatapath(this.tesseractDir);
            instance.setTessVariable("tessedit_char_whitelist", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZçéèêﬂ0123456789%./");
            List<String> config = new ArrayList<>();
            config.add("bazaar");
            config.add("quiet");
            instance.setConfigs(config);
            Rectangle rect = new Rectangle(zone.getDebut().getX(), zone.getDebut().getY(), zone.getWidth(), zone.getHeigth());
            result = instance.doOCR(doc, rect);
        } catch (TesseractException e) {
            LOGGER.error("erreur",e);
        }
        LOGGER.debug("Fin traitement ocr fichier {} pour la zone {}", doc.getPath(), zone);
		return result;
	}
}
