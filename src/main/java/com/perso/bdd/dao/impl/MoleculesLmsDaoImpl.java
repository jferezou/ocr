package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.HibernateDao;
import com.perso.bdd.dao.ParamMoleculesLmsDao;
import com.perso.bdd.entity.parametrage.FleursEntity;
import com.perso.bdd.entity.parametrage.MoleculesLmsEntity;
import org.apache.log4j.Logger;
import org.hibernate.NonUniqueResultException;
import org.hibernate.query.Query;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.List;

public class MoleculesLmsDaoImpl extends HibernateDao implements ParamMoleculesLmsDao {

    final static Logger LOGGER = Logger.getLogger(MoleculesLmsDaoImpl.class);

    @Override
    public List<MoleculesLmsEntity> getAllMoleculesLms() {

        Query requete = getCurrentSession().createQuery("from MoleculesLmsEntity");

        List<MoleculesLmsEntity> moleculesLmsList = null;

        try {
            moleculesLmsList = requete.getResultList();
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return moleculesLmsList;
    }

    @Override
    public MoleculesLmsEntity findByName(String nom) throws NoResultException {
        Query requete = getCurrentSession().createQuery("from MoleculesLmsEntity where nom=:nom");
        requete.setParameter("nom", nom);

        MoleculesLmsEntity moleculesLmsEntity = null;

        try {
            moleculesLmsEntity = (MoleculesLmsEntity) requete.getSingleResult();
        } catch (NonUniqueResultException nonUniqueException) {
            LOGGER.debug("Pas de molecules Lms trouvée avec le nom : {}" + nom);
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return moleculesLmsEntity;
    }

    /**
     * {@inheritDoc}
     * @param moleculesLmsEntity
     */
    @Override
    @Transactional
    public void deleteMoleculesLms(final MoleculesLmsEntity moleculesLmsEntity) {
        this.getCurrentSession().delete(moleculesLmsEntity);
    }

    /**
     * {@inheritDoc}
     * @param moleculesLmsEntity
     */
    @Override
    @Transactional
    public void updateMoleculesLms(final MoleculesLmsEntity moleculesLmsEntity) {
        this.getCurrentSession().merge(moleculesLmsEntity);
    }

    /**
     * {@inheritDoc}
     * @param moleculesLmsEntity
     */
    @Override
    @Transactional
    public void createMoleculesLms(final MoleculesLmsEntity moleculesLmsEntity) {
        this.getCurrentSession().save(moleculesLmsEntity);
    }

}
