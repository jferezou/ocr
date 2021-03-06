package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.ResidusDocumentDao;
import com.perso.bdd.entity.PalynologieDocumentEntity;
import com.perso.bdd.entity.ResidusDocumentEntity;
import com.perso.bdd.entity.parametrage.EspeceEntity;
import org.apache.log4j.Logger;
import org.hibernate.NonUniqueResultException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    @Transactional
    public List<ResidusDocumentEntity> getAllResidusDocument(){
        Query requete = getCurrentSession().createQuery("from ResidusDocumentEntity");

        List<ResidusDocumentEntity> residusDocumentEntityList = null;

        try {
            residusDocumentEntityList = requete.getResultList();
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return residusDocumentEntityList;
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

    @Override
    @Transactional
    public ResidusDocumentEntity findByIdentifiant(final String identifiant) {
        Query requete = getCurrentSession().createQuery("from ResidusDocumentEntity where identifiant=:identifiant");
        requete.setParameter("identifiant", identifiant);

        ResidusDocumentEntity residusDocumentEntity = null;

        try {
            residusDocumentEntity = (ResidusDocumentEntity) requete.getSingleResult();
        } catch (NonUniqueResultException nonUniqueException) {
            LOGGER.debug("Plusieurs residus trouvée avec l'identifiant echantillon : {}" + identifiant);
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return residusDocumentEntity;
    }

}
