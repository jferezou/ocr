package com.perso.bdd.entity.parametrage;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@ToString
@Entity
@Table(schema = "public", name = "param_molecules_gms", uniqueConstraints = {@UniqueConstraint(columnNames={"nom"})})
public class MoleculesGmsEntity  extends MoleculeEntity implements Serializable {
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
    protected String nom;

    @Column(name = "valeurTrace", nullable = false)
    protected Double valeurTrace;


    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    private TypeResidusEntity type;

}
