package com.perso.bdd.dao;

import com.perso.bdd.entity.ResidusLmsEntity;

public interface ResidusLmsDao {
    void deleteResidusLms(final ResidusLmsEntity residusLmsEntity);
    void updateResidusLms(final ResidusLmsEntity residusLmsEntity);
    void createResidusLms(final ResidusLmsEntity residusLmsEntity);
}
