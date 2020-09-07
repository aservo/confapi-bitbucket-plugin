package de.aservo.confapi.bitbucket.filter;

import com.atlassian.bitbucket.auth.Authentication;
import com.atlassian.bitbucket.auth.AuthenticationService;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.plugins.rest.common.security.AuthenticationRequiredException;
import com.atlassian.plugins.rest.common.security.AuthorisationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static com.atlassian.bitbucket.permission.Permission.SYS_ADMIN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class SysadminOnlyResourceFilterTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private PermissionService permissionService;

    private SysAdminOnlyResourceFilter sysadminOnlyResourceFilter;

    @Before
    public void setup() {
        sysadminOnlyResourceFilter = new SysAdminOnlyResourceFilter(authenticationService, permissionService);
    }

    @Test
    public void testFilterDefaults() {
        assertNull(sysadminOnlyResourceFilter.getResponseFilter());
        assertEquals(sysadminOnlyResourceFilter, sysadminOnlyResourceFilter.getRequestFilter());
    }

    @Test
    public void testSysAdminAccess() {
        final Authentication authentication = mock(Authentication.class);
        doReturn(authentication).when(authenticationService).get();

        final ApplicationUser applicationUser = mock(ApplicationUser.class);
        doReturn(Optional.of(applicationUser)).when(authentication).getUser();
        doReturn(true).when(permissionService).hasUserPermission(applicationUser, SYS_ADMIN);

        assertNull(sysadminOnlyResourceFilter.filter(null));
    }

    @Test(expected = AuthenticationRequiredException.class)
    public void testNotAuthenticated() {
        final Authentication authentication = mock(Authentication.class);
        doReturn(authentication).when(authenticationService).get();

        sysadminOnlyResourceFilter.filter(null);
    }

    @Test(expected = AuthorisationException.class)
    public void testNotAuthorized() {
        final Authentication authentication = mock(Authentication.class);
        doReturn(authentication).when(authenticationService).get();

        final ApplicationUser applicationUser = mock(ApplicationUser.class);
        doReturn(Optional.of(applicationUser)).when(authentication).getUser();

        sysadminOnlyResourceFilter.filter(null);
    }

}
