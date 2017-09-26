package com.perso.bdd.dao;

import com.perso.bdd.entity.parametrage.MatriceEntity;

import javax.persistence.NoResultException;

public interface ParamMatriceDao {
    MatriceEntity findByIdentifiant(final String identifiant) throws NoResultException;
    void deleteMatrice(final MatriceEntity matriceEntity);
    void updateMatrice(final MatriceEntity matriceEntity);
    void createMatrice(final MatriceEntity matriceEntity);
}
