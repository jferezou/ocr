package com.perso.utils;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.Date;

@Getter
@Setter
public class AggregatePdf {

    private Date date;
    private Path path;
    private String ruche;
    private String matrice;
    private int rucher;

}
