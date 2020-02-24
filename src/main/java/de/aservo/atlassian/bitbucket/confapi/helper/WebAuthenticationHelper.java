package de.aservo.atlassian.bitbucket.confapi.helper;

import com.atlassian.bitbucket.auth.AuthenticationContext;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Named
public class WebAuthenticationHelper {

    @ComponentImport
    private final AuthenticationContext authenticationContext;

    @ComponentImport
    private final PermissionService permissionService;

    @Inject
    public WebAuthenticationHelper(
            final AuthenticationContext authenticationContext,
            final PermissionService permissionService) {

        this.authenticationContext = authenticationContext;
        this.permissionService = permissionService;
    }

    public ApplicationUser getAuthenticatedUser() {
        return authenticationContext.getCurrentUser();
    }

    public void mustBeSysAdmin() {
        final ApplicationUser user = getAuthenticatedUser();

        if (user == null) {
            // NOSONAR: Ignore that WebApplicationException is a RuntimeException
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        final boolean isSysAdmin = permissionService.hasGlobalPermission(user, Permission.SYS_ADMIN);

        if (!isSysAdmin) {
            // NOSONAR: Ignore that WebApplicationException is a RuntimeException
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
    }

}
