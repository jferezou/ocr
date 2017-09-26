package com.perso.bdd.entity.parametrage;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@ToString
@Entity
@Table(schema = "schema_name_opm", name = "param_ruchier", uniqueConstraints = {@UniqueConstraint(columnNames={"correspondance"})})
public class RuchierEntity implements Serializable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1L;
    // ======================== ATTRIBUTS DE LA TABLE ========================
    /**
     * Identifiant unique et automatique
     */
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "seq_param_ruchier_id", sequenceName = "seq_param_ruchier_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_ruchier_id")
    private Long id;


    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Column(name = "site")
    private String site;

    @Column(name = "departement")
    private Integer departement;


    @Column(name = "region")
    private String region;


    @Column(name = "telephone")
    private String telephone;

    @Column(name = "correspondance", nullable = false)
    private Integer correspondance;

}
