package com.perso.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Traitement2Obj {

    private String value;
    private double pourcentage;
    private boolean trace;

    public Traitement2Obj() {
        this.trace = false;
    }
}
