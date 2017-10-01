package com.perso.bdd.dao;

import com.perso.bdd.entity.PalynologieDocumentEntity;

import java.util.List;

public interface PalynologieDocumentDao {
    void deletePalynologieDocument(final PalynologieDocumentEntity palynologieDocumentEntity);
    void updatePalynologieDocument(final PalynologieDocumentEntity palynologieDocumentEntity);
    void createPalynologieDocument(final PalynologieDocumentEntity palynologieDocumentEntity);
    PalynologieDocumentEntity findByEchantillonId(final String idEchantillon);
    List<PalynologieDocumentEntity> getAllPalynologieDocument();
}
