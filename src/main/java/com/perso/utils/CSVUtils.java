package com.perso.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CSVUtils {

    private static final char DEFAULT_SEPARATOR = ';';



    public static String writeResult(final ResultatPdf resultatPdf)  throws IOException {

        final String echantillon = resultatPdf.getEchantillon();
        StringBuilder result = new StringBuilder();
        for(CompositionObj cptObj : resultatPdf.getCompositions()) {
            result.append(writeLine(Arrays.asList(echantillon, cptObj.getValue(), cptObj.getPercentage().toString(), cptObj.getType()),'"'));
        }
        return result.toString();
    }


    public static String writeResult(final ResponseTraitement2 responseTraitement2)  throws IOException {

        final String reference = responseTraitement2.getReference();
        StringBuilder result = new StringBuilder();
        for(Traitement2Obj cptObj : responseTraitement2.getGmsList()) {
            result.append(writeLine(Arrays.asList(reference, cptObj.getValue(), cptObj.getPourcentage().toString(), "GMS"),'"'));
        }
        for(Traitement2Obj cptObj : responseTraitement2.getLmsList()) {
            result.append(writeLine(Arrays.asList(reference, cptObj.getValue(), cptObj.getPourcentage().toString(), "LMS"),'"'));
        }
        return result.toString();
    }

    //https://tools.ietf.org/html/rfc4180
    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    public static String writeLine( List<String> values, char customQuote) throws IOException {

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
        return sb.toString();
    }

}
