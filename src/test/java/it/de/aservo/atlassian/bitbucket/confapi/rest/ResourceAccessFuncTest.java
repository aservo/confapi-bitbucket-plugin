package it.de.aservo.atlassian.bitbucket.confapi.rest;

import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.DetailedUser;
import com.atlassian.bitbucket.user.SecurityService;
import com.atlassian.bitbucket.user.UserAdminService;
import com.atlassian.bitbucket.util.UncheckedOperation;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.osgi.test.AtlassianPluginsTestRunner;
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.handlers.BasicAuthSecurityHandler;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

import static de.aservo.atlassian.bitbucket.confapi.rest.SettingsResource.SETTINGS_PATH;
import static org.junit.Assert.assertEquals;

@RunWith(AtlassianPluginsTestRunner.class)
public class ResourceAccessFuncTest {

    @ComponentImport
    private final SecurityService securityService;

    @ComponentImport
    private final UserAdminService userAdminService;

    @Inject
    public ResourceAccessFuncTest(
            final SecurityService securityService,
            final UserAdminService userAdminService) {

        this.securityService = securityService;
        this.userAdminService = userAdminService;
    }

    @Test
    public void testAccessForbiddenAsAnonymousUser() {
        final String baseUrl = System.getProperty("baseurl");
        final String resourceUrl = baseUrl + "/rest/confapi/1/" + SETTINGS_PATH;

        final RestClient client = new RestClient();
        final Resource resource = client.resource(resourceUrl);

        assertEquals(Status.UNAUTHORIZED.getStatusCode(), resource.get().getStatusCode());
    }

    @Test
    public void testAccessForbiddenAsNormalUser() {
        ApplicationUser user = userAdminService.getUserDetails("test");

        if (user == null) {
            user = securityService
                    .withPermission(Permission.ADMIN, "create new users")
                    .call((UncheckedOperation<ApplicationUser>) () -> {
                        userAdminService.createUser("test", "test", "test", "test@localhost");
                        return userAdminService.getUserDetails("test");
                    });
            assert user != null;
        }

        final String baseUrl = System.getProperty("baseurl");
        final String resourceUrl = baseUrl + "/rest/confapi/1/" + SETTINGS_PATH;

        final BasicAuthSecurityHandler basicAuthHandler = new BasicAuthSecurityHandler();
        basicAuthHandler.setUserName(user.getName());
        basicAuthHandler.setPassword(user.getName());

        final ClientConfig config = new ClientConfig();
        config.handlers(basicAuthHandler);

        final RestClient client = new RestClient(config);
        final Resource resource = client.resource(resourceUrl);

        assertEquals(Status.FORBIDDEN.getStatusCode(), resource.get().getStatusCode());

        final ApplicationUser finalUser = user;
        securityService
                .withPermission(Permission.ADMIN, "create new users")
                .call((UncheckedOperation<DetailedUser>) () -> {
                    return userAdminService.deleteUser(finalUser.getName());
                });
    }

    @Test
    public void testAccessSuccessfulAsSysAdmin() {
        final String baseUrl = System.getProperty("baseurl");
        final String resourceUrl = baseUrl + "/rest/confapi/1/" + SETTINGS_PATH;

        final BasicAuthSecurityHandler basicAuthHandler = new BasicAuthSecurityHandler();
        basicAuthHandler.setUserName("admin");
        basicAuthHandler.setPassword("admin");

        final ClientConfig config = new ClientConfig();
        config.handlers(basicAuthHandler);

        final RestClient client = new RestClient(config);
        final Resource resource = client.resource(resourceUrl);

        assertEquals(Status.OK.getStatusCode(), resource.get().getStatusCode());
    }

}
