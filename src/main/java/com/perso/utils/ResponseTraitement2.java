package com.perso.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResponseTraitement2 {
    private List<Traitement2Obj> gmsList;
    private List<Traitement2Obj> lmsList;
    private String pdfFilePath;
    private String pdfName;
    private String reference;
    private int id;
    private static int IDENTIFIANT = 0;

    public ResponseTraitement2() {
        this.gmsList = new ArrayList<>();
        this.lmsList = new ArrayList<>();
        IDENTIFIANT++;
        this.id = new Integer(IDENTIFIANT);
    }
}
