package com.perso.bdd.dao;

import com.perso.bdd.entity.parametrage.EspeceEntity;

import javax.persistence.NoResultException;
import java.util.List;

public interface ParamEspeceDao {
    List<EspeceEntity> getAllEspeces();
    EspeceEntity findByName(final String fleurName) throws NoResultException;
    void deleteEspece(final EspeceEntity especeEntity);
    void updateEspece(final EspeceEntity especeEntity);
    void createEspece(final EspeceEntity especeEntity);
}
