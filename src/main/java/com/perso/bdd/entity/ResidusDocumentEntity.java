package com.perso.bdd.entity;

import com.perso.bdd.entity.parametrage.MatriceEntity;
import com.perso.bdd.entity.parametrage.ContactEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Entity
@ToString
@Table(schema = "public", name = "residus_document", uniqueConstraints = {@UniqueConstraint(columnNames={"identifiant"})})
public class ResidusDocumentEntity implements Serializable {
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
    @SequenceGenerator(name = "seq_residus_document_id", sequenceName = "seq_residus_document_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_residus_document_id")
    private Long id;


    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "identifiant", nullable = false)
    private String identifiant;

    @Column(name = "poids", nullable = false)
    private Integer poids;


    @Column(name = "certificat_analyse", nullable = false)
    private String certificatAnalyse;


    @Column(name = "pdf_name")
    private String pdfName;


    @ManyToOne
    @JoinColumn(name = "contact_id", referencedColumnName = "id", nullable = false)
    private ContactEntity contact;


    @ManyToOne
    @JoinColumn(name = "matrice_id", referencedColumnName = "id", nullable = false)
    private MatriceEntity matrice;


    @ManyToOne
    @JoinColumn(name = "ruche_id", referencedColumnName = "id", nullable = false)
    private RuchesEntity ruche;


    @OneToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL,mappedBy="residusDocument")
    private List<ResidusGmsEntity> residusGmsList;


    @OneToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL,mappedBy="residusDocument")
    private List<ResidusLmsEntity> residusLmsList;

}
