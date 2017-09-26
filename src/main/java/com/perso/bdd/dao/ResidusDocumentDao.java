package com.perso.bdd.dao;

import com.perso.bdd.entity.ResidusDocumentEntity;

public interface ResidusDocumentDao {
    void deleteResidusDocument(final ResidusDocumentEntity palynologieDocumentEntity);
    void updateResidusDocument(final ResidusDocumentEntity palynologieDocumentEntity);
    void createResidusDocument(final ResidusDocumentEntity palynologieDocumentEntity);
}
