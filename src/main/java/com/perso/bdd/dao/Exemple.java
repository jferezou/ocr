package com.perso.bdd.dao;

import com.perso.bdd.entity.AnomalieEntity;
import org.apache.log4j.Logger;
import org.hibernate.NonUniqueResultException;
import org.hibernate.query.Query;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;


//@Repository
public class Exemple  extends HibernateDao {

    public Exemple(){super();}

    final static Logger LOGGER = Logger.getLogger(Exemple.class);

    /**
     * {@inheritDoc}
     * @param idPtfCollecte
     * @return
     */
    public AnomalieEntity getAnomalieByIdPtfCollecte(final Integer idPtfCollecte) {
        Query requete = getCurrentSession().createQuery("from AnomalieEntity where idPtfCollecte=:idPtfCollecte");
        requete.setParameter("idPtfCollecte", idPtfCollecte);

        AnomalieEntity anomalieEntity = null;

        try {
            anomalieEntity = (AnomalieEntity) requete.getSingleResult();
        } catch (NonUniqueResultException nonUniqueException) {
            LOGGER.debug("Pas d'anomalie trouvée avec le numéro de PTF Collecte : {}" + idPtfCollecte);
        } catch (NoResultException noResultException) {
            LOGGER.debug("Pas d'anomalie trouvée avec le numéro de PTF Collecte : {}" + idPtfCollecte);
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return anomalieEntity;
    }

    /**
     * {@inheritDoc}
     */
    public AnomalieEntity getAnomalieById(final Long idAnomalie) {
        Query requete = getCurrentSession().createQuery("from AnomalieEntity where id=:idAnomalie");
        requete.setParameter("idAnomalie", idAnomalie);

        AnomalieEntity anomalieEntity = null;

        try {
            anomalieEntity = (AnomalieEntity) requete.getSingleResult();
        }
        catch (NonUniqueResultException nonUniqueException) {
            LOGGER.debug("Pas d'anomalie trouvée avec le numéro de PTF Collecte : " );
        }catch (NoResultException noResultException) {
            LOGGER.debug("Pas d'anomalie trouvée avec l'id : " + idAnomalie);
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return anomalieEntity;
    }

    /**
     * {@inheritDoc}
     * @param id
     */
    @Transactional
    public void deleteAnomalie(final Integer id) {
        AnomalieEntity ano = this.getAnomalieByIdPtfCollecte(id);
        this.getCurrentSession().delete(ano);
    }

    /**
     * {@inheritDoc}
     * @param anomalieEntity
     */
    @Transactional
    public void updateAnomalie(final AnomalieEntity anomalieEntity) {
        this.getCurrentSession().merge(anomalieEntity);
    }

    /**
     * {@inheritDoc}
     * @param anomalieEntity
     */
    @Transactional
    public void createAnomalie(final AnomalieEntity anomalieEntity) {
        this.getCurrentSession().save(anomalieEntity);
    }

    /**
     * {@inheritDoc}
     * @param anomalieEntity
     */
    public void cloreAnomalie(final AnomalieEntity anomalieEntity) {
        this.updateAnomalie(anomalieEntity);
    }

    /**
     * {@inheritDoc}
     * @param numeroRame
     * @return
     */
    public List<AnomalieEntity> getAnomaliesByNumeroRame(Integer numeroRame) {
        Query requete = getCurrentSession().createQuery("from AnomalieEntity where numeroRame=:numeroRame and codeStatut not in (:codeStatutList)");
        requete.setParameter("numeroRame", numeroRame);
        List<String> codeAnoList = new ArrayList<>();
        requete.setParameter("codeStatutList",codeAnoList);

        List<AnomalieEntity> anomaliesEntity = null;

        try {
            anomaliesEntity = requete.getResultList();
        } catch (NoResultException noResultException) {
            LOGGER.debug("Pas d'anomalie trouvée avec le numéro de rame : " + numeroRame);
        } catch (Exception e) {
            LOGGER.error("Erreur : " + e);
        }

        return anomaliesEntity;
    }
}