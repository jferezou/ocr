package com.perso.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CSVUtils {

    private static final char DEFAULT_SEPARATOR = ';';



    public static void writeResult(final Writer fw, final ResultatPdf resultatPdf)  throws IOException {

        int max = Math.max(resultatPdf.getDominant().size(), Math.max(resultatPdf.getAccompagnement().size(), resultatPdf.getIsole().size()));

        int iterator = 0;
        while (iterator < max) {
            String dominant = "";
            String validStringDominant = "";
            if(resultatPdf.getDominant().size() > iterator && resultatPdf.getDominant().get(iterator).isValid()) {
                dominant = resultatPdf.getDominant().get(iterator).getValue();
                if(!resultatPdf.getDominant().get(iterator).isValid()) {
                    validStringDominant = "X";
                }
            }

            String accompagnement = "";
            String validStringaccompagnement = "";
            if(resultatPdf.getAccompagnement().size() > iterator && resultatPdf.getAccompagnement().get(iterator).isValid()) {
                accompagnement = resultatPdf.getAccompagnement().get(iterator).getValue();
                if(!resultatPdf.getAccompagnement().get(iterator).isValid()) {
                    validStringaccompagnement = "X";
                }
            }
            String isole = "";
            String validStringisole = "";
            if(resultatPdf.getIsole().size() > iterator) {
                isole = resultatPdf.getIsole().get(iterator).getValue();
                if(!resultatPdf.getIsole().get(iterator).isValid()) {
                    validStringisole = "X";
                }
            }


            if(iterator == 0) {
                writeLine(fw, Arrays.asList(resultatPdf.getEchantillon(), dominant, validStringDominant, accompagnement, validStringaccompagnement, isole, validStringisole, resultatPdf.getInterpretation()));
            }
            else {
                writeLine(fw, Arrays.asList("", dominant, validStringDominant, accompagnement, validStringaccompagnement, isole, validStringisole,""));
            }

            iterator++;
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
