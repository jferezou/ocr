package com.perso.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CSVUtils {

    private static final char DEFAULT_SEPARATOR = ';';



    public static void writeResult(final Writer fw, final ResultatPdf resultatPdf)  throws IOException {


        final String echantillon = resultatPdf.getEchantillon();
        for(CompositionObj cptObj : resultatPdf.getCompositions()) {
            writeLine(fw, Arrays.asList(echantillon, cptObj.getValue(), cptObj.getPercentage().toString(), cptObj.getType()));
        }
    }

    public static void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, '"');
    }


    //https://tools.ietf.org/html/rfc4180
    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    public static void writeLine(Writer w, List<String> values, char customQuote) throws IOException {

        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(DEFAULT_SEPARATOR);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());


    }

}
