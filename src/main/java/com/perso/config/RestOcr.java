package com.perso.config;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.perso.annotation.ServiceMethod;
import com.perso.exception.FichierInvalideException;
import com.perso.exception.ParsingException;
import com.perso.service.*;
import com.perso.utils.*;
import com.perso.utils.response.ListPdfIdResponse;
import com.perso.utils.response.ListResponse;
import org.apache.cxf.jaxrs.impl.ResponseBuilderImpl;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.Set;

@Consumes(MediaType.APPLICATION_JSON)
@Service
@Path("/traitement")
public class RestOcr {

    @Value("${dossier.temporaire}")
    private String tempDir;
    @Resource
    ReaderFileService readerFileService;
    @Resource
    UpdatedValuesService updatedValuesService;
    @Resource
    EstimateTimeService estimateTimeService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderFileService.class);

    @GET
    @Path("/t1")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceMethod
    public Response lancerTraitement()  throws InvalidFormatException {
        this.updatedValuesService.cleanT1Map();
        ListResponse response = new ListResponse();
        try {
            Set<ListPdfIdResponse> results = this.readerFileService.readAndLaunch();
            response.setResultats(results);
        } catch (FichierInvalideException | TikaException | IOException e) {
            LOGGER.error("Erreur lors du traitement T1", e);
        }
        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        responseBuilder.entity(response);
        responseBuilder.status(200);
        responseBuilder.header("Content-Type", "application/json;charset=utf-8");

        // Réponse du service
        return responseBuilder.build();
    }


    @GET
    @Path("/t2")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceMethod
    public Response lancerTraitement2()  throws InvalidFormatException {
        this.updatedValuesService.cleanT2Map();
        ListResponse response = new ListResponse();
        try {
            Set<ListPdfIdResponse> liste = this.readerFileService.readAndLaunchT2();
            response.setResultats(liste);

        } catch (FichierInvalideException | TikaException | IOException e) {
            LOGGER.error("Erreur lors du traitement T2", e);
        }
        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        responseBuilder.entity(response);
        responseBuilder.status(200);
        responseBuilder.header("Content-Type", "application/json;charset=utf-8");

        // Réponse du service
        return responseBuilder.build();
    }

    @GET
    @Path("/getpdf")
    @Produces("application/pdf")
    @ServiceMethod
    public Response getFile(@QueryParam("pdfName") final String pdfName) {

        File file = new File(tempDir+"\\"+pdfName);

        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        responseBuilder.entity(file);
        responseBuilder.status(200);

        // Réponse du service
        return responseBuilder.build();

    }

    @POST
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceMethod
    public Response sauvegarder(final String data)  throws InvalidFormatException {

        int status = 200;
        String body = "";
        try {
            this.updatedValuesService.parseAndSave(data);
        } catch (ParsingException e) {
            LOGGER.error("Erreur de parsing", e);
            body = e.getMessage();
            status = 500;
        }
        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        responseBuilder.entity(body);
        responseBuilder.status(status);
        // Réponse du service
        return responseBuilder.build();
    }

    @POST
    @Path("/savet2")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceMethod
    public Response sauvegardert2(final String data)  throws InvalidFormatException {

        int status = 200;
        String body = "";
        try {
            this.updatedValuesService.parseAndSavet2(data);
        } catch (ParsingException e) {
            LOGGER.error("Erreur de parsing", e);
            body = e.getMessage();
            status = 500;
        }
        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        responseBuilder.entity(body);
        responseBuilder.status(status);
        // Réponse du service
        return responseBuilder.build();
    }


    @GET
    @Path("/getcsv")
    @Produces("text/csv")
    @ServiceMethod
    public Response getCsvFile() {

        String csv = this.updatedValuesService.getCsv();

        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        String disposition = "attachment; fileName=resultat.csv";
        responseBuilder.header("Content-Disposition", disposition);
        responseBuilder.header("Content-Type", "text/csv;charset=windows-1252");
        responseBuilder.entity(csv);
        responseBuilder.status(200);

        // Réponse du service
        return responseBuilder.build();

    }


    @GET
    @Path("/getcsvt2")
    @Produces("text/csv")
    @ServiceMethod
    public Response getCsvFileT2() {

        String csv = this.updatedValuesService.getCsvt2();

        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        String disposition = "attachment; fileName=resultat.csv";
        responseBuilder.header("Content-Disposition", disposition);
        responseBuilder.header("Content-Type", "text/csv;charset=windows-1252");
        responseBuilder.entity(csv);
        responseBuilder.status(200);

        // Réponse du service
        return responseBuilder.build();

    }


    @GET
    @Path("/estimatetime")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceMethod
    public Response estimateTime(@QueryParam("multiplicateur") final String multiplicateur, @QueryParam("ist1") final Boolean isT1) {

        EstimateTime estimateTime = this.estimateTimeService.estimateTime(multiplicateur, isT1);

        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        responseBuilder.entity(estimateTime);
        responseBuilder.status(200);
        responseBuilder.header("Content-Type", "application/json;charset=utf-8");

        // Réponse du service
        return responseBuilder.build();

    }



    @GET
    @Path("/gett2")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceMethod
    public Response getT2Value(@QueryParam("id") final Integer key) {


        ResponseTraitement2 resultat = this.updatedValuesService.getValeursEnregistreest2().get(key);
        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        responseBuilder.entity(resultat);
        responseBuilder.status(200);
        responseBuilder.header("Content-Type", "application/json;charset=utf-8");

        // Réponse du service
        return responseBuilder.build();

    }

    @GET
    @Path("/gett1")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceMethod
    public Response getT1Value(@QueryParam("id") final Integer key) {

        ResultatPdf resultat = this.updatedValuesService.getValeursEnregistrees().get(key);
        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        responseBuilder.entity(resultat);
        responseBuilder.status(200);
        responseBuilder.header("Content-Type", "application/json;charset=utf-8");

        // Réponse du service
        return responseBuilder.build();

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
