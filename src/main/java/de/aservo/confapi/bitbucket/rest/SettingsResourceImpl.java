package de.aservo.confapi.bitbucket.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.confapi.bitbucket.filter.SysAdminOnlyResourceFilter;
import de.aservo.confapi.commons.constants.ConfAPI;
import de.aservo.confapi.commons.rest.AbstractSettingsResourceImpl;
import de.aservo.confapi.commons.service.api.SettingsService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Path;

@Path(ConfAPI.SETTINGS)
@ResourceFilters(SysAdminOnlyResourceFilter.class)
@Named
public class SettingsResourceImpl extends AbstractSettingsResourceImpl {

    @Inject
    public SettingsResourceImpl(SettingsService settingsService) {
        super(settingsService);
    }

}
