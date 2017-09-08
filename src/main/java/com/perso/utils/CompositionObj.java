package com.perso.utils;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CompositionObj implements Serializable {

    /** Numéro de sérialisation */
    private static final long serialVersionUID = -1;


    private boolean isValid;

    private String value;

    private Double percentage = new Double(0);

    private String type;

    public void calculateType() {
        if(this.getPercentage() > 45) {
            this.type = "Dominant";
        }
        else if (this.percentage > 15) {
            this.type = "Accompagnement";
        }
        else {
            this.type = "Isole";
        }


    }

}
