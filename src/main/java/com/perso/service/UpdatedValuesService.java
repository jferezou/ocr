package com.perso.service;

import com.perso.exception.BddException;
import com.perso.exception.ParsingException;
import com.perso.pojo.residus.ResidusDocument;
import com.perso.pojo.palynologie.PalynologieDocument;

import java.util.List;
import java.util.Map;

public interface UpdatedValuesService {

    void parseAndSavePalynologie(final String value) throws ParsingException, BddException;
    void parseAndSaveResidus(final String value) throws ParsingException, BddException;
    String getCsvPalynologie();
    String getCsvResidus();
    void cleanPalynologieMap();
    void cleanResidusMap();
    void fillPalynologieMap(final List<PalynologieDocument> newValues);
    void fillResidusMap(final List<ResidusDocument> newValues);
    Map<Integer, PalynologieDocument> getValeursPalynologie();
    Map<Integer, ResidusDocument> getValeursResidus();
}
