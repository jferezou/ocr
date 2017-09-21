package com.perso.utils;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.Date;

@Getter
@Setter
public class AggregatePdf implements Comparable<AggregatePdf> {

    private Date date;
    private Path path;

    @Override
    public int compareTo(AggregatePdf o) {
        return this.getDate().compareTo(o.getDate());
    }
}
