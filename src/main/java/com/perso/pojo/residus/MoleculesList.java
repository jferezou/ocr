package com.perso.pojo.residus;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public abstract class MoleculesList {

    protected Map<String, Double> values;

    public MoleculesList(){
        this.values=new HashMap<>();
        this.fillMap();
    }

    protected abstract void fillMap();

    public List<DataListItem> toList() {
        List<DataListItem> newList = new ArrayList<>();
        for(String key : values.keySet()) {
            DataListItem dt = new DataListItem();
            dt.setTitle(key);
            dt.setValue(values.get(key));
            newList.add(dt);
        }

        return newList;
    }

}
