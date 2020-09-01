package de.aservo.confapi.bitbucket.model;

import com.atlassian.bitbucket.server.ApplicationMode;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SettingsBeanTest {

    public static final String BASEURL = "http://localhost:7990/bitbucket";
    public static final String MODE = "DEFAULT";
    public static final String TITLE = "ASERVO Bitbucket";

    @Mock
    private ApplicationPropertiesService applicationPropertiesService;

    @Before
    public void setup() {
        when(applicationPropertiesService.getBaseUrl()).thenReturn(URI.create(BASEURL));
        when(applicationPropertiesService.getMode()).thenReturn(ApplicationMode.valueOf(MODE));
        when(applicationPropertiesService.getDisplayName()).thenReturn(TITLE);
    }

    @Test
    public void testDefaultConstructor() {
        final SettingsBean bean = new SettingsBean();

        assertNull(bean.getBaseurl());
        assertNull(bean.getMode());
        assertNull(bean.getTitle());
    }

    @Test
    public void testFromConstructor() throws Exception {
        final SettingsBean bean = SettingsBean.from(applicationPropertiesService);

        assertEquals(bean.getBaseurl(), applicationPropertiesService.getBaseUrl());
        assertEquals(bean.getMode(), applicationPropertiesService.getMode());
        assertEquals(bean.getTitle(), applicationPropertiesService.getDisplayName());
    }

}
