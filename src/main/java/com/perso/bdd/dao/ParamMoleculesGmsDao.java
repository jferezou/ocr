package com.perso.bdd.dao;

import com.perso.bdd.entity.parametrage.MoleculeEntity;
import com.perso.bdd.entity.parametrage.MoleculesGmsEntity;
import com.perso.exception.BddException;

import javax.persistence.NoResultException;
import java.util.List;

public interface ParamMoleculesGmsDao {
    List<MoleculeEntity> getAllMoleculesGms();
    MoleculesGmsEntity findByName(final String moleculeName) throws BddException;
    MoleculesGmsEntity findByNameContaining(String nom) throws BddException;
    void deleteMoleculesGms(final MoleculesGmsEntity moleculesGmsEntity);
    void updateMoleculesGms(final MoleculesGmsEntity moleculesGmsEntity);
    void createMoleculesGms(final MoleculesGmsEntity moleculesGmsEntity);
}
