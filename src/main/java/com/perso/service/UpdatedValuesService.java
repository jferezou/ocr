package com.perso.service;

import com.perso.utils.ResponseTraitement2;
import com.perso.utils.ResultatPdf;

import java.util.List;

public interface UpdatedValuesService {

    void parseAndSave(final String value);
    void parseAndSavet2(final String value);
    String getCsv();
    String getCsvt2();
    void cleanT1Map();
    void cleanT2Map();
    void fillT1Map(List<ResultatPdf> newValues);
    void fillT2Map(final List<ResponseTraitement2> newValues);
}
