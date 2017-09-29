package com.perso.bdd.entity.parametrage;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@ToString
@Table(schema = "public", name = "param_espece", uniqueConstraints = {@UniqueConstraint(columnNames={"nom"})})
public class EspeceEntity implements Serializable {
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
    @SequenceGenerator(name = "seq_param_espece_id", sequenceName = "seq_param_espece_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_espece_id")
    private Long id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "nom2")
    private String nom2;

    @ManyToOne
    @JoinColumn(name = "genre_id", referencedColumnName = "id")
    private GenreEntity genre;

}
