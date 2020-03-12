package de.aservo.atlassian.bitbucket.confapi.resource;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.atlassian.bitbucket.confapi.filter.AdminOnlyResourceFilter;
import de.aservo.atlassian.confapi.model.ErrorCollection;
import de.aservo.atlassian.confapi.model.UserDirectoryBean;
import de.aservo.atlassian.confapi.service.UserDirectoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * The type User directory resource.
 */
@Path("/user-directories")
@Produces(MediaType.APPLICATION_JSON)
@ResourceFilters(AdminOnlyResourceFilter.class)
public class UserDirectoryResource {

    private static final Logger log = LoggerFactory.getLogger(UserDirectoryResource.class);

    private final UserDirectoryService directoryService;

    /**
     * Instantiates a new User directory resource.
     *
     * @param directoryService the crowd directory service
     */
    public UserDirectoryResource(UserDirectoryService directoryService) {
        this.directoryService = checkNotNull(directoryService);
    }

    /**
     * Gets directories.
     *
     * @return the directories
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Retrieves user directory information",
            description = "Upon successful request, returns a list of `UserDirectoryBean` object containing user directory details, e.g. \n```\n" +
                    "[\n" +
                    "  {\n" +
                    "    \"active\": true,\n" +
                    "    \"name\": \"Confluence Internal Directory\",\n" +
                    "    \"type\": \"INTERNAL\",\n" +
                    "    \"description\": \"Confluence default internal directory\",\n" +
                    "    \"implClass\": \"com.atlassian.crowd.directory.InternalDirectory\"\n" +
                    "  }\n" +
                    "]" +
                    "\n```",
            responses = {
                    @ApiResponse(responseCode = "![Status 200][status-200]", description = "user directory details list", content = @Content(schema = @Schema(implementation = UserDirectoryBean.class))),
                    @ApiResponse(responseCode = "![Status 400][status-400]", description = "An error occurred while retrieving the user directory list")
            })
    public Response getDirectories() {
        final ErrorCollection errorCollection = new ErrorCollection();
        try {
            List<UserDirectoryBean> directories = directoryService.getDirectories();
            return Response.ok(directories).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errorCollection.addErrorMessage(e.getMessage());
        }
        return Response.status(BAD_REQUEST).entity(errorCollection).build();
    }

    /**
     * Add directory.
     *
     * @param testConnection the test connection
     * @param directory      the directory
     * @return the response
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Adds a new user directory",
            description = "Upon successful request, returns the added `UserDirectoryBean` object, Any existing configurations with the same 'name' property are removed before adding the new configuration",
            responses = {
                    @ApiResponse(responseCode = "![Status 200][status-200]", description = "user directory added", content = @Content(schema = @Schema(implementation = UserDirectoryBean.class))),
                    @ApiResponse(responseCode = "![Status 400][status-400]", description = "An error occured while setting adding the new user directory")
            },
            parameters = @Parameter(description = "Whether or not to test the connection to the user directory service, e.g. CROWD (DEFAULT = true)", schema = @Schema(implementation = Boolean.class)),
            requestBody = @RequestBody(description = "The user directory to add", required = true, content = @Content(schema = @Schema(implementation = UserDirectoryBean.class))))
    public Response addDirectory(@QueryParam("test") Boolean testConnection, UserDirectoryBean directory) {
        final ErrorCollection errorCollection = new ErrorCollection();
        try {
            boolean test = testConnection == null ? true : testConnection;
            UserDirectoryBean addDirectory = directoryService.addDirectory(directory, test);
            return Response.ok(addDirectory).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errorCollection.addErrorMessage(e.getMessage());
        }
        return Response.status(BAD_REQUEST).entity(errorCollection).build();
    }
}
