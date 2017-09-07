import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.SimpleRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.util.List;

public class Test {


    public static void main(String[] args) {

        try {
        File file = new File("D:\\dev\\ocr\\src\\main\\resources\\com\\perso\\170703171333_0001_1.pdf");
        PDFDocument document = new PDFDocument();
        document.load(file);

            // create renderer
            SimpleRenderer renderer = new SimpleRenderer();

            // set resolution (in DPI)
            renderer.setResolution( 300 );
            // render the images
            List<Image> images = renderer.render( document );

            String imExtension = "png";
            // write the images to file
            for (int iPage = 0; iPage < images.size(); iPage++) {
                ImageIO.write( (RenderedImage) images.get( iPage ), imExtension,
                        new File( "" + iPage + "." + imExtension ) );
            }

        BufferedImage bufferedImage = new BufferedImage(200, 200,
                BufferedImage.TYPE_BYTE_INDEXED);
            bufferedImage = ImageIO.read(new File("D:\\dev\\ocr\\0.png"));

            float scaleFactor = 1.3f;
            RescaleOp op = new RescaleOp(scaleFactor, 20.0f, null);
            op.filter(bufferedImage, bufferedImage);
        }
        catch (Exception e) { System.out.println(e); }

    }
}
