package de.aservo.atlassian.bitbucket.confapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class SettingsBean {

    @NotNull
    private String baseUrl;

    @NotNull
    @Size(min = 1, max = 255)
    private String displayName;

    private String mode;
}
