package com.perso.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Traitement2Obj {

    private String value;
    private Double pourcentage;
    private boolean trace;

    public Traitement2Obj() {
        this.trace = false;
    }
}
