package com.perso.bdd.dao;

import com.perso.bdd.entity.PalynologieEntity;

public interface PalynologieDao {
    void deletePalynologie(final PalynologieEntity palynologieEntity);
    void updatePalynologie(final PalynologieEntity palynologieEntity);
    void createPalynologie(final PalynologieEntity palynologieEntity);
}
