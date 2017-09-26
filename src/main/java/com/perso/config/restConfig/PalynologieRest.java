package com.perso.config.restConfig;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.perso.annotation.ServiceMethod;
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
    @ServiceMethod
    public Response extraire()  throws InvalidFormatException {
        this.updatedValuesService.cleanPalynologieMap();
        ListPdfResponse response = new ListPdfResponse();
        try {
            Set<ListPdfIdResponse> results = this.readerFileService.readAndLaunchPalynologie();
            response.setResultats(results);
        } catch (FichierInvalideException | TikaException | IOException e) {
            LOGGER.error("Erreur lors de l'extraction palynologie", e);
        }
        return Response.ok(response).build();
    }

    @Override
    @ApiResponses({@ApiResponse(code = 200, message = "", response = String.class)})
    @ServiceMethod
    public Response sauvegarder(final String data)  throws InvalidFormatException, ParsingException {

            this.updatedValuesService.parseAndSavePalynologie(data);
        return Response.ok().build();
    }

    @Override
    @ApiResponses({@ApiResponse(code = 200, message = "", response = String.class)})
    @ServiceMethod
    public Response getCsvFile() {

        String csv = this.updatedValuesService.getCsvPalynologie();


        String disposition = "attachment; fileName=extractionPalynologie.csv";
        Response response = Response.ok(csv).build();
        response.getHeaders().add("Content-Disposition", disposition);
        // Réponse du service
        return response;

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses({@ApiResponse(code = 200, message = "", response = PalynologieDocument.class)})
    @ServiceMethod
    @Path("/get/{id}")
    public Response getValue(@PathParam("id") final int key) {

        PalynologieDocument resultat = this.updatedValuesService.getValeursPalynologie().get(key);
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
