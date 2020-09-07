package de.aservo.confapi.bitbucket.rest;

import com.atlassian.bitbucket.server.ApplicationMode;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import de.aservo.confapi.bitbucket.service.SettingsServiceImpl;
import de.aservo.confapi.commons.model.SettingsBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SettingsServiceImplTest {

    private static final String BASEURL = "http://localhost:7990/bitbucket";
    private static final String MODE = "DEFAULT";
    private static final String TITLE = "ASERVO Bitbucket";

    @Mock
    private ApplicationPropertiesService applicationPropertiesService;

    private SettingsServiceImpl settingsServiceImpl;

    @Before
    public void setup() {
        when(applicationPropertiesService.getBaseUrl()).thenReturn(URI.create(BASEURL));
        when(applicationPropertiesService.getMode()).thenReturn(ApplicationMode.valueOf(MODE));
        when(applicationPropertiesService.getDisplayName()).thenReturn(TITLE);

        settingsServiceImpl = new SettingsServiceImpl(applicationPropertiesService);
    }

    @Test
    public void testGetSettings() {
        final SettingsBean bean = settingsServiceImpl.getSettings();

        assertNotNull(applicationPropertiesService.getBaseUrl());
        assertEquals(applicationPropertiesService.getBaseUrl().toString(), bean.getBaseUrl());
        assertEquals(applicationPropertiesService.getMode().getId(), bean.getMode());
        assertEquals(applicationPropertiesService.getDisplayName(), bean.getTitle());
    }

    @Test
    public void testSetSettings() {
        final SettingsBean settingsBean = settingsServiceImpl.getSettings();
        SettingsBean updatedBean = settingsServiceImpl.setSettings(settingsBean);

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
