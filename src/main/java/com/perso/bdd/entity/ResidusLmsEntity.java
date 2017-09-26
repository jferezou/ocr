package com.perso.bdd.entity;

import com.perso.bdd.entity.parametrage.MoleculesLmsEntity;
import com.perso.bdd.entity.parametrage.RuchierEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@ToString
@Table(schema = "schema_name_opm", name = "residus_lms")
public class ResidusLmsEntity implements Serializable {
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
    @SequenceGenerator(name = "seq_residus_lms_id", sequenceName = "seq_residus_lms_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_residus_lms_id")
    private Long id;

    @Column(name = "taux")
    private Double taux;

    @Column(name = "identifiant", nullable = false)
    private String identifiant;

    @ManyToOne
    @JoinColumn(name = "id_molecule_lms", referencedColumnName = "id", nullable = false)
    private MoleculesLmsEntity moleculeLms;

}
