package com.perso.config.restConfig;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.perso.annotation.ServiceMethod;
import com.perso.exception.ParsingException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface ApiExposeRest {

    @GET
    @Path("/extraire")
    @Produces(MediaType.APPLICATION_JSON)
    Response extraire()  throws InvalidFormatException;



    @POST
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    Response sauvegarder(final String data)  throws InvalidFormatException, ParsingException;



    @GET
    @Path("/export/csv")
    @Produces("text/csv; charset=windows-1252")
    Response getCsvFile();

}
