package com.perso.bdd.dao;

import com.perso.bdd.entity.parametrage.TypeEntity;
import com.perso.exception.BddException;

import javax.persistence.NoResultException;

public interface ParamTypeDao {
    TypeEntity findByName(final String typeName) throws BddException;
    void deleteType(final TypeEntity typeEntity);
    void updateType(final TypeEntity typeEntity);
    void createType(final TypeEntity typeEntity);
}
