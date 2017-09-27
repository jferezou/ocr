package com.perso.bdd.entity;

import com.perso.bdd.entity.parametrage.MoleculesGmsEntity;
import com.perso.bdd.entity.parametrage.MoleculesLmsEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@ToString
@Table(schema = "public", name = "residus_gms")
public class ResidusGmsEntity implements Serializable {
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
    @SequenceGenerator(name = "seq_residus_gms_id", sequenceName = "seq_residus_gms_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_residus_gms_id")
    private Long id;

    @Column(name = "taux")
    private Double taux;

    @Column(name = "trace", nullable = false)
    private Boolean trace;

    @ManyToOne
    @JoinColumn(name = "id_molecule_gms", referencedColumnName = "id", nullable = false)
    private MoleculesGmsEntity moleculeGms;

    @ManyToOne
    @JoinColumn(name = "residus_document_id", referencedColumnName = "id", nullable = false)
    private ResidusDocumentEntity residusDocument;

}
