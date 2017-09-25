package com.perso.pojo.residus;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResidusDocument {
    private List<Molecule> moleculesGms;
    private List<Molecule> moleculesLms;
    private String pdfFilePath;
    private String pdfName;
    private String reference;
    private String certificatAnalyses;
    private double poids;
    private int id;
    private static int IDENTIFIANT = 0;

    public ResidusDocument() {
        this.moleculesGms = new ArrayList<>();
        this.moleculesLms = new ArrayList<>();
        IDENTIFIANT++;
        this.id = new Integer(IDENTIFIANT);
    }

    private List<DataListItem> lmsDataList;
    private List<DataListItem> gmsDataList;
}
