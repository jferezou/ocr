package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.ResidusGmsDao;
import com.perso.bdd.entity.ResidusGmsEntity;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ResidusGmsDaoImpl extends HibernateDao implements ResidusGmsDao {

    final static Logger LOGGER = Logger.getLogger(ResidusGmsDaoImpl.class);

    /**
     * {@inheritDoc}
     * @param residusGmsEntity
     */
    @Override
    @Transactional
    public void deleteResidusGms(final ResidusGmsEntity residusGmsEntity) {
        this.getCurrentSession().delete(residusGmsEntity);
    }

    /**
     * {@inheritDoc}
     * @param residusGmsEntity
     */
    @Override
    @Transactional
    public void updateResidusGms(final ResidusGmsEntity residusGmsEntity) {
        this.getCurrentSession().merge(residusGmsEntity);
    }

    /**
     * {@inheritDoc}
     * @param residusGmsEntity
     */
    @Override
    @Transactional
    public void createResidusGms(final ResidusGmsEntity residusGmsEntity) {
        this.getCurrentSession().save(residusGmsEntity);
    }

}
