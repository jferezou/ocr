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
            LOGGER.error("Erreur lors du traitement résidus", e);
        }
        return Response.ok(response).build();
    }

    @Override
    @ApiResponses({@ApiResponse(code = 200, message = "", response = String.class)})
    @ServiceMethod
    public Response sauvegarder(final String data)  throws InvalidFormatException {

        try {
            this.updatedValuesService.parseAndSaveResidus(data);
        } catch (ParsingException e) {
            LOGGER.error("Erreur de parsing", e);
        }
        return Response.ok().build();
    }

    @Override
    @ApiResponses({@ApiResponse(code = 200, message = "", response = String.class)})
    @ServiceMethod
    public Response getCsvFile() {

        String csv = this.updatedValuesService.getCsvResidus();

        String disposition = "attachment; fileName=extractionResidus.csv";
        Response response = Response.ok(csv).build();
        response.getHeaders().add("Content-Disposition", disposition);

        // Réponse du service
        return response;

    }

    @GET
    @ApiResponses({@ApiResponse(code = 200, message = "", response = File.class)})
    @ServiceMethod
    @Path("/pdf/aggregate")
    @Produces("application/pdf")
    public Response splitPdf() {
        File file = null;
        try {
            file = this.readerFileService.readAndLaunchAggregatePdf();
        } catch (FichierInvalideException |TikaException | IOException e) {
            LOGGER.error("erreur" ,e);
        }

        String disposition = "attachment; fileName=resultatPdf.pdf";
        Response response = Response.ok(file).build();
        response.getHeaders().add("Content-Disposition", disposition);

        // Réponse du service
        return response;

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses({@ApiResponse(code = 200, message = "", response = ResidusDocument.class)})
    @ServiceMethod
    @Path("/get/{id}")
    public Response getValue(@PathParam("id") final int key) {


        ResidusDocument resultat = this.updatedValuesService.getValeursResidus().get(key);

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
