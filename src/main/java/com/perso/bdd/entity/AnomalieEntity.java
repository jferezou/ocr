package com.perso.bdd.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * Created by jferezou on 06/07/2017.
 */
@Data
@Entity
@Table(schema = "schema_name_opm", name = "t_mat_anomalie", uniqueConstraints = {@UniqueConstraint(columnNames={"ano_id_ptf_collecte"})})
@NamedQueries({
        @NamedQuery(
                name = "findAnomaliesByNumeroRameAndCodeStatuts",
                query = "SELECT Object(ano) FROM AnomalieEntity ano " +
                        "WHERE ano.numeroRame = :numeroRame AND ano.codeStatut IN (:codeStatuts)")
})
public class AnomalieEntity implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4541554703060244210L;

    public static final String PREFIXE_PB_ANOMALIE = "ANO-";

    // ======================== ATTRIBUTS DE LA TABLE ========================
    /**
     * Identifiant unique et automatique
     */
    @Id
    @Column(name = "ano_id")
    @SequenceGenerator(name = "seq_ano_id", sequenceName = "seq_ano_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ano_id")
    private Long id;


    /*
     * ID AnomalieEntity PTF Collecte
     */
    @Column(name = "ano_id_ptf_collecte", nullable = false)
    private Integer idPtfCollecte;


    /*
     * Numéro de la rame, on n'utilise pas l'ID technique car il faut pouvoir enregistrer l'ano même si la rame n'est pas référencée dans T_REF_RAME
     */
    @Column(name = "ano_numero_rame", nullable = false)
    private Integer numeroRame;


    /*
     * Libellé de l'anomalie
     */
    @Column(name = "ano_libelle", length = 200)
    private String libelle;


    /*
     * Libellé de l'état de l'anomalie dans son workflow
     */
    @Column(name = "ano_libelle_statut", nullable = false, length = 50)
    private String libelleStatut;


    /*
     * Code de l'état de l'anomalie dans son workflow
     */
    @Column(name = "ano_code_statut", nullable = false, length = 10)
    private String codeStatut;


    /*
     * Pour les signalements confort, le niveau (H:Haut, B:Bas)
     */
    @Column(name = "ano_niveau", nullable = true, length = 1)
    private String niveau;


    /*
     * libelle de la localisation sur lequel porte l'anomalie.
     */
    @Column(name = "ano_localisation", nullable = true, length = 200)
    private String localisation;


    /*
     * libelle de la précision N1 sur lequel porte l'anomalie.
     */
    @Column(name = "ano_precision_n1", nullable = true, length = 200)
    private String precisionN1;


    /*
     * libelle de la catégorie sur lequel porte l'anomalie.
     */
    @Column(name = "ano_categorie", nullable = true, length = 200)
    private String categorie;


    /*
     * libelle de la précision N2 sur lequel porte l'anomalie.
     */
    @Column(name = "ano_precision_n2", nullable = true, length = 200)
    private String precisionN2;


    /*
     * Pour les signalements confort, libelle de l’organe sur lequel porte le signalement.
     */
    @Column(name = "ano_organe", nullable = true, length = 200)
    private String organe;


    /*
     * libelle de la précision N3 sur lequel porte l'anomalie confort.
     */
    @Column(name = "ano_precision_n3", nullable = true, length = 200)
    private String precisionN3;


    /*
     * libelle de la sous organe sur lequel porte l'anomalie confort.
     */
    @Column(name = "ano_organe_enfant", nullable = true, length = 200)
    private String organeEnfant;


    /*
     * Code Composant de la fonction sur lequel porte l'anomalie confort.
     */
    @Column(name = "ano_code_composant", nullable = true, length = 10)
    private String codeComposant;


    /*
     * Libellé Composant de la fonction sur lequel porte l'anomalie confort.
     */
    @Column(name = "ano_libelle_composant", nullable = true, length = 250)
    private String libelleComposant;


    /*
     * Code panne de la fonction sur laquelle porte l'anomalie
     */
    @Column(name = "ano_code_panne", nullable = true, length = 10)
    private String codePanne;


    /*
     * Libellé panne de la fonction sur laquelle porte l'anomalie
     */
    @Column(name = "ano_libelle_panne", nullable = true, length = 250)
    private String libellePanne;


    /*
     * Position Tigre du Véhicule
     */
    @Column(name = "ano_position_tigre", nullable = true, length = 3)
    private String positionTigre;


    /*
     * libelle du rendez-vous de maintenance
     */
    @Column(name = "ano_libelle_rdv", nullable = true, length = 250)
    private String libelleRdv;


    /*
     * Timestamp de début prévisionnel du rendez-vous de maintenance
     */
    @Column(name = "ano_debut_rdv", nullable = true)
    private Date debutRdv;


    /*
     * Timestamp de fin prévisionnel du rendez-vous de maintenance
     */
    @Column(name = "ano_fin_rdv", nullable = true)
    private Date finRdv;


    /*
     * Creation le
     */
    @Column(name = "ano_cree_le", nullable = true)
    private Date creeLe;

    /**
     * Creation par
     */
    @Column(name = "ano_cree_par", nullable = true, length = 50)
    private String creePar;

    /*
     * Creation le
     */
    @Column(name = "ano_modifie_le", nullable = true)
    private Date modifieLe;

    /**
     * Creation par
     */
    @Column(name = "ano_modifie_par", nullable = true, length = 50)
    private String modifiePar;


    public String getIdentifiantProbleme() {
        return PREFIXE_PB_ANOMALIE + idPtfCollecte.toString();
    }

    public String getCommentaireProbleme() {

        StringBuilder sb = new StringBuilder();
        sb.append(positionTigre == null ? "Rame" : positionTigre);
        //
        if (localisation != null) {
            sb.append(" - ");
            sb.append(localisation);
            sb.append(precisionN1 == null ? "" : "/" + precisionN1);
        }
        //
        sb.append(libelle == null ? "" : " - " + libelle);
        sb.append(idPtfCollecte == null ? "" : " - Anomalie " + idPtfCollecte);

        return sb.toString();
    }


    @Override
    public String toString() {
        return "AnomalieEntity{" +
                "id=" + id +
                ", idPtfCollecte=" + idPtfCollecte +
                ", numeroRame=" + numeroRame +
                ", libelle='" + libelle + '\'' +
                ", libelleStatut='" + libelleStatut + '\'' +
                ", codeStatut=" + codeStatut +
                ", niveau='" + niveau + '\'' +
                ", localisation='" + localisation + '\'' +
                ", precisionN1='" + precisionN1 + '\'' +
                ", categorie='" + categorie + '\'' +
                ", precisionN2='" + precisionN2 + '\'' +
                ", organe='" + organe + '\'' +
                ", precisionN3='" + precisionN3 + '\'' +
                ", organeEnfant='" + organeEnfant + '\'' +
                ", codeComposant='" + codeComposant + '\'' +
                ", libelleComposant='" + libelleComposant + '\'' +
                ", codePanne='" + codePanne + '\'' +
                ", libellePanne='" + libellePanne + '\'' +
                ", positionTigre='" + positionTigre + '\'' +
                ", libelleRdv='" + libelleRdv + '\'' +
                ", debutRdv=" + debutRdv +
                ", finRdv=" + finRdv +
                ", creeLe=" + creeLe +
                ", creePar='" + creePar + '\'' +
                ", modifieLe=" + modifieLe +
                ", modifiePar='" + modifiePar + '\'' +
                '}';
    }
}
