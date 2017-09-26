package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.PalynologieDocumentDao;
import com.perso.bdd.entity.PalynologieDocumentEntity;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

public class PalynologieDocumentDaoImpl extends HibernateDao implements PalynologieDocumentDao {

    final static Logger LOGGER = Logger.getLogger(PalynologieDocumentDaoImpl.class);


    /**
     * {@inheritDoc}
     * @param palynologieDocumentEntity
     */
    @Override
    @Transactional
    public void deletePalynologieDocument(final PalynologieDocumentEntity palynologieDocumentEntity) {
        this.getCurrentSession().delete(palynologieDocumentEntity);
    }

    /**
     * {@inheritDoc}
     * @param palynologieDocumentEntity
     */
    @Override
    @Transactional
    public void updatePalynologieDocument(final PalynologieDocumentEntity palynologieDocumentEntity) {
        this.getCurrentSession().merge(palynologieDocumentEntity);
    }

    /**
     * {@inheritDoc}
     * @param palynologieDocumentEntity
     */
    @Override
    @Transactional
    public void createPalynologieDocument(final PalynologieDocumentEntity palynologieDocumentEntity) {
        this.getCurrentSession().save(palynologieDocumentEntity);
    }

}
