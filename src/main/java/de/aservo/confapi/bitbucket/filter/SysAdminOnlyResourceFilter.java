package de.aservo.confapi.bitbucket.filter;

import com.atlassian.bitbucket.auth.AuthenticationService;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AuthenticationRequiredException;
import com.atlassian.plugins.rest.common.security.AuthorisationException;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.ext.Provider;

import static com.atlassian.bitbucket.permission.Permission.SYS_ADMIN;

@Provider
@Named
public class SysAdminOnlyResourceFilter implements ResourceFilter, ContainerRequestFilter {

    private final AuthenticationService authenticationService;
    private final PermissionService permissionService;

    @Inject
    public SysAdminOnlyResourceFilter(
            @ComponentImport final AuthenticationService authenticationService,
            @ComponentImport final PermissionService permissionService) {

        this.authenticationService = authenticationService;
        this.permissionService = permissionService;
    }

    // these methods exists as "preparation" for abstract parent from commons

    public ApplicationUser getLoggedInUser() {
        return authenticationService.get().getUser().orElse(null);
    }

    public boolean isSystemAdministrator(ApplicationUser user) {
        return permissionService.hasUserPermission(user, SYS_ADMIN);
    }

    // these methods should be implemented in abstract parents in commons, but how to deal with Atlassian deps?

    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {
        final ApplicationUser loggedInUser = getLoggedInUser();
        if (loggedInUser == null) {
            throw new AuthenticationRequiredException();
        } else if (!isSystemAdministrator(loggedInUser)) {
            throw new AuthorisationException("Client must be authenticated as an system administrator to access this resource.");
        }
        return containerRequest;
    }

    @Override
    public ContainerRequestFilter getRequestFilter() {
        return this;
    }

    @Override
    public ContainerResponseFilter getResponseFilter() {
        return null;
    }

}
