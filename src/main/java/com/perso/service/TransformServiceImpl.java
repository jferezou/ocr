package com.perso.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.perso.utils.CompositionObj;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.asprise.ocr.Ocr;
import com.perso.utils.Point;
import com.perso.utils.ResultatPdf;
import com.perso.utils.Zone;

@Service("transformService")
public class TransformServiceImpl implements TransformService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransformServiceImpl.class);

	private static final String DEUX_POINTS=":";
	private static final String PAGE_SEPARATEUR = "----page-separator----";

	@Value("${options}")
	private String options;
    @Value("#{'${possibleValues}'.split(';')}")
	private List<String> possibleValues;

	@Override
	/**
	 * Permet de reconnaitre le texte
	 */
	public ResultatPdf extract(final File pdfFile) throws IOException {
		LOGGER.info("Début du traitement du fichier {}", pdfFile.getPath());
		
		Ocr.setUp(); // one time setup
		

		Zone zoneEchantillon = new Zone(new Point(540, 114), new Point(807, 140));
		LOGGER.info("zoneEchantillon : {}", zoneEchantillon.toString());
		
		Zone zone1 = new Zone(new Point(500, 370), new Point(800, 837));
		LOGGER.info("Zone1 : {}", zone1.toString());
		
		Zone zoneInterpretation = new Zone(new Point(0, 795), new Point(800, 1150));
		LOGGER.info("ZoneInterpretation : {}", zoneInterpretation.toString());

		String zoneEchantillonValue = this.zoneReading(pdfFile.getPath(), zoneEchantillon);				
		String zone1Value = this.zoneReading(pdfFile.getPath(), zone1);				
		String zoneInterpretationValue = this.zoneReading(pdfFile.getPath(), zoneInterpretation);

		ResultatPdf result = new ResultatPdf();
        result.setPdfFilePath(pdfFile.getPath());
        String zoneEchantillonValueTempName = StringUtils.stripStart(zoneEchantillonValue, " ");
        zoneEchantillonValueTempName = StringUtils.stripEnd(zoneEchantillonValueTempName, " ");
        zoneEchantillonValueTempName.replace("\n","");
        result.setEchantillon(zoneEchantillonValueTempName);

        String[] tempZone1 = zone1Value.split("\n");

        List<CompositionObj> compositionList = new ArrayList<>();
        for (int j = 0 ; j < tempZone1.length ;j++) {
            if(!StringUtils.stripStart(tempZone1[j]," ").isEmpty()) {
                CompositionObj zoneObj = new CompositionObj();
                boolean valid = false;
                for(String valeurPossible : this.possibleValues) {
                    String tempP = StringUtils.stripAccents(valeurPossible.toLowerCase());
                    String currentValueTemp = StringUtils.stripAccents(tempZone1[j].toLowerCase());
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
                    int lastSpaceIndex = currentValue.lastIndexOf(" ");
                    LOGGER.debug("lastIndex = {}", lastSpaceIndex);
                    if(lastSpaceIndex > -1) {
                        String name = currentValue.substring(0, lastSpaceIndex);
                        LOGGER.debug("name = {}", name);
                        String percentValue = currentValue.substring(lastSpaceIndex, currentValue.length());

                        double percent = 0;
                        try {
                            percent = Double.parseDouble(percentValue.replace(" ",""));
                        }
                        catch(NumberFormatException e) {
                            LOGGER.warn("Erreur de parsing du nombre {}",percentValue);
                        }
                        String tempName = StringUtils.stripAccents(name.toLowerCase());
                        tempName = StringUtils.stripStart(tempName, " ");
                        tempName = StringUtils.stripEnd(tempName, " ");
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
        result.setCompositions(compositionList);
		LOGGER.info("Fin du traitement du fichier {}", pdfFile.getName());
		return result;
	}

	
	private String zoneReading(final String pdfDoc, final Zone zone) {
		Ocr ocr = new Ocr(); // create a new OCR engine
		ocr.startEngine(Ocr.LANGUAGE_FRA, Ocr.SPEED_SLOW); 
		// int pageIndex, int startX, int startY, int width, int height
		String s = ocr.recognize(pdfDoc, -1, zone.getDebut().getX(), zone.getDebut().getY(), zone.getWidth(), zone.getHeigth(), Ocr.RECOGNIZE_TYPE_TEXT, Ocr.OUTPUT_FORMAT_PLAINTEXT,this.options);
		return s;
	}
}
