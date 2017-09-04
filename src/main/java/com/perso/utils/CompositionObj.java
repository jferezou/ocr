package com.perso.utils;

import lombok.Data;

@Data
public class CompositionObj {


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
            this.type = "Isolé";
        }


    }

}
