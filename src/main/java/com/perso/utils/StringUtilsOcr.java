package com.perso.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtilsOcr {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtilsOcr.class);
    public static int getfirstdigitIndex(final String stringValue) {
        Pattern pattern = Pattern.compile("^\\D*(\\d)");
        Matcher matcher = pattern.matcher(stringValue);
        matcher.find();
        int value = -1;
        try {
            value = matcher.start(1);
        } catch (IllegalStateException | IndexOutOfBoundsException e) {
            LOGGER.warn("Erreur lors de la récupération du 1er digit pour : {}",stringValue);
        }
        LOGGER.debug("Index du 1er digit de la chaîne {} : {}", stringValue, value);
        return value;
    }
    public static int getFirstSpace(final String stringValue) {
        int value = stringValue.indexOf(Constantes.ESPACE);
        LOGGER.debug("Index du 1er espace de la chaîne {} : {}", stringValue, value);
        return value;
    }


    public static int getFirstParenthese(final String stringValue) {
        int value = stringValue.indexOf(Constantes.PARENTHESEOUVRANTE);
        LOGGER.debug("Index du 1er ( de la chaîne {} : {}", stringValue, value);
        return value;
    }

    public static int getFirstDeuxPoint(final String stringValue) {
        int value = stringValue.indexOf(Constantes.DEUXPOINT);
        LOGGER.debug("Index du 1er : de la chaîne {} : {}", stringValue, value);
        return value;
    }
    public static int getLastUnderscore(final String stringValue) {
        int lastUnderScore = stringValue.lastIndexOf(Constantes.UNDERSCORE);

        LOGGER.debug("Index du dernier _ de la chaîne {} : {}", stringValue, lastUnderScore);
        return lastUnderScore;
    }

    public static int getLastSpace(final String stringValue) {
        int lastSpace = stringValue.lastIndexOf(Constantes.ESPACE);

        LOGGER.debug("Index du dernier ' ' de la chaîne {} : {}", stringValue, lastSpace);
        return lastSpace;
    }



    /**
     * on supprime les espaces au debut de la ligne et on en enlève un potentiel '-' à la fin
     * @param line
     * @return
     */
    public static String trimEtSupprimeTiretalaFin(final String line) {
        String value = StringUtils.trim(line);
        value = StringUtils.removeEnd(value,Constantes.TIRET);
        return value;
    }
}
