package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.ResidusDocumentDao;
import com.perso.bdd.entity.ResidusDocumentEntity;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ResidusDocumentDaoImpl extends HibernateDao implements ResidusDocumentDao {

    final static Logger LOGGER = Logger.getLogger(ResidusDocumentDaoImpl.class);


    /**
     * {@inheritDoc}
     * @param residusDocumentEntity
     */
    @Override
    @Transactional
    public void deleteResidusDocument(final ResidusDocumentEntity residusDocumentEntity) {
        this.getCurrentSession().delete(residusDocumentEntity);
    }

    /**
     * {@inheritDoc}
     * @param residusDocumentEntity
     */
    @Override
    @Transactional
    public void updateResidusDocument(final ResidusDocumentEntity residusDocumentEntity) {
        this.getCurrentSession().merge(residusDocumentEntity);
    }

    /**
     * {@inheritDoc}
     * @param residusDocumentEntity
     */
    @Override
    @Transactional
    public void createResidusDocument(final ResidusDocumentEntity residusDocumentEntity) {
        this.getCurrentSession().save(residusDocumentEntity);
    }

}
