package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.ResidusLmsDao;
import com.perso.bdd.entity.ResidusLmsEntity;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

public class ResidusLmsDaoImpl extends HibernateDao implements ResidusLmsDao {

    final static Logger LOGGER = Logger.getLogger(ResidusLmsDaoImpl.class);

    /**
     * {@inheritDoc}
     * @param residusLmsEntity
     */
    @Override
    @Transactional
    public void deleteResidusLms(final ResidusLmsEntity residusLmsEntity) {
        this.getCurrentSession().delete(residusLmsEntity);
    }

    /**
     * {@inheritDoc}
     * @param residusLmsEntity
     */
    @Override
    @Transactional
    public void updateResidusLms(final ResidusLmsEntity residusLmsEntity) {
        this.getCurrentSession().merge(residusLmsEntity);
    }

    /**
     * {@inheritDoc}
     * @param residusLmsEntity
     */
    @Override
    @Transactional
    public void createResidusLms(final ResidusLmsEntity residusLmsEntity) {
        this.getCurrentSession().save(residusLmsEntity);
    }

}
