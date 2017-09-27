package com.perso.bdd.entity;

import com.perso.bdd.entity.parametrage.RuchierEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@ToString
@Table(schema = "public", name = "ruches", uniqueConstraints = {@UniqueConstraint(columnNames={"nom"})})
public class RuchesEntity implements Serializable {
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
    @SequenceGenerator(name = "seq_ruches_id", sequenceName = "seq_ruches_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ruches_id")
    private Long id;


    @Column(name = "nom", nullable = false)
    private String nom;

}
