package de.aservo.atlassian.bitbucket.confapi.filter;


import com.atlassian.bitbucket.auth.Authentication;
import com.atlassian.bitbucket.auth.AuthenticationService;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AuthenticationRequiredException;
import com.atlassian.plugins.rest.common.security.AuthorisationException;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;

import javax.ws.rs.ext.Provider;
import java.util.Optional;

/**
 * The Admin only resource filter.
 */
@Provider
public class AdminOnlyResourceFilter implements ResourceFilter, ContainerRequestFilter {

    private final AuthenticationService authenticationService;
    private final PermissionService permissionService;

    public AdminOnlyResourceFilter(@ComponentImport AuthenticationService authenticationService,
                                   @ComponentImport PermissionService permissionService) {
        this.authenticationService = authenticationService;
        this.permissionService = permissionService;
    }

    public ContainerRequestFilter getRequestFilter() {
        return this;
    }

    public ContainerResponseFilter getResponseFilter() {
        return null;
    }

    public ContainerRequest filter(ContainerRequest containerRequest) {
        Authentication authentication = authenticationService.get();
        Optional<ApplicationUser> user = authentication.getUser();
        if (!user.isPresent()) {
            throw new AuthenticationRequiredException();
        } else {
            if (!permissionService.hasUserPermission(user.get(), Permission.USER_ADMIN)) {
                throw new AuthorisationException("Client must be authenticated as an administrator to access this resource.");
            }
        }
        return containerRequest;
    }
}
