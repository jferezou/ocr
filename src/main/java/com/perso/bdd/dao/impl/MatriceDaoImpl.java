package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.ParamMatriceDao;
import com.perso.bdd.entity.parametrage.MatriceEntity;
import com.perso.exception.BddException;
import org.apache.log4j.Logger;
import org.hibernate.NonUniqueResultException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

@Repository
public class MatriceDaoImpl extends HibernateDao implements ParamMatriceDao {

    final static Logger LOGGER = Logger.getLogger(MatriceDaoImpl.class);

    @Override
    @Transactional
    public MatriceEntity findByIdentifiant(String identifiant) throws BddException {
        Query requete = getCurrentSession().createQuery("from MatriceEntity where identifiant=:identifiant");
        requete.setParameter("identifiant", identifiant);

        MatriceEntity matriceEntity = null;

        try {
            matriceEntity = (MatriceEntity) requete.getSingleResult();
        } catch (NoResultException e) {
            throw new BddException("Pas de matrice trouvée pour la correspondance " + identifiant);
        }
        catch (NonUniqueResultException nonUniqueException) {
            LOGGER.debug("Pas de matrice trouvée avec l'identifiant : {}" + identifiant);
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        if(matriceEntity == null) {
            throw new BddException("Pas de matrice trouvée pour la correspondance " + identifiant);
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
