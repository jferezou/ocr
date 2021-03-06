package com.perso.config.restConfig;

import com.perso.annotation.ServiceMethod;
import com.perso.service.EstimateTimeService;
import com.perso.service.ReaderFileService;
import com.perso.utils.EstimateTime;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

@Api(tags = "Commun")
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
    @ApiResponses({@ApiResponse(code = 200, message = "", response = File.class)})
    public Response getFile(@QueryParam("pdfName") final String pdfName) {

        File file = new File(tempDir+"\\"+pdfName);

        return Response.ok(file).build();

    }

    @GET
    @Path("/estimatetime")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceMethod
    @ApiResponses({@ApiResponse(code = 200, message = "", response = EstimateTime.class)})
    public Response estimateTime(@QueryParam("multiplicateur") final String multiplicateur, @QueryParam("isPalynologie") final Boolean isPalynologie) {

        EstimateTime estimateTime = this.estimateTimeService.estimateTime(multiplicateur, isPalynologie);

        return Response.ok(estimateTime).build();

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
