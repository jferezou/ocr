package com.perso.bdd.entity.parametrage;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@ToString
@Table(schema = "schema_name_opm", name = "param_fleurs")
public class FleursEntity implements Serializable {
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
    @SequenceGenerator(name = "seq_param_fleurs_id", sequenceName = "seq_param_fleurs_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_fleurs_id")
    private Long id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "nom2")
    private String nom2;

    @Column(name = "identifiant", nullable = false)
    private String identifiant;


    @ManyToOne
    @JoinColumn(name = "espece_id", referencedColumnName = "id", nullable = false)
    private EspecesEntity espece;

}
