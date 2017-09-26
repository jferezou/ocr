package com.perso.bdd.dao;

import com.perso.bdd.entity.ResidusGmsEntity;

public interface ResidusGmsDao {
    void deleteResidusGms(final ResidusGmsEntity residusGmsEntity);
    void updateResidusGms(final ResidusGmsEntity residusGmsEntity);
    void createResidusGms(final ResidusGmsEntity residusGmsEntity);
}
