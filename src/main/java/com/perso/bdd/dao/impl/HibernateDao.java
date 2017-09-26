package com.perso.bdd.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Classe de base pour un dao Hibernate
 *
 * @author Romain GERVAIS
 *
 */
public class HibernateDao {

    /**
     * Référence sur l'objet logger de la classe
     */
    protected final Logger logger = LogManager.getLogger(getClass());

    /**
     * JDBC batch size
     */
    private static final int JDBC_BATCH_SIZE = 20;

    /**
     * Factory de création des session hibernate
     */
    @Resource
    private SessionFactory sessionFactory;

    /**
     * Retourne la session courante
     *
     * @return la session courante
     */
    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Créer une HQL query à partir de la session courante. À utiliser lorsque le DAO ne fait appel qu'à une seule requête. Sinon récupérer d'abord la
     * session courante puis créer les query à partir de cette session.
     *
     * @param queryString
     *            la requête HQL
     * @return un builder pour construire/exécuter la query
     */
    protected Query hqlQuery(final String queryString) {
        return getCurrentSession().createQuery(queryString);
    }

    /**
     * Créer une SQL query à partir de la session courante. À utiliser lorsque le DAO ne fait appel qu'à une seule requête. Sinon récupérer d'abord la
     * session courante puis créer les query à partir de cette session.
     *
     * @param queryString
     *            la requête HQL
     * @return un builder pour construire/exécuter la query
     */
    protected SQLQuery sqlQuery(final String queryString) {
        return getCurrentSession().createSQLQuery(queryString);
    }

    /**
     * Flush la session en fonction d'un index dans une boucle. Incrémente l'index de 1
     *
     * @param session
     *            la session à flusher
     * @param index
     *            l'index de la boucle
     * @return le nouvel index
     */
    protected int flushAndClearAndIncrement(final Session session, int index) {
        index++;
        if (index % JDBC_BATCH_SIZE == 0) {
            flushAndClear(session);
        }
        return index;
    }

    /**
     * Flush et clear la session courante
     *
     * @param session
     *            session à flusher
     */
    protected void flushAndClear(final Session session) {
        session.flush();
        session.clear();
    }

    /**
     * Flush et clear la session courante
     */
    public void flushAndClearCurrentSession() {
        Session currentSession = getCurrentSession();
        currentSession.flush();
        currentSession.clear();
    }

    /**
     * Supprimer une liste
     *
     * @param ids
     *            liste d'id technique des entités à supprimer
     * @param clazz
     *            Classe des entités à supprimer. Cette classe doit avoir une propriété "id" qui correspond à la clé primaire.
     */
    protected void delete(final Set<?> ids, final Class<?> clazz) {
        if (CollectionUtils.isNotEmpty(ids)) {
            String queryString = "DELETE FROM " + clazz.getSimpleName() + " WHERE id IN (:ids)";
            Query query = getCurrentSession().createQuery(queryString);
            query.setParameterList("ids", ids);
            query.executeUpdate();
        }
    }

    public <T> T avecProfile(final String profile, final Callable<T> work) throws Exception {
        Session currentSession = getCurrentSession();
        currentSession.enableFetchProfile(profile);
        try {
            return work.call();
        } finally {
            currentSession.disableFetchProfile(profile);
        }

    }
}
