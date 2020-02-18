package de.aservo.atlassian.bitbucket.confapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@XmlRootElement(name = "settings")
public class SettingsBean {

    @NotNull
    @Size(min = 1)
    @XmlElement
    private String baseUrl;

    @NotNull
    @Size(min = 1, max = 255)
    @XmlElement
    private String displayName;

    private String mode;
}
