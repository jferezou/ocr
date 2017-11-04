package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.ParamEspeceDao;
import com.perso.bdd.entity.parametrage.EspeceEntity;
import org.apache.log4j.Logger;
import org.hibernate.NonUniqueResultException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.List;

@Repository
public class EspeceDaoImpl extends HibernateDao implements ParamEspeceDao {

    final static Logger LOGGER = Logger.getLogger(EspeceDaoImpl.class);

    @Override
    @Transactional
    public List<EspeceEntity> getAllEspeces() {
        Query requete = getCurrentSession().createQuery("from EspeceEntity");

        List<EspeceEntity> especes = null;

        try {
            especes = requete.getResultList();
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return especes;
    }

    @Override
    @Transactional
    public EspeceEntity findByName(final String nom) throws NoResultException {
        Query requete = getCurrentSession().createQuery("from EspeceEntity where nom=:nom");
        requete.setParameter("nom", nom);

        EspeceEntity especeEntity = null;

        try {
            especeEntity = (EspeceEntity) requete.getSingleResult();
        } catch (NonUniqueResultException nonUniqueException) {
            LOGGER.debug("Pas de espece trouvée avec le nom : {}" + nom);
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return especeEntity;
    }

    /**
     * {@inheritDoc}
     * @param especeEntity
     */
    @Override
    @Transactional
    public void deleteEspece(final EspeceEntity especeEntity) {
        this.getCurrentSession().delete(especeEntity);
    }

    /**
     * {@inheritDoc}
     * @param especeEntity
     */
    @Override
    @Transactional
    public void updateEspece(final EspeceEntity especeEntity) {
        this.getCurrentSession().merge(especeEntity);
    }

    /**
     * {@inheritDoc}
     * @param especeEntity
     */
    @Override
    @Transactional
    public void createEspece(final EspeceEntity especeEntity) {
        this.getCurrentSession().save(especeEntity);
    }

}
