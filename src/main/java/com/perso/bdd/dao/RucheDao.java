package com.perso.bdd.dao;

import com.perso.bdd.entity.RuchesEntity;

public interface RucheDao {
    void deleteRuche(final String rucheName);
    void updateRuche(final RuchesEntity ruchesentity);
    void createRuche(final RuchesEntity ruchesentity);
    RuchesEntity getRuchesByName(final String rucheName);
}
