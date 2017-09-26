package com.perso.bdd.dao;

import com.perso.bdd.entity.parametrage.TypeEntity;

import javax.persistence.NoResultException;

public interface ParamTypeDao {
    TypeEntity findByName(final String typeName) throws NoResultException;
    void deleteType(final TypeEntity typeEntity);
    void updateType(final TypeEntity typeEntity);
    void createType(final TypeEntity typeEntity);
}
