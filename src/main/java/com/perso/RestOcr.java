package com.perso;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.perso.annotation.ServiceMethod;
import com.perso.exception.FichierInvalideException;
import com.perso.service.ReaderFileService;
import com.perso.service.TransformServiceImpl;
import com.perso.utils.ResultatPdf;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Service
@Path("/traitement")
public class RestOcr {

    @Autowired
    ReaderFileService readerFileService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderFileService.class);

    @GET
    @Path("/t1")
    @ServiceMethod
    public Response transmettreEtatRame(@QueryParam("pdfName") final String pdfName)  throws InvalidFormatException {

        List<ResultatPdf> results = null;
        try {
            results = this.readerFileService.readAndLaunch();
        } catch (FichierInvalideException | TikaException | IOException e) {
            LOGGER.error("", e);
        }
        // Réponse du service
        return ok(results);
    }

    /**
     * Retourne des données
     *
     * @param object
     * @return
     */
    private Response ok(final Object object) {
        return Response.ok(object).build();
    }

    /**
     * Retourne des données
     *
     * @return
     */
    private Response ok() {
        return Response.ok().build();
    }

}
