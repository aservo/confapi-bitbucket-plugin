package de.aservo.atlassian.bitbucket.confapi.helper;

import com.atlassian.bitbucket.auth.AuthenticationContext;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.TestApplicationUser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.WebApplicationException;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebAuthenticationHelperTest {

    private final ApplicationUser user = new TestApplicationUser("test");

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Mock
    private AuthenticationContext authenticationContext;

    @Mock
    private PermissionService permissionService;

    private WebAuthenticationHelper webAuthenticationHelper;

    @Before
    public void setup() {
        webAuthenticationHelper = new WebAuthenticationHelper(
                authenticationContext, permissionService);
    }

    @Test
    public void testNotAuthenticated() {
        when(webAuthenticationHelper.getAuthenticatedUser()).thenReturn(null);
        exceptionRule.expect(WebApplicationException.class);
        webAuthenticationHelper.mustBeSysAdmin();
    }

    @Test
    public void testNotSysAdmin() {
        when(webAuthenticationHelper.getAuthenticatedUser()).thenReturn(user);
        when(permissionService.hasGlobalPermission(user, Permission.SYS_ADMIN)).thenReturn(false);
        exceptionRule.expect(WebApplicationException.class);
        webAuthenticationHelper.mustBeSysAdmin();
    }

    @Test
    @SuppressWarnings("java:S2699") // Ignore that no assertion is present
    public void testIsSysAdmin() {
        when(webAuthenticationHelper.getAuthenticatedUser()).thenReturn(user);
        when(permissionService.hasGlobalPermission(user, Permission.SYS_ADMIN)).thenReturn(true);
        webAuthenticationHelper.mustBeSysAdmin();
    }

}
