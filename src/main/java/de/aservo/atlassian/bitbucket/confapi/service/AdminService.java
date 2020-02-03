package de.aservo.atlassian.bitbucket.confapi.service;

import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

@ExportAsService
@BitbucketComponent
public class AdminService {

    private ApplicationPropertiesService applicationPropertiesService;

    public AdminService(@ComponentImport ApplicationPropertiesService applicationPropertiesService) {
        this.applicationPropertiesService = applicationPropertiesService;
    }

    public ApplicationPropertiesService getPropertiesService() {
        return applicationPropertiesService;
    }
}
