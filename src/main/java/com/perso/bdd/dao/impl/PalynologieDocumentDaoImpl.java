package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.PalynologieDocumentDao;
import com.perso.bdd.entity.PalynologieDocumentEntity;
import com.perso.bdd.entity.ResidusDocumentEntity;
import org.apache.log4j.Logger;
import org.hibernate.NonUniqueResultException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
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
    @Override
    @Transactional
    public List<PalynologieDocumentEntity> getAllPalynologieDocument(){
        Query requete = getCurrentSession().createQuery("from PalynologieDocumentEntity");

        List<PalynologieDocumentEntity> palynologieDocumentEntityList = null;

        try {
            palynologieDocumentEntityList = requete.getResultList();
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return palynologieDocumentEntityList;
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

    @Override
    @Transactional
    public PalynologieDocumentEntity findByEchantillonId(String idEchantillon) {
        Query requete = getCurrentSession().createQuery("from PalynologieDocumentEntity where identifiant_echantillon=:idEchantillon");
        requete.setParameter("idEchantillon", idEchantillon);

        PalynologieDocumentEntity palynologieDocumentEntity = null;

        try {
            palynologieDocumentEntity = (PalynologieDocumentEntity) requete.getSingleResult();
        } catch (NonUniqueResultException nonUniqueException) {
            LOGGER.debug("Pas de fleur trouvée avec l'identifiant echa,tillon : {}" + idEchantillon);
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return palynologieDocumentEntity;
    }

}
