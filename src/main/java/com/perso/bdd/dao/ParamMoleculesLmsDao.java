package com.perso.bdd.dao;

import com.perso.bdd.entity.parametrage.MoleculeEntity;
import com.perso.bdd.entity.parametrage.MoleculesLmsEntity;
import com.perso.exception.BddException;

import javax.persistence.NoResultException;
import java.util.List;

public interface ParamMoleculesLmsDao {
    List<MoleculeEntity> getAllMoleculesLms();
    MoleculesLmsEntity findByName(final String moleculeName) throws BddException;
    MoleculesLmsEntity findByNameContaining(String nom) throws BddException;
    void deleteMoleculesLms(final MoleculesLmsEntity moleculesLmsEntity);
    void updateMoleculesLms(final MoleculesLmsEntity moleculesLmsEntity);
    void createMoleculesLms(final MoleculesLmsEntity moleculesLmsEntity);
}
