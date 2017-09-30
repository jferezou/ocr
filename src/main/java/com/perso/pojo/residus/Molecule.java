package com.perso.pojo.residus;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude={"trace","erreur","pourcentage"})
public class Molecule {

    private String value;
    private Double pourcentage;
    private boolean trace;
    private boolean erreur;

    public Molecule() {
        this.trace = false;
        this.erreur = false;
    }
}
