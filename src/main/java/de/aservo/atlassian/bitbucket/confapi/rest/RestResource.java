package de.aservo.atlassian.bitbucket.confapi.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.Response;

public class RestResource {

    private ObjectMapper jacksonMapper = new JacksonMapper().getContext(null);

    protected Response buildErrorResponse(Exception e) {
        return buildErrorResponse(e.getMessage());
    }

    protected Response buildErrorResponse(String message) {
        try {
            String errorJson = jacksonMapper.writeValueAsString(new ErrorMessage(message));
            return Response.status(Response.Status.BAD_REQUEST).entity(errorJson).build();
        } catch (JsonProcessingException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.toString()).build();
        }
    }

    protected String wrapObject(Object o) throws JsonProcessingException {
        return jacksonMapper.writeValueAsString(o);
    }
}
