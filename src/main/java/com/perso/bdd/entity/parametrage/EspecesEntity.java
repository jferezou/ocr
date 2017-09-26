package com.perso.bdd.entity.parametrage;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@ToString
@Table(schema = "schema_name_opm", name = "param_especes", uniqueConstraints = {@UniqueConstraint(columnNames={"nom"})})
public class EspecesEntity implements Serializable {
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
    @SequenceGenerator(name = "seq_param_especes_id", sequenceName = "seq_param_especes_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_especes_id")
    private Long id;


    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "identifiant", nullable = false)
    private String identifiant;


    @ManyToOne
    @JoinColumn(name = "famille_id", referencedColumnName = "id", nullable = false)
    private FamilleEntity famille;

}
