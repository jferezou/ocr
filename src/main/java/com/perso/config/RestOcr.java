package com.perso.config;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.perso.annotation.ServiceMethod;
import com.perso.exception.FichierInvalideException;
import com.perso.service.ReaderFileService;
import com.perso.service.TransformServiceImpl;
import com.perso.utils.ResponseTraitement;
import com.perso.utils.ResultatPdf;
import org.apache.cxf.jaxrs.impl.ResponseBuilderImpl;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
    @Autowired
    ReaderFileService readerFileService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderFileService.class);

    @GET
    @Path("/t1")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceMethod
    public Response lancerTraitement()  throws InvalidFormatException {

        ResponseTraitement response = new ResponseTraitement();
        try {
            List<ResultatPdf> results = this.readerFileService.readAndLaunch();
            response.setResultats(results);
        } catch (FichierInvalideException | TikaException | IOException e) {
            LOGGER.error("", e);
        }
//        String myValue = "{\"resultats\":[{\"id\":1,\"pdfFilePath\":\"170703171333_0001_2_1.pdf\",\"echantillon\":\"Troyes P212CR969 8/06/17\",\"compositions\":[{\"value\":\"Papaver\",\"percentage\":58.3,\"type\":\"Dominant\",\"valid\":false},{\"value\":\"Sainfoin\",\"percentage\":20.4,\"type\":\"Accompagnement\",\"valid\":false},{\"value\":\"Phacêlie\",\"percentage\":10.0,\"type\":\"Isolé\",\"valid\":false},{\"value\":\"Tamaris\",\"percentage\":3.4,\"type\":\"Isolé\",\"valid\":false},{\"value\":\"Seringat\",\"percentage\":3.0,\"type\":\"Isolé\",\"valid\":false},{\"value\":\"Rèéda\",\"percentage\":0.0,\"type\":\"Isolé\",\"valid\":false},{\"value\":\"Tilleul\",\"percentage\":0.0,\"type\":\"Isolé\",\"valid\":false},{\"value\":\"Nerprun\",\"percentage\":0.0,\"type\":\"Isolé\",\"valid\":false},{\"value\":\"Sedum\",\"percentage\":0.0,\"type\":\"Isolé\",\"valid\":false},{\"value\":\"Trèfle blanc\",\"percentage\":0.0,\"type\":\"Isolé\",\"valid\":false},{\"value\":\"Aigremnine eupatoire\",\"percentage\":0.0,\"type\":\"Isolé\",\"valid\":false},{\"value\":\"Luzerne\",\"percentage\":0.0,\"type\":\"Isolé\",\"valid\":false},{\"value\":\"Sumac\",\"percentage\":0.0,\"type\":\"Isolé\",\"valid\":false}]}]}";
        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
//        responseBuilder.entity(myValue);
        responseBuilder.entity(response);
        responseBuilder.status(200);
        responseBuilder.header("Content-Type", "application/json;charset=utf-8");
        responseBuilder.header("Access-Control-Allow-Origin", "*");

        // Réponse du service
        return responseBuilder.build();
    }

    @GET
    @Path("/getpdf")
    @Produces("application/pdf")
    @ServiceMethod
    public Response getFile(@QueryParam("pdfName") final String pdfName) {

        File file = new File(tempDir+"\\"+pdfName);

//        Response.ResponseBuilder response = Response.ok(file);
//        response.header("Access-Control-Allow-Origin", "*");
        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        responseBuilder.entity(file);
        responseBuilder.status(200);
        responseBuilder.header("Access-Control-Allow-Origin", "*");

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
