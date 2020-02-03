package de.aservo.atlassian.bitbucket.confapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SettingsBean {
    private String baseUrl;
    private String displayName;
    private String mode;
}
