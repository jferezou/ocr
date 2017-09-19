package com.perso.pojo.residus;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResidusDocument {
    private List<Residu> gmsList;
    private List<Residu> lmsList;
    private String pdfFilePath;
    private String pdfName;
    private String reference;
    private int id;
    private static int IDENTIFIANT = 0;

    public ResidusDocument() {
        this.gmsList = new ArrayList<>();
        this.lmsList = new ArrayList<>();
        IDENTIFIANT++;
        this.id = new Integer(IDENTIFIANT);
    }
}
