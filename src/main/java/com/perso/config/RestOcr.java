package com.perso.config;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.perso.annotation.ServiceMethod;
import com.perso.exception.FichierInvalideException;
import com.perso.service.*;
import com.perso.utils.*;
import org.apache.cxf.jaxrs.impl.ResponseBuilderImpl;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.PDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.List;

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
    PdfService pdfService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderFileService.class);

    @GET
    @Path("/t1")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceMethod
    public Response lancerTraitement()  throws InvalidFormatException {
        this.updatedValuesService.cleanT1Map();
        ResponseTraitement response = new ResponseTraitement();
        try {
            List<ResultatPdf> results = this.readerFileService.readAndLaunch();
            response.setResultats(results);
        } catch (FichierInvalideException | TikaException | IOException e) {
            LOGGER.error("", e);
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
        ResponseT2List response = new ResponseT2List();
        try {
            List<ResponseTraitement2> liste = this.readerFileService.readAndLaunchT2();
            response.setResultats(liste);

        } catch (FichierInvalideException | TikaException | IOException e) {
            LOGGER.error("", e);
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

        this.updatedValuesService.parseAndSave(data);
        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        responseBuilder.entity("");
        responseBuilder.status(200);
        // Réponse du service
        return responseBuilder.build();
    }

    @POST
    @Path("/savet2")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceMethod
    public Response sauvegardert2(final String data)  throws InvalidFormatException {

        this.updatedValuesService.parseAndSavet2(data);
        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        responseBuilder.entity("");
        responseBuilder.status(200);
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

        EstimateTime estimateTime = this.pdfService.estimateTime(multiplicateur, isT1);

        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        responseBuilder.entity(estimateTime);
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
