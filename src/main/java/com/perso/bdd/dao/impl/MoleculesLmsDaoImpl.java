package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.ParamMoleculesLmsDao;
import com.perso.bdd.entity.parametrage.MoleculeEntity;
import com.perso.bdd.entity.parametrage.MoleculesLmsEntity;
import com.perso.exception.BddException;
import org.apache.log4j.Logger;
import org.hibernate.NonUniqueResultException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.List;

@Repository
public class MoleculesLmsDaoImpl extends HibernateDao implements ParamMoleculesLmsDao {

    final static Logger LOGGER = Logger.getLogger(MoleculesLmsDaoImpl.class);

    @Override
    @Transactional
    public List<MoleculeEntity> getAllMoleculesLms() {

        Query requete = getCurrentSession().createQuery("from MoleculesLmsEntity");

        List<MoleculeEntity> moleculesLmsList = null;

        try {
            moleculesLmsList = requete.getResultList();
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return moleculesLmsList;
    }

    @Override
    @Transactional
    public MoleculesLmsEntity findByName(final String nom) throws BddException {
        Query requete = getCurrentSession().createQuery("from MoleculesLmsEntity where nom=:nom");
        requete.setParameter("nom", nom);

        MoleculesLmsEntity moleculesLmsEntity = null;

        try {
            moleculesLmsEntity = (MoleculesLmsEntity) requete.getSingleResult();
        } catch (NoResultException e) {
            throw new BddException("Pas de molécule LMS trouvée pour le nom " + nom);
        }catch (NonUniqueResultException nonUniqueException) {
            LOGGER.debug("Pas de molecules Lms trouvée avec le nom : {}" + nom);
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        if(moleculesLmsEntity == null) {
            throw new BddException("Pas de molécule LMS trouvée pour le nom " + nom);
        }
        return moleculesLmsEntity;
    }

    @Override
    @Transactional
    public MoleculesLmsEntity findByNameContaining(final String nom) throws BddException {
        Query requete = getCurrentSession().createQuery("from MoleculesLmsEntity where nom like :nom");
        requete.setParameter("nom", "%"+nom+"%");

        MoleculesLmsEntity moleculesLmsEntity = null;

        try {
            moleculesLmsEntity = (MoleculesLmsEntity) requete.getSingleResult();
        } catch (NoResultException e) {
            throw new BddException("Pas de molécule LMS trouvée pour le nom " + nom);
        }catch (NonUniqueResultException nonUniqueException) {
            LOGGER.debug("Pas de molecules Lms trouvée avec le nom : {}" + nom);
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        if(moleculesLmsEntity == null) {
            throw new BddException("Pas de molécule LMS trouvée pour le nom " + nom);
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
