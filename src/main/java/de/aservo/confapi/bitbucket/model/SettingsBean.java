package de.aservo.confapi.bitbucket.model;

import com.atlassian.bitbucket.server.ApplicationMode;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Arrays;

@XmlRootElement(name = "settings")
public class SettingsBean {

    @XmlElement
    private final String baseurl;

    @XmlElement
    private final String mode;

    @XmlElement
    private final String title;

    /**
     * The default constructor is needed for JSON request deserialization.
     */
    public SettingsBean() {
        this.baseurl = null;
        this.mode = null;
        this.title = null;
    }

    public SettingsBean(
            final String baseUrl,
            final String mode,
            final String title) {

        this.baseurl = baseUrl;
        this.mode = mode != null ? mode.toLowerCase() : null;
        this.title = title;
    }

    public URI getBaseurl() {
        return baseurl != null ? URI.create(baseurl) : null;
    }

    public ApplicationMode getMode() {
        return Arrays.stream(ApplicationMode.values())
                .filter(m -> m.getId().equalsIgnoreCase(mode))
                .findAny().orElse(null);
    }

    public String getTitle() {
        return title;
    }

    public static SettingsBean from(
            final ApplicationPropertiesService applicationPropertiesService) {

        final String baseurl = applicationPropertiesService.getBaseUrl() != null ?
                applicationPropertiesService.getBaseUrl().toString() : null;

        return new SettingsBean(
                baseurl,
                applicationPropertiesService.getMode().toString(),
                applicationPropertiesService.getDisplayName()
        );
    }

}
