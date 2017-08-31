package com.perso.service;

import java.io.File;
import java.io.IOException;
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
import com.perso.utils.Zone;

@Service("transformService")
public class TransformServiceImpl implements TransformService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransformServiceImpl.class);

	private static final String DEUX_POINTS=":";
	
	@Override
	/**
	 * Permet de convertir du texte vers le javanais
	 */
	public void extract(final File pdfFile) throws IOException {
		LOGGER.info("Début du traitement du fichier {}", pdfFile.getPath());
		
		Ocr.setUp(); // one time setup
		

		Zone zoneEchantillon = new Zone(new Point(285, 339), new Point(795, 823));
		LOGGER.info("zoneEchantillon : {}", zoneEchantillon.toString());
		
		Zone zone1 = new Zone(new Point(285, 339), new Point(795, 823));
		LOGGER.info("Zone1 : {}", zone1.toString());
		
		Zone zone2 = new Zone(new Point(0, 795), new Point(800, 1150));
		LOGGER.info("Zone2 : {}", zone2.toString());

		String zoneEchantillonValue = this.zoneReading(pdfFile.getPath(), zoneEchantillon);		
		LOGGER.info("\nZoneEchantillon :\n{}", zoneEchantillonValue);
		String zone1Value = this.zoneReading(pdfFile.getPath(), zone1);		
		LOGGER.info("\nZone1 :\n{}", zone1Value);
		String zone2Value = this.zoneReading(pdfFile.getPath(), zone2);
		LOGGER.info("\nZone2 :\n{}", zone2Value);

		LOGGER.info("Fin du traitement du fichier {}", pdfFile.getName());
	}

	
	private String zoneReading(final String pdfDoc, final Zone zone) {
		Ocr ocr = new Ocr(); // create a new OCR engine
		ocr.startEngine("fra", Ocr.SPEED_FASTEST); 
		// int pageIndex, int startX, int startY, int width, int height
		String s = ocr.recognize(pdfDoc,0, zone.getDebut().getX(), zone.getDebut().getY(), zone.getWidth(), zone.getHeigth(), Ocr.RECOGNIZE_TYPE_TEXT, Ocr.OUTPUT_FORMAT_PLAINTEXT);
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
