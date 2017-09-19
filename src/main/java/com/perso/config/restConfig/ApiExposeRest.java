package com.perso.config.restConfig;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.perso.annotation.ServiceMethod;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface ApiExposeRest {

    @GET
    @Path("/extraire")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceMethod
    Response extraire()  throws InvalidFormatException;



    @POST
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceMethod
    Response sauvegarder(final String data)  throws InvalidFormatException;



    @GET
    @Path("/export/csv")
    @Produces("text/csv")
    @ServiceMethod
    Response getCsvFile();



    @GET
    @Path("/get/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceMethod
    Response getValue(@PathParam("id") final int key);
}
