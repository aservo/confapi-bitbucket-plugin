package de.aservo.atlassian.bitbucket.confapi.rest;

import com.atlassian.bitbucket.server.ApplicationMode;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import de.aservo.atlassian.bitbucket.confapi.helper.WebAuthenticationHelper;
import de.aservo.atlassian.bitbucket.confapi.model.SettingsBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.net.URI;

import static de.aservo.atlassian.bitbucket.confapi.model.SettingsBeanTest.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SettingsResourceTest {

    @Mock
    private ApplicationPropertiesService applicationPropertiesService;

    @Mock
    private WebAuthenticationHelper webAuthenticationHelper;

    private SettingsResource settingsResource;

    @Before
    public void setup() {
        when(applicationPropertiesService.getBaseUrl()).thenReturn(URI.create(BASEURL));
        when(applicationPropertiesService.getMode()).thenReturn(ApplicationMode.valueOf(MODE));
        when(applicationPropertiesService.getDisplayName()).thenReturn(TITLE);

        settingsResource = new SettingsResource(
                applicationPropertiesService, webAuthenticationHelper);
    }

    @Test
    public void testGetSettings() {
        final Response response = settingsResource.getSettings();
        final SettingsBean bean = (SettingsBean) response.getEntity();

        verify(webAuthenticationHelper).mustBeSysAdmin();

        assertEquals(applicationPropertiesService.getBaseUrl(), bean.getBaseurl());
        assertEquals(applicationPropertiesService.getMode(), bean.getMode());
        assertEquals(applicationPropertiesService.getDisplayName(), bean.getTitle());
    }

    @Test
    public void testSetSettings() {
        final SettingsBean settingsBean = SettingsBean.from(applicationPropertiesService);
        final Response response = settingsResource.putSettings(settingsBean);

        verify(webAuthenticationHelper).mustBeSysAdmin();

        assertNotNull(applicationPropertiesService.getBaseUrl());
        assertNotNull(applicationPropertiesService.getDisplayName());

        ArgumentCaptor<URI> baseurlCaptor = ArgumentCaptor.forClass(URI.class);
        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);

        verify(applicationPropertiesService).setBaseURL(baseurlCaptor.capture());
        verify(applicationPropertiesService).setDisplayName(titleCaptor.capture());

        assertEquals(applicationPropertiesService.getBaseUrl(), baseurlCaptor.getValue());
        assertEquals(applicationPropertiesService.getDisplayName(), titleCaptor.getValue());
    }

}
