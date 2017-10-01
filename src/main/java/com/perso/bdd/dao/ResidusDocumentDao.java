package com.perso.bdd.dao;

import com.perso.bdd.entity.ResidusDocumentEntity;

import java.util.List;

public interface ResidusDocumentDao {
    void deleteResidusDocument(final ResidusDocumentEntity residusDocumentEntity);
    void updateResidusDocument(final ResidusDocumentEntity residusDocumentEntity);
    void createResidusDocument(final ResidusDocumentEntity residusDocumentEntity);
    ResidusDocumentEntity findByIdentifiant(final String identifiant);
    List<ResidusDocumentEntity> getAllResidusDocument();
}
