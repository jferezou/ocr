package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.HibernateDao;
import com.perso.bdd.dao.ParamFleursDao;
import com.perso.bdd.entity.parametrage.FleursEntity;
import org.apache.log4j.Logger;
import org.hibernate.NonUniqueResultException;
import org.hibernate.query.Query;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.List;

public class FleursDaoImpl extends HibernateDao implements ParamFleursDao {

    final static Logger LOGGER = Logger.getLogger(FleursDaoImpl.class);

    @Override
    public List<FleursEntity> getAllFleurs() {
        Query requete = getCurrentSession().createQuery("from FleursEntity");

        List<FleursEntity> fleurs = null;

        try {
            fleurs = requete.getResultList();
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return fleurs;
    }

    @Override
    public FleursEntity findByName(String nom) throws NoResultException {
        Query requete = getCurrentSession().createQuery("from FleursEntity where nom=:nom");
        requete.setParameter("nom", nom);

        FleursEntity fleurEntity = null;

        try {
            fleurEntity = (FleursEntity) requete.getSingleResult();
        } catch (NonUniqueResultException nonUniqueException) {
            LOGGER.debug("Pas de fleur trouvée avec le nom : {}" + nom);
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return fleurEntity;
    }

    /**
     * {@inheritDoc}
     * @param fleurEntity
     */
    @Override
    @Transactional
    public void deleteFleur(final FleursEntity fleurEntity) {
        this.getCurrentSession().delete(fleurEntity);
    }

    /**
     * {@inheritDoc}
     * @param fleurEntity
     */
    @Override
    @Transactional
    public void updateFleur(final FleursEntity fleurEntity) {
        this.getCurrentSession().merge(fleurEntity);
    }

    /**
     * {@inheritDoc}
     * @param fleurEntity
     */
    @Override
    @Transactional
    public void createFleur(final FleursEntity fleurEntity) {
        this.getCurrentSession().save(fleurEntity);
    }

}
