package com.perso.bdd.entity.parametrage;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@ToString
@Table(schema = "schema_name_opm", name = "param_famille", uniqueConstraints = {@UniqueConstraint(columnNames={"nom"})})
public class FamilleEntity implements Serializable {
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
    @SequenceGenerator(name = "seq_param_famille_id", sequenceName = "seq_param_famille_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_famille_id")
    private Long id;


    @Column(name = "nom", nullable = false)
    private String nom;



}
