package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.ParamMoleculesGmsDao;
import com.perso.bdd.entity.parametrage.MoleculesGmsEntity;
import org.apache.log4j.Logger;
import org.hibernate.NonUniqueResultException;
import org.hibernate.query.Query;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.List;

public class MoleculesGmsDaoImpl extends HibernateDao implements ParamMoleculesGmsDao {

    final static Logger LOGGER = Logger.getLogger(MoleculesGmsDaoImpl.class);
    @Override
    public List<MoleculesGmsEntity> getAllMoleculesGms() {

        Query requete = getCurrentSession().createQuery("from MoleculesGmsEntity");

        List<MoleculesGmsEntity> moleculesGmsList = null;

        try {
            moleculesGmsList = requete.getResultList();
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return moleculesGmsList;
    }
    @Override
    public MoleculesGmsEntity findByName(String nom) throws NoResultException {
        Query requete = getCurrentSession().createQuery("from MoleculesGmsEntity where nom=:nom");
        requete.setParameter("nom", nom);

        MoleculesGmsEntity moleculesGmsEntity = null;

        try {
            moleculesGmsEntity = (MoleculesGmsEntity) requete.getSingleResult();
        } catch (NonUniqueResultException nonUniqueException) {
            LOGGER.debug("Pas de molecules Gms trouvée avec le nom : {}" + nom);
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return moleculesGmsEntity;
    }

    /**
     * {@inheritDoc}
     * @param moleculesGmsEntity
     */
    @Override
    @Transactional
    public void deleteMoleculesGms(final MoleculesGmsEntity moleculesGmsEntity) {
        this.getCurrentSession().delete(moleculesGmsEntity);
    }

    /**
     * {@inheritDoc}
     * @param moleculesGmsEntity
     */
    @Override
    @Transactional
    public void updateMoleculesGms(final MoleculesGmsEntity moleculesGmsEntity) {
        this.getCurrentSession().merge(moleculesGmsEntity);
    }

    /**
     * {@inheritDoc}
     * @param moleculesGmsEntity
     */
    @Override
    @Transactional
    public void createMoleculesGms(final MoleculesGmsEntity moleculesGmsEntity) {
        this.getCurrentSession().save(moleculesGmsEntity);
    }

}
