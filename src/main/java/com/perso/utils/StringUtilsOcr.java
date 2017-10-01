package com.perso.utils;

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
        Pattern pattern = Pattern.compile("(\\s)+");
        Matcher matcher = pattern.matcher(stringValue);
        matcher.find();
        int value = -1;
        try {
            value = matcher.start(1);
        } catch (IllegalStateException | IndexOutOfBoundsException e) {
            LOGGER.warn("Erreur lors de la récupération de l'espace pour : {}",stringValue);
        }
        LOGGER.debug("Index du 1er espace de la chaîne {} : {}", stringValue, value);
        return value;
    }

    public static int getLastUnderscore(final String stringValue) {
        Pattern pattern = Pattern.compile("(_)+");
        Matcher matcher = pattern.matcher(stringValue);
        int value = -1;
        try {
            while(matcher.find()) {
                value = matcher.start(1);
            }
        } catch (IllegalStateException | IndexOutOfBoundsException e) {
            LOGGER.warn("Erreur lors de la récupération de _ pour : {}",stringValue);
        }
        LOGGER.debug("Index du dernier _ de la chaîne {} : {}", stringValue, value);
        return value;
    }
}
