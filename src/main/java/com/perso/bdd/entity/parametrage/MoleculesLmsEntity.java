package com.perso.bdd.entity.parametrage;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@ToString
@Entity
@Table(schema = "public", name = "param_molecules_lms", uniqueConstraints = {@UniqueConstraint(columnNames={"nom"})})
public class MoleculesLmsEntity extends MoleculeEntity implements Serializable {
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
    @SequenceGenerator(name = "seq_param_molecules_lms_id", sequenceName = "seq_param_molecules_lms_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_molecules_lms_id")
    private Long id;

    @Column(name = "nom", nullable = false)
    protected String nom;

    @Column(name = "valeurTrace", nullable = false)
    protected Double valeurTrace;


    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    private TypeResidusEntity type;
}
