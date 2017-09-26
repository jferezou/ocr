package com.perso.bdd.entity;

import com.perso.bdd.entity.parametrage.MatriceEntity;
import com.perso.bdd.entity.parametrage.RuchierEntity;
import com.perso.pojo.palynologie.Palynologie;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@Data
@Entity
@ToString
@Table(schema = "public", name = "palynologie_document", uniqueConstraints = {@UniqueConstraint(columnNames={"identifiant"})})
public class PalynologieDocumentEntity implements Serializable {
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
    @SequenceGenerator(name = "seq_palynologie_document_id", sequenceName = "seq_palynologie_document_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_palynologie_document_id")
    private Long id;


    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "identifiant", nullable = false)
    private String identifiant;


    @Column(name = "identifiant_echantillon", nullable = false)
    private String identifiantEchantillon;


    @Column(name = "numero_echantillon", nullable = false)
    private Long numeroEchantillon;


    @Column(name = "pdf_name")
    private String pdfName;


    @ManyToOne
    @JoinColumn(name = "ruchier_id", referencedColumnName = "id", nullable = false)
    private RuchierEntity ruchier;


    @ManyToOne
    @JoinColumn(name = "matrice_id", referencedColumnName = "id", nullable = false)
    private MatriceEntity matrice;


    @ManyToOne
    @JoinColumn(name = "ruche_id", referencedColumnName = "id", nullable = false)
    private RuchesEntity ruche;

    @OneToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL,mappedBy="palynologieDocument")
    private List<PalynologieEntity> palynologieList;
}
