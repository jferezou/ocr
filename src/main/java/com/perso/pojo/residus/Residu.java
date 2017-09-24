package com.perso.pojo.residus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Residu {

    private String value;
    private Double pourcentage;
    private boolean trace;
    private boolean erreur;

    public Residu() {
        this.trace = false;
        this.erreur = false;
    }
}
