package com.perso.utils;

import com.perso.service.TraitementT2ServiceImpl;
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
            LOGGER.error("Erreur lors de la récupération du 1er digit pour : "+ stringValue, e);
        }
        LOGGER.debug("Index du 1er digit de la chaîne {} : {}", stringValue, value);
        return value;
    }
}
