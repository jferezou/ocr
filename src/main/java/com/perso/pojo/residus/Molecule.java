package com.perso.pojo.residus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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
