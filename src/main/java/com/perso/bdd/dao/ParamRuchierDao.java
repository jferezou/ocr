package com.perso.bdd.dao;

import com.perso.bdd.entity.parametrage.RuchierEntity;

import javax.persistence.NoResultException;

public interface ParamRuchierDao {
    RuchierEntity findByCorrespondance(final int idCorrespondance) throws NoResultException;
    void deleteRuchier(final RuchierEntity ruchierEntity);
    void updateRuchier(final RuchierEntity ruchierEntity);
    void createRuchier(final RuchierEntity ruchierEntity);
}
