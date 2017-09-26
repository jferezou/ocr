package com.perso.bdd.dao;

import com.perso.bdd.entity.parametrage.FleursEntity;
import com.perso.bdd.entity.parametrage.MoleculesLmsEntity;

import javax.persistence.NoResultException;
import java.util.List;

public interface ParamFleursDao {
    List<FleursEntity> getAllFleurs();
    FleursEntity findByName(final String fleurName) throws NoResultException;
    void deleteFleur(final FleursEntity fleursEntity);
    void updateFleur(final FleursEntity fleursEntity);
    void createFleur(final FleursEntity fleursEntity);
}
