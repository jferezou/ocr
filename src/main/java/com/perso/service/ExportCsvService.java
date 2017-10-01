package com.perso.service;

import com.perso.bdd.entity.PalynologieDocumentEntity;
import com.perso.bdd.entity.ResidusDocumentEntity;
import com.perso.pojo.palynologie.PalynologieDocument;

import java.io.IOException;

public interface ExportCsvService {
    String writeResult(final ResidusDocumentEntity residusDocument)  throws IOException;
    String writeResult(final PalynologieDocumentEntity palynologieDocument)  throws IOException;
}
