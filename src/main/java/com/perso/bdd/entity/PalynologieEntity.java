package com.perso.bdd.entity;

import com.perso.bdd.entity.parametrage.FleursEntity;
import com.perso.bdd.entity.parametrage.TypeEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@ToString
@Table(schema = "public", name = "palynologie")
public class PalynologieEntity implements Serializable {
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
    @SequenceGenerator(name = "seq_palynologie_id", sequenceName = "seq_palynologie_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_palynologie_id")
    private Long id;


    @Column(name = "pourcentage", nullable = false)
    private Double pourcentage;


    @ManyToOne
    @JoinColumn(name = "id_fleur", referencedColumnName = "id", nullable = false)
    private FleursEntity fleur;

    @ManyToOne
    @JoinColumn(name = "id_type", referencedColumnName = "id", nullable = false)
    private TypeEntity type;


    @ManyToOne
    @JoinColumn(name = "palynologie_document_id", referencedColumnName = "id", nullable = false)
    private PalynologieDocumentEntity palynologieDocument;

}
