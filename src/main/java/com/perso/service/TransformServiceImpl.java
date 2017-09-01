package com.perso.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.asprise.ocr.Ocr;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;
import com.perso.utils.Point;
import com.perso.utils.ResultatPdf;
import com.perso.utils.Zone;

@Service("transformService")
public class TransformServiceImpl implements TransformService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransformServiceImpl.class);

	private static final String DEUX_POINTS=":";
	private static final String PAGE_SEPARATEUR = "----page-separator----";
	
	@Override
	/**
	 * Permet de convertir du texte vers le javanais
	 */
	public List<ResultatPdf> extract(final File pdfFile) throws IOException {
		LOGGER.info("Début du traitement du fichier {}", pdfFile.getPath());
		
		Ocr.setUp(); // one time setup
		

		Zone zoneEchantillon = new Zone(new Point(13, 133), new Point(370, 155));
		LOGGER.info("zoneEchantillon : {}", zoneEchantillon.toString());
		
		Zone zone1 = new Zone(new Point(285, 339), new Point(795, 823));
		LOGGER.info("Zone1 : {}", zone1.toString());
		
		Zone zoneInterpretation = new Zone(new Point(0, 795), new Point(800, 1150));
		LOGGER.info("ZoneInterpretation : {}", zoneInterpretation.toString());

		String zoneEchantillonValue = this.zoneReading(pdfFile.getPath(), zoneEchantillon);				
		String zone1Value = this.zoneReading(pdfFile.getPath(), zone1);				
		String zoneInterpretationValue = this.zoneReading(pdfFile.getPath(), zoneInterpretation);

		String[] echantillons = zoneEchantillonValue.split(PAGE_SEPARATEUR);
		String[] zone1S = zone1Value.split(PAGE_SEPARATEUR);
		String[] interpretations = zoneInterpretationValue.split(PAGE_SEPARATEUR);
		List<ResultatPdf> resultList = new ArrayList<>();
		for(int i =0 ; i < echantillons.length; i++) {
			ResultatPdf result = new ResultatPdf();
			result.setEchantillon(StringEscapeUtils.escapeJava(echantillons[i].split(DEUX_POINTS)[1]));
			String[] tempZone1 = zone1S[i].replace(DEUX_POINTS+" ", DEUX_POINTS+" \n").split("\n");;
			int curseur = 0;
			List<String> dominant = new ArrayList<>();
			List<String> accompagnement = new ArrayList();
			List<String> isole = new ArrayList();
			for (int j = 0 ; j < tempZone1.length ;j++) {
				if(tempZone1[j].contains(DEUX_POINTS)) {
					curseur++;
				}
				else {
					if(curseur == 1) {
						dominant.add(tempZone1[j]);
					}
					else if(curseur == 2) {
						accompagnement.add(tempZone1[j]);
					}
					else if(curseur == 3) {
						isole.add(tempZone1[j]);
					}
				}
			}
			result.setDominant(dominant);
			result.setAccompagnement(accompagnement);
			result.setIsole(isole);
			result.setInterpretation(StringEscapeUtils.escapeJava(interpretations[i].split("Fait à")[0].split("tat[iï]on:")[1]));
			resultList.add(result);
		}

		LOGGER.info("Fin du traitement du fichier {}", pdfFile.getName());
		return resultList;
	}

	
	private String zoneReading(final String pdfDoc, final Zone zone) {
		Ocr ocr = new Ocr(); // create a new OCR engine
		ocr.startEngine(Ocr.LANGUAGE_FRA, Ocr.SPEED_SLOW); 
		// int pageIndex, int startX, int startY, int width, int height
		String s = ocr.recognize(pdfDoc, -1, zone.getDebut().getX(), zone.getDebut().getY(), zone.getWidth(), zone.getHeigth(), Ocr.RECOGNIZE_TYPE_TEXT, Ocr.OUTPUT_FORMAT_PLAINTEXT);
		return s;
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
