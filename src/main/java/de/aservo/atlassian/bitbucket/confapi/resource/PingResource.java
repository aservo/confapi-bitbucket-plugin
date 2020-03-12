package de.aservo.atlassian.bitbucket.confapi.resource;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("ping")
@AnonymousAllowed
@Produces({MediaType.APPLICATION_JSON})
public class PingResource {

    public static final String PONG = "pong";

    /**
     * Simple ping method for probing the REST api. Returns 'pong' upon success
     *
     * @return response
     */
    @GET
    @Operation(summary = "Ping",
            description = "Simple connectivity check",
            responses = @ApiResponse(responseCode = "![Status 200][status-200]", description = "returns pong", content = @Content(schema = @Schema(implementation = String.class))))
    public Response get() {
        return Response.ok(PONG).build();
    }
}
