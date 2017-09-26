package com.perso.bdd.dao;

import com.perso.bdd.entity.parametrage.MoleculesLmsEntity;

import javax.persistence.NoResultException;
import java.util.List;

public interface ParamMoleculesLmsDao {
    List<MoleculesLmsEntity> getAllMoleculesLms();
    MoleculesLmsEntity findByName(final String moleculeName) throws NoResultException;
    void deleteMoleculesLms(final MoleculesLmsEntity moleculesLmsEntity);
    void updateMoleculesLms(final MoleculesLmsEntity moleculesLmsEntity);
    void createMoleculesLms(final MoleculesLmsEntity moleculesLmsEntity);
}
