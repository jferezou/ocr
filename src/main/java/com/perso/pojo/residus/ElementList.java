package com.perso.pojo.residus;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class ElementList {

    protected Map<String, Double> values;

    public ElementList(){
        this.values=new HashMap<>();
        this.fillMap();
    }

    protected abstract void fillMap();

}
