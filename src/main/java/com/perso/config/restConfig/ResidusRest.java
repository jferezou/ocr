package com.perso.config.restConfig;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.perso.annotation.ServiceMethod;
import com.perso.exception.FichierInvalideException;
import com.perso.exception.ParsingException;
import com.perso.service.ReaderFileService;
import com.perso.service.UpdatedValuesService;
import com.perso.pojo.residus.ResidusDocument;
import com.perso.utils.response.ListPdfIdResponse;
import com.perso.utils.response.ListPdfResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import java.io.IOException;
import java.util.Set;

@Consumes(MediaType.APPLICATION_JSON)
@Service
@Path("/residus")
@Api(tags = "Résidus")
public class ResidusRest implements ApiExposeRest {

    @Value("${dossier.temporaire}")
    private String tempDir;
    @Resource
    ReaderFileService readerFileService;
    @Resource
    UpdatedValuesService updatedValuesService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderFileService.class);

    @Override
    @ApiResponses({@ApiResponse(code = 200, message = "", response = ListPdfResponse.class)})
    @ServiceMethod
    public Response extraire()  throws InvalidFormatException {
        this.updatedValuesService.cleanResidusMap();
        ListPdfResponse response = new ListPdfResponse();
        try {
            Set<ListPdfIdResponse> liste = this.readerFileService.readAndLaunchResidus();
            response.setResultats(liste);

        } catch (FichierInvalideException | TikaException | IOException e) {
            LOGGER.error("Erreur lors du traitement T2", e);
        }
//        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
//        responseBuilder.entity(response);
//        responseBuilder.status(200);
//        responseBuilder.header("Content-Type", "application/json;charset=utf-8");

        // Réponse du service
        return Response.ok(response).build();
    }

    @Override
    @ApiResponses({@ApiResponse(code = 200, message = "", response = String.class)})
    @ServiceMethod
    public Response sauvegarder(final String data)  throws InvalidFormatException {

//        int status = 200;
//        String body = "";
        try {
            this.updatedValuesService.parseAndSaveResidus(data);
        } catch (ParsingException e) {
            LOGGER.error("Erreur de parsing", e);
//            body = e.getMessage();
//            status = 500;
        }
//        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
//        responseBuilder.entity(body);
//        responseBuilder.status(status);
        // Réponse du service
        return Response.ok().build();
    }

    @Override
    @ApiResponses({@ApiResponse(code = 200, message = "", response = String.class)})
    @ServiceMethod
    public Response getCsvFile() {

        String csv = this.updatedValuesService.getCsvResidus();

//        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
//        String disposition = "attachment; fileName=resultat.csv";
//        responseBuilder.header("Content-Disposition", disposition);
//        responseBuilder.header("Content-Type", "text/csv;charset=windows-1252");
//        responseBuilder.entity(csv);
//        responseBuilder.status(200);

        // Réponse du service
        return Response.ok(csv).build();

    }

    @Override
    @ApiResponses({@ApiResponse(code = 200, message = "", response = ResidusDocument.class)})
    @ServiceMethod
    public Response getValue(@PathParam("id") final int key) {


        ResidusDocument resultat = this.updatedValuesService.getValeursResidus().get(key);
//        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
//        responseBuilder.entity(resultat);
//        responseBuilder.status(200);
//        responseBuilder.header("Content-Type", "application/json;charset=utf-8");

        // Réponse du service
        return Response.ok(resultat).build();

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
