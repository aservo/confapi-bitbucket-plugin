package de.aservo.atlassian.bitbucket.confapi.resource;

import com.atlassian.annotations.PublicApi;
import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.atlassian.bitbucket.confapi.filter.AdminOnlyResourceFilter;
import de.aservo.atlassian.bitbucket.confapi.model.SettingsBean;
import de.aservo.atlassian.bitbucket.confapi.rest.RestResource;
import de.aservo.atlassian.bitbucket.confapi.service.AdminService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Objects;

@PublicApi
@Path("settings")
@ResourceFilters(AdminOnlyResourceFilter.class)
public class SettingsResource extends RestResource {

    private final AdminService adminService;

    public SettingsResource(AdminService adminService) {
        this.adminService = adminService;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getGeneralSettings() {
        try {
            SettingsBean settings = new SettingsBean();
            settings.setBaseUrl(Objects.requireNonNull(adminService.getPropertiesService().getBaseUrl()).toString());
            settings.setDisplayName(adminService.getPropertiesService().getDisplayName());
            settings.setMode(adminService.getPropertiesService().getMode().toString());
            return Response.ok(settings).build();
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response setGeneralSettings(SettingsBean settings) {
        try {
            validateInputs(settings);
            adminService.getPropertiesService().setBaseURL(URI.create(settings.getBaseUrl()));
            adminService.getPropertiesService().setDisplayName(settings.getDisplayName());
            return getGeneralSettings();
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }
}