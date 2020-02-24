package de.aservo.atlassian.bitbucket.confapi.rest;

import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.aservo.atlassian.bitbucket.confapi.helper.WebAuthenticationHelper;
import de.aservo.atlassian.bitbucket.confapi.model.SettingsBean;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(SettingsResource.SETTINGS_PATH)
@Produces({MediaType.APPLICATION_JSON})
@Named
public class SettingsResource {

    public static final String SETTINGS_PATH = "settings";

    @ComponentImport
    private final ApplicationPropertiesService applicationPropertiesService;

    private final WebAuthenticationHelper webAuthenticationHelper;

    @Inject
    public SettingsResource(
            final ApplicationPropertiesService applicationPropertiesService,
            final WebAuthenticationHelper webAuthenticationHelper) {

        this.applicationPropertiesService = applicationPropertiesService;
        this.webAuthenticationHelper = webAuthenticationHelper;
    }

    @GET
    public Response getSettings() {
        webAuthenticationHelper.mustBeSysAdmin();

        final SettingsBean settingsBean = SettingsBean.from(applicationPropertiesService);
        return Response.ok(settingsBean).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    public Response putSettings(
            final SettingsBean settings) {

        webAuthenticationHelper.mustBeSysAdmin();

        if (settings.getBaseurl() != null) {
            applicationPropertiesService.setBaseURL(settings.getBaseurl());
        }

        // TODO: Cannot set mode using ApplicationPropertiesService

        if (settings.getTitle() != null) {
            applicationPropertiesService.setDisplayName(settings.getTitle());
        }

        final SettingsBean settingsBean = SettingsBean.from(applicationPropertiesService);
        return Response.ok(settingsBean).build();
    }

}
