package de.aservo.atlassian.bitbucket.confapi.rest;

import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.aservo.atlassian.bitbucket.confapi.model.SettingsBean;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("settings")
@Produces({MediaType.APPLICATION_JSON})
@BitbucketComponent
public class SettingsResource {

    @ComponentImport
    private final ApplicationPropertiesService applicationPropertiesService;

    public SettingsResource(
            final ApplicationPropertiesService applicationPropertiesService) {

        this.applicationPropertiesService = applicationPropertiesService;
    }

    @GET
    public Response getSettings() {
        final SettingsBean settingsBean = SettingsBean.from(applicationPropertiesService);
        return Response.ok(settingsBean).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    public Response putSettings(SettingsBean settings) {
        if (settings.getBaseurl() != null) {
            applicationPropertiesService.setBaseURL(settings.getBaseurl());
        }

        // TODO: Cannot set mode using ApplicationPropertiesService

        if (settings.getTitle() != null) {
            applicationPropertiesService.setDisplayName(settings.getTitle());
        }

        return getSettings();
    }

}
