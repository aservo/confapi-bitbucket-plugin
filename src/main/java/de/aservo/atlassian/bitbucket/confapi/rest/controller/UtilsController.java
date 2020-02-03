package de.aservo.atlassian.bitbucket.confapi.rest.controller;

import de.aservo.atlassian.bitbucket.confapi.rest.RestResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("utils")
public class UtilsController extends RestResource {

    @GET
    @Path("ping")
    @Produces({MediaType.APPLICATION_JSON})
    public Response ping() {
        return Response.ok("pong").build();
    }
}