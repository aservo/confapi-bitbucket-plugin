package ut.de.aservo.atlassian;

import com.atlassian.bitbucket.server.ApplicationMode;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.aservo.atlassian.bitbucket.confapi.model.SettingsBean;
import de.aservo.atlassian.bitbucket.confapi.rest.ErrorMessage;
import de.aservo.atlassian.bitbucket.confapi.rest.JacksonMapper;
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

    private ObjectMapper jacksonMapper;

    @Before
    public void setup() {
        jacksonMapper = new JacksonMapper().getContext(null);
        adminService = new AdminService(applicationPropertiesService);
        adminController = new AdminController(adminService);
    }

    @Test
    public void testGetSettings() throws URISyntaxException, IOException {

        when(adminService.getPropertiesService().getBaseUrl()).thenReturn(new URI("http://localhost:7990/bitbucket"));
        when(adminService.getPropertiesService().getDisplayName()).thenReturn("Bitbucket");
        when(adminService.getPropertiesService().getMode()).thenReturn(ApplicationMode.DEFAULT);

        final Response response = adminController.getGeneralSettings();
        jacksonMapper.readValue(response.getEntity().toString(), SettingsBean.class);
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
        jacksonMapper.readValue(response.getEntity().toString(), SettingsBean.class);
    }

    @Test
    public void testSetSettingsInvalidBaseUrl() throws URISyntaxException, IOException {
        SettingsBean settingsBean = new SettingsBean();
        settingsBean.setBaseUrl(":::");
        settingsBean.setDisplayName("Bitbucket");
        final Response response = adminController.setGeneralSettings(settingsBean);
        String respStr = response.getEntity().toString();
        System.out.println(respStr);
        ErrorMessage errorMessage = jacksonMapper.readValue(respStr, ErrorMessage.class);
        assertThat(errorMessage.getMessage(), notNullValue());
    }

    @Test
    public void testSetSettingsDisplayNameIsEmpty() throws URISyntaxException, IOException {
        SettingsBean settingsBean = new SettingsBean();
        settingsBean.setBaseUrl("localhost");
        settingsBean.setDisplayName("");
        final Response response = adminController.setGeneralSettings(settingsBean);
        String respStr = response.getEntity().toString();
        System.out.println(respStr);
        ErrorMessage errorMessage = jacksonMapper.readValue(respStr, ErrorMessage.class);
        assertThat(errorMessage.getMessage(), notNullValue());
    }
}
