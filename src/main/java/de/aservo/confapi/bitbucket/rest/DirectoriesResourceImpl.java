package de.aservo.confapi.bitbucket.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.confapi.bitbucket.filter.SysAdminOnlyResourceFilter;
import de.aservo.confapi.commons.constants.ConfAPI;
import de.aservo.confapi.commons.rest.AbstractDirectoriesResourceImpl;
import de.aservo.confapi.commons.service.api.DirectoriesService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Path;

@Path(ConfAPI.DIRECTORIES)
@ResourceFilters(SysAdminOnlyResourceFilter.class)
@Named
public class DirectoriesResourceImpl extends AbstractDirectoriesResourceImpl {

    @Inject
    public DirectoriesResourceImpl(DirectoriesService directoryService) {
        super(directoryService);
    }

    // Completely inhering the implementation of AbstractDirectoriesResourceImpl

}
