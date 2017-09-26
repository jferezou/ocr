package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.HibernateDao;
import com.perso.bdd.dao.RucheDao;
import com.perso.bdd.entity.RuchesEntity;
import org.apache.log4j.Logger;
import org.hibernate.NonUniqueResultException;
import org.hibernate.query.Query;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

public class RucheDaoImpl extends HibernateDao implements RucheDao {

    final static Logger LOGGER = Logger.getLogger(RucheDaoImpl.class);

    /**
     * {@inheritDoc}
     * @param rucheName
     */
    @Override
    @Transactional
    public void deleteRuche(final String rucheName) {
        RuchesEntity ruchesentity = this.getRuchesByName(rucheName);
        this.getCurrentSession().delete(ruchesentity);
    }

    /**
     * {@inheritDoc}
     * @param ruchesentity
     */
    @Override
    @Transactional
    public void updateRuche(final RuchesEntity ruchesentity) {
        this.getCurrentSession().merge(ruchesentity);
    }

    /**
     * {@inheritDoc}
     * @param ruchesentity
     */
    @Override
    @Transactional
    public void createRuche(final RuchesEntity ruchesentity) {
        this.getCurrentSession().save(ruchesentity);
    }

    /**
     * {@inheritDoc}
     * @param rucheName
     * @return
     */
    @Override
    public RuchesEntity getRuchesByName(final String rucheName) throws NoResultException {
        Query requete = getCurrentSession().createQuery("from RuchesEntity where nom=:rucheName");
        requete.setParameter("rucheName", rucheName);

        RuchesEntity ruchesEntity = null;

        try {
            ruchesEntity = (RuchesEntity) requete.getSingleResult();
        } catch (NonUniqueResultException nonUniqueException) {
            LOGGER.debug("Pas de ruche trouvée avec le nom : {}" + rucheName);
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return ruchesEntity;
    }
}
