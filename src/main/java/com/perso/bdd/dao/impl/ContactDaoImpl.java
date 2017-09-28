package com.perso.bdd.dao.impl;

import com.perso.bdd.dao.ParamContactDao;
import com.perso.bdd.entity.parametrage.ContactEntity;
import org.apache.log4j.Logger;
import org.hibernate.NonUniqueResultException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

@Repository
public class ContactDaoImpl extends HibernateDao implements ParamContactDao {

    final static Logger LOGGER = Logger.getLogger(ContactDaoImpl.class);

    @Override
    @Transactional
    public ContactEntity findByCorrespondance(int idCorrespondance) throws NoResultException {
        Query requete = getCurrentSession().createQuery("from RuchierEntity where correspondance=:idCorrespondance");
        requete.setParameter("idCorrespondance", idCorrespondance);

        ContactEntity contactEntity = null;

        try {
            contactEntity = (ContactEntity) requete.getSingleResult();
        } catch (NonUniqueResultException nonUniqueException) {
            LOGGER.debug("Pas de contact trouvée avec l'identifiant : {}" + idCorrespondance);
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return contactEntity;
    }

    /**
     * {@inheritDoc}
     * @param contactEntity
     */
    @Override
    @Transactional
    public void deleteContact(final ContactEntity contactEntity) {
        this.getCurrentSession().delete(contactEntity);
    }

    /**
     * {@inheritDoc}
     * @param contactEntity
     */
    @Override
    @Transactional
    public void updateContact(final ContactEntity contactEntity) {
        this.getCurrentSession().merge(contactEntity);
    }

    /**
     * {@inheritDoc}
     * @param contactEntity
     */
    @Override
    @Transactional
    public void createContact(final ContactEntity contactEntity) {
        this.getCurrentSession().save(contactEntity);
    }

}
