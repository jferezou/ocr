package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.ParamTypeDao;
import com.perso.bdd.entity.parametrage.TypeEntity;
import com.perso.exception.BddException;
import org.apache.log4j.Logger;
import org.hibernate.NonUniqueResultException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

@Repository
public class TypeDaoImpl extends HibernateDao implements ParamTypeDao {

    final static Logger LOGGER = Logger.getLogger(TypeDaoImpl.class);

    @Override
    @Transactional
    public TypeEntity findByName(final String typeName) throws BddException {
        Query requete = getCurrentSession().createQuery("from TypeEntity where valeur=:typeName");
        requete.setParameter("typeName", typeName);

        TypeEntity typeEntity = null;

        try {
            typeEntity = (TypeEntity) requete.getSingleResult();
        } catch (NoResultException e) {
            throw new BddException("Pas de type trouvé pour le type " + typeName);
        }catch (NonUniqueResultException nonUniqueException) {
            LOGGER.debug("Pas de type trouvée avec le nom : {}" + typeName);
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        if(typeEntity == null) {
            throw new BddException("Pas de type trouvé pour le type " + typeName);
        }
        return typeEntity;
    }

    /**
     * {@inheritDoc}
     * @param typeEntity
     */
    @Override
    @Transactional
    public void deleteType(final TypeEntity typeEntity) {
        this.getCurrentSession().delete(typeEntity);
    }

    /**
     * {@inheritDoc}
     * @param typeEntity
     */
    @Override
    @Transactional
    public void updateType(final TypeEntity typeEntity) {
        this.getCurrentSession().merge(typeEntity);
    }

    /**
     * {@inheritDoc}
     * @param typeEntity
     */
    @Override
    @Transactional
    public void createType(final TypeEntity typeEntity) {
        this.getCurrentSession().save(typeEntity);
    }

}
