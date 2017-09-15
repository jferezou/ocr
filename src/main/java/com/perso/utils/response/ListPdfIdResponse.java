package com.perso.utils.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListPdfIdResponse {
    private int id;
    private String pdfFilePath;
    private boolean valider=false;
}
