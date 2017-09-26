package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.PalynologieDao;
import com.perso.bdd.entity.PalynologieEntity;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

public class PalynologieDaoImpl extends HibernateDao implements PalynologieDao {

    final static Logger LOGGER = Logger.getLogger(PalynologieDaoImpl.class);

    /**
     * {@inheritDoc}
     * @param palynologieEntity
     */
    @Override
    @Transactional
    public void deletePalynologie(final PalynologieEntity palynologieEntity) {
        this.getCurrentSession().delete(palynologieEntity);
    }

    /**
     * {@inheritDoc}
     * @param palynologieEntity
     */
    @Override
    @Transactional
    public void updatePalynologie(final PalynologieEntity palynologieEntity) {
        this.getCurrentSession().merge(palynologieEntity);
    }

    /**
     * {@inheritDoc}
     * @param palynologieEntity
     */
    @Override
    @Transactional
    public void createPalynologie(final PalynologieEntity palynologieEntity) {
        this.getCurrentSession().save(palynologieEntity);
    }

}
