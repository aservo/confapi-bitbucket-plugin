package de.aservo.confapi.bitbucket.service;

import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.aservo.confapi.commons.model.SettingsBean;
import de.aservo.confapi.commons.service.api.SettingsService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.net.URI;

@Named
@ExportAsService
public class SettingsServiceImpl implements SettingsService {

    private final ApplicationPropertiesService applicationPropertiesService;

    @Inject
    public SettingsServiceImpl(
            @ComponentImport final ApplicationPropertiesService applicationPropertiesService) {

        this.applicationPropertiesService = applicationPropertiesService;
    }

    @Override
    public SettingsBean getSettings() {
        final SettingsBean settingsBean = new SettingsBean();

        if (applicationPropertiesService.getBaseUrl() != null) {
            settingsBean.setBaseUrl(applicationPropertiesService.getBaseUrl().toString());
        }

        settingsBean.setTitle(applicationPropertiesService.getDisplayName());

        settingsBean.setMode(applicationPropertiesService.getMode().getId());

        return settingsBean;
    }

    @Override
    public SettingsBean setSettings(
            @NotNull SettingsBean settingsBean) {

        if (settingsBean.getBaseUrl() != null) {
            applicationPropertiesService.setBaseURL(URI.create(settingsBean.getBaseUrl()));
        }

        // Cannot set mode using ApplicationPropertiesService

        if (settingsBean.getTitle() != null) {
            applicationPropertiesService.setDisplayName(settingsBean.getTitle());
        }

        return getSettings();
    }

}
