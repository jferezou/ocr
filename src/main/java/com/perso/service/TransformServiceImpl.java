package com.perso.service;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.perso.config.ListeFleursConfig;
import com.perso.utils.CompositionObj;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.perso.utils.Point;
import com.perso.utils.ResultatPdf;
import com.perso.utils.Zone;

@Service("transformService")
public class TransformServiceImpl implements TransformService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransformServiceImpl.class);

    @Value("${dossier.tesseract}")
    private String tesseractDir;

	@Autowired
	private ListeFleursConfig listeFleurs;

	@Override
	/**
	 * Permet de reconnaitre le texte
	 */
	public ResultatPdf extract(final File pngFile) {
		LOGGER.info("Début du traitement du fichier {}", pngFile.getPath());

		Zone zoneEchantillon = new Zone(new Point(1646, 330), new Point(2448, 416));
		LOGGER.info("zoneEchantillon : {}", zoneEchantillon.toString());

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

		String zoneEchantillonValue = this.zoneReading(pngFile, zoneEchantillon);
		String zone1Value = this.zoneReading(pngFile, zone1);
		String zoneInterpretationValue = this.zoneReading(pngFile, zoneInterpretation);

		ResultatPdf result = new ResultatPdf();

        String baseName = FilenameUtils.getBaseName(pngFile.getName())+".pdf";
        result.setPdfFileName(baseName);
        result.setPdfFilePath(pngFile.getParentFile()+"\\"+baseName);
        String zoneEchantillonValueTempName = this.fillEchantillon(zoneEchantillonValue);
        result.setEchantillon(zoneEchantillonValueTempName);

        List<CompositionObj> compositionList = this.fillComposition(zone1Value);
        this.fillInterpretation(zoneInterpretationValue, compositionList);

        result.setCompositions(compositionList);
		LOGGER.info("Fin du traitement du fichier {}", pngFile.getName());
		return result;
	}

    private void fillInterpretation(String zoneInterpretationValue, List<CompositionObj> compositionList) {
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
                    int lastSpaceIndex = this.getfirstdigitIndex(currentValue);
                    if(lastSpaceIndex > -1) {
                        String name = currentValue.substring(0, lastSpaceIndex);
                        name = name.replace(".", "");
                        name = StringUtils.trim(name);
                        if(name.length() > 2) {
                            for(String fleur : this.listeFleurs.getFleurs()) {
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
                        for (CompositionObj comp : compositionList) {
                            if (StringUtils.stripAccents(comp.getValue().toLowerCase()).equals(tempName)) {
                                comp.setPercentage(percent);
                            }
                        }
                    }
                }

            }
        }

        for (CompositionObj comp : compositionList) {
            comp.calculateType();
        }
    }

    private int getfirstdigitIndex(final String stringValue) {
        Pattern pattern = Pattern.compile("^\\D*(\\d)");
        Matcher matcher = pattern.matcher(stringValue);
        matcher.find();
        int value = -1;
        try {
            value = matcher.start(1);
        } catch (IllegalStateException | IndexOutOfBoundsException e) {
            LOGGER.error("Erreur lors de la récupération du 1er digit pour : "+ stringValue, e);
        }
        LOGGER.debug("Index du 1er digit de la chaîne {} : {}", stringValue, value);
        return value;
    }

    private List<CompositionObj> fillComposition(String zone1Value) {
        String[] tempZone1 = zone1Value.split("\n");

        List<CompositionObj> compositionList = new ArrayList<>();
        for (int j = 0 ; j < tempZone1.length ;j++) {
            if(!StringUtils.stripStart(tempZone1[j]," ").isEmpty()) {
                CompositionObj zoneObj = new CompositionObj();
                boolean valid = false;
                for(String valeurPossible : this.listeFleurs.getFleurs()) {
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

    private String fillEchantillon(String zoneEchantillonValue) {
        String zoneEchantillonValueTempName = StringUtils.stripStart(zoneEchantillonValue, " ");
        zoneEchantillonValueTempName = StringUtils.stripEnd(zoneEchantillonValueTempName, " ");
        zoneEchantillonValueTempName = zoneEchantillonValueTempName.replace("\n","");
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
//            instance.setTessVariable("tessedit_char_blacklist", "|’œ'");
//            instance.setTessVariable("load_system_dawg", "false");
//            instance.setTessVariable("load_freq_dawg", "false");
//            instance.setTessVariable("user_words_suffix", "user-words");
//            instance.setTessVariable("user_patterns_suffix", "user-patterns");
//            instance.setTessVariable("", "bazaar");
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
