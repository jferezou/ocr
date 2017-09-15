package com.perso.utils.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ListResponse {
    Set<ListPdfIdResponse> resultats;
}
