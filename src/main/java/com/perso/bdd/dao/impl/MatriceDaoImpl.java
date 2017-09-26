package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.HibernateDao;
import com.perso.bdd.dao.ParamMatriceDao;
import com.perso.bdd.entity.RuchesEntity;
import com.perso.bdd.entity.parametrage.MatriceEntity;
import org.apache.log4j.Logger;
import org.hibernate.NonUniqueResultException;
import org.hibernate.query.Query;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

public class MatriceDaoImpl extends HibernateDao implements ParamMatriceDao {

    final static Logger LOGGER = Logger.getLogger(MatriceDaoImpl.class);

    @Override
    public MatriceEntity findByIdentifiant(String identifiant) throws NoResultException {
        Query requete = getCurrentSession().createQuery("from MatriceEntity where identifiant=:identifiant");
        requete.setParameter("identifiant", identifiant);

        MatriceEntity matriceEntity = null;

        try {
            matriceEntity = (MatriceEntity) requete.getSingleResult();
        } catch (NonUniqueResultException nonUniqueException) {
            LOGGER.debug("Pas de matrice trouvée avec l'identifiant : {}" + identifiant);
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return matriceEntity;
    }

    /**
     * {@inheritDoc}
     * @param matriceEntity
     */
    @Override
    @Transactional
    public void deleteMatrice(final MatriceEntity matriceEntity) {
        this.getCurrentSession().delete(matriceEntity);
    }

    /**
     * {@inheritDoc}
     * @param matriceEntity
     */
    @Override
    @Transactional
    public void updateMatrice(final MatriceEntity matriceEntity) {
        this.getCurrentSession().merge(matriceEntity);
    }

    /**
     * {@inheritDoc}
     * @param matriceEntity
     */
    @Override
    @Transactional
    public void createMatrice(final MatriceEntity matriceEntity) {
        this.getCurrentSession().save(matriceEntity);
    }

}
