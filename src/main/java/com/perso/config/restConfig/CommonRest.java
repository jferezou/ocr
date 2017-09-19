package com.perso.config.restConfig;

import com.perso.annotation.ServiceMethod;
import com.perso.service.EstimateTimeService;
import com.perso.service.ReaderFileService;
import com.perso.utils.EstimateTime;
import org.apache.cxf.jaxrs.impl.ResponseBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

@Consumes(MediaType.APPLICATION_JSON)
@Service
@Path("/traitement")
public class CommonRest {

    @Value("${dossier.temporaire}")
    private String tempDir;
    @Resource
    EstimateTimeService estimateTimeService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderFileService.class);


    @GET
    @Path("/get/pdf")
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
