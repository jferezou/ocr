package com.perso.bdd.entity.parametrage;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@ToString
@Entity
@Table(schema = "schema_name_opm", name = "param_molecules_gms", uniqueConstraints = {@UniqueConstraint(columnNames={"nom"})})
public class MoleculesGmsEntity implements Serializable {
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
    @SequenceGenerator(name = "seq_param_molecules_gms_id", sequenceName = "seq_param_molecules_gms_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_molecules_gms_id")
    private Long id;


    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "valeurTrace", nullable = false)
    private Double valeurTrace;

}
