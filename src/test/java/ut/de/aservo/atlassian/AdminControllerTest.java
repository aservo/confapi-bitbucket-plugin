package ut.de.aservo.atlassian;

import com.atlassian.bitbucket.server.ApplicationMode;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import de.aservo.atlassian.bitbucket.confapi.model.SettingsBean;
import de.aservo.atlassian.bitbucket.confapi.rest.ErrorMessage;
import de.aservo.atlassian.bitbucket.confapi.rest.controller.AdminController;
import de.aservo.atlassian.bitbucket.confapi.service.AdminService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdminControllerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private ApplicationPropertiesService applicationPropertiesService;

    @Mock
    private AdminService adminService;

    private AdminController adminController;

    @Before
    public void setup() {
        adminService = new AdminService(applicationPropertiesService);
        adminController = new AdminController(adminService);
    }

    @Test
    public void testGetSettings() throws URISyntaxException, IOException {

        when(adminService.getPropertiesService().getBaseUrl()).thenReturn(new URI("http://localhost:7990/bitbucket"));
        when(adminService.getPropertiesService().getDisplayName()).thenReturn("Bitbucket");
        when(adminService.getPropertiesService().getMode()).thenReturn(ApplicationMode.DEFAULT);

        final Response response = adminController.getGeneralSettings();
        assertThat(response.getEntity(), instanceOf(SettingsBean.class));
    }

    @Test
    public void testSetSettingsPositiveTest() throws URISyntaxException, IOException {

        when(adminService.getPropertiesService().getBaseUrl()).thenReturn(new URI("http://localhost:7990/bitbucket"));
        when(adminService.getPropertiesService().getDisplayName()).thenReturn("Bitbucket");
        when(adminService.getPropertiesService().getMode()).thenReturn(ApplicationMode.DEFAULT);

        SettingsBean settingsBean = new SettingsBean();
        settingsBean.setBaseUrl("http://localhost:7990/bitbucket");
        settingsBean.setDisplayName("Bitbucket");
        final Response response = adminController.setGeneralSettings(settingsBean);
        assertThat(response.getEntity(), instanceOf(SettingsBean.class));
    }

    @Test
    public void testSetSettingsInvalidBaseUrl() throws URISyntaxException, IOException {
        SettingsBean settingsBean = new SettingsBean();
        settingsBean.setBaseUrl(":::");
        settingsBean.setDisplayName("Bitbucket");
        final Response response = adminController.setGeneralSettings(settingsBean);
        assertThat(response.getEntity(), instanceOf(ErrorMessage.class));
        ErrorMessage errorMessage = (ErrorMessage) response.getEntity();
        assertThat(errorMessage.getMessage(), notNullValue());
        System.out.println(errorMessage.getMessage());
    }

    @Test
    public void testSetSettingsDisplayNameIsEmpty() throws URISyntaxException, IOException {
        SettingsBean settingsBean = new SettingsBean();
        settingsBean.setBaseUrl("localhost");
        settingsBean.setDisplayName("");
        final Response response = adminController.setGeneralSettings(settingsBean);
        assertThat(response.getEntity(), instanceOf(ErrorMessage.class));
        ErrorMessage errorMessage = (ErrorMessage) response.getEntity();
        assertThat(errorMessage.getMessage(), notNullValue());
        System.out.println(errorMessage.getMessage());
    }
}
