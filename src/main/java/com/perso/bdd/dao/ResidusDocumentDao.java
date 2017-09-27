package com.perso.bdd.dao;

import com.perso.bdd.entity.ResidusDocumentEntity;

public interface ResidusDocumentDao {
    void deleteResidusDocument(final ResidusDocumentEntity residusDocumentEntity);
    void updateResidusDocument(final ResidusDocumentEntity residusDocumentEntity);
    void createResidusDocument(final ResidusDocumentEntity residusDocumentEntity);
    ResidusDocumentEntity findByIdentifiant(final String identifiant);
}
