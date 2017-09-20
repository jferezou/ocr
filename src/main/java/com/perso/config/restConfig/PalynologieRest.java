package com.perso.config.restConfig;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.perso.exception.FichierInvalideException;
import com.perso.exception.ParsingException;
import com.perso.service.*;
import com.perso.pojo.palynologie.PalynologieDocument;
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
@Path("/palynologie")
@Api(tags = "Palynologie")
public class PalynologieRest implements ApiExposeRest{

    @Value("${dossier.temporaire}")
    private String tempDir;
    @Resource
    ReaderFileService readerFileService;
    @Resource
    UpdatedValuesService updatedValuesService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderFileService.class);

    @Override
    @ApiResponses({@ApiResponse(code = 200, message = "", response = ListPdfResponse.class)})
    public Response extraire()  throws InvalidFormatException {
        this.updatedValuesService.cleanPalynologieMap();
        ListPdfResponse response = new ListPdfResponse();
        try {
            Set<ListPdfIdResponse> results = this.readerFileService.readAndLaunchPalynologie();
            response.setResultats(results);
        } catch (FichierInvalideException | TikaException | IOException e) {
            LOGGER.error("Erreur lors du traitement T1", e);
        }
//        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
//        responseBuilder.entity(response);
//        responseBuilder.status(200);
//        responseBuilder.header("Content-Type", "application/json;charset=utf-8");
//
//        // Réponse du service
//        return responseBuilder.build();

        return Response.ok(response).build();
    }

    @Override
    @ApiResponses({@ApiResponse(code = 200, message = "", response = String.class)})
    public Response sauvegarder(final String data)  throws InvalidFormatException {

//        int status = 200;
//        String body = "";
        try {
            this.updatedValuesService.parseAndSavePalynologie(data);
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
    public Response getCsvFile() {

        String csv = this.updatedValuesService.getCsvPalynologie();

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
    @ApiResponses({@ApiResponse(code = 200, message = "", response = PalynologieDocument.class)})
    public Response getValue(@PathParam("id") final int key) {

        PalynologieDocument resultat = this.updatedValuesService.getValeursPalynologie().get(key);
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
