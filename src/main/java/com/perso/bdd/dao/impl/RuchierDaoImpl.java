package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.HibernateDao;
import com.perso.bdd.dao.ParamRuchierDao;
import com.perso.bdd.entity.parametrage.RuchierEntity;
import org.apache.log4j.Logger;
import org.hibernate.NonUniqueResultException;
import org.hibernate.query.Query;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

public class RuchierDaoImpl extends HibernateDao implements ParamRuchierDao {

    final static Logger LOGGER = Logger.getLogger(RuchierDaoImpl.class);

    @Override
    public RuchierEntity findByCorrespondance(int idCorrespondance) throws NoResultException {
        Query requete = getCurrentSession().createQuery("from RuchierEntity where correspondance=:idCorrespondance");
        requete.setParameter("idCorrespondance", idCorrespondance);

        RuchierEntity ruchierEntity = null;

        try {
            ruchierEntity = (RuchierEntity) requete.getSingleResult();
        } catch (NonUniqueResultException nonUniqueException) {
            LOGGER.debug("Pas de ruchier trouvée avec l'identifiant : {}" + idCorrespondance);
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return ruchierEntity;
    }

    /**
     * {@inheritDoc}
     * @param ruchierEntity
     */
    @Override
    @Transactional
    public void deleteRuchier(final RuchierEntity ruchierEntity) {
        this.getCurrentSession().delete(ruchierEntity);
    }

    /**
     * {@inheritDoc}
     * @param ruchierEntity
     */
    @Override
    @Transactional
    public void updateRuchier(final RuchierEntity ruchierEntity) {
        this.getCurrentSession().merge(ruchierEntity);
    }

    /**
     * {@inheritDoc}
     * @param ruchierEntity
     */
    @Override
    @Transactional
    public void createRuchier(final RuchierEntity ruchierEntity) {
        this.getCurrentSession().save(ruchierEntity);
    }

}
