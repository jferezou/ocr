package com.perso.service;

import com.perso.exception.BddException;
import com.perso.pojo.palynologie.PalynologieDocument;
import com.perso.pojo.residus.ResidusDocument;

public interface InsertService {
    void insertNewResidus(ResidusDocument result) throws BddException;
    void insertNewPalynologie(PalynologieDocument result) throws BddException;
}
