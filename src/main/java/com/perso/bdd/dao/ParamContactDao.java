package com.perso.bdd.dao;

import com.perso.bdd.entity.parametrage.ContactEntity;

import javax.persistence.NoResultException;

public interface ParamContactDao {
    ContactEntity findByCorrespondance(final int idCorrespondance) throws NoResultException;
    void deleteContact(final ContactEntity contactEntity);
    void updateContact(final ContactEntity contactEntity);
    void createContact(final ContactEntity contactEntity);
}
