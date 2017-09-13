package com.perso.service;

public interface UpdatedValuesService {

    void parseAndSave(final String value);
    void parseAndSavet2(final String value);
    String getCsv();
    String getCsvt2();
    void cleanT1Map();
    void cleanT2Map();
}
