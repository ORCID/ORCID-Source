/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.togglz;

import java.io.File;
import java.io.IOException;

import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.jaxb.model.message.OrcidType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.togglz.core.Feature;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.file.FileBasedStateRepository;
import org.togglz.core.user.FeatureUser;
import org.togglz.core.user.SimpleFeatureUser;
import org.togglz.core.user.UserProvider;

@Component
public class OrcidTogglzConfiguration implements TogglzConfig {

    @Value("${org.orcid.frontend.togglz.config:classpath:/features.properties}")
    private String togglzConfigFilePath;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public Class<? extends Feature> getFeatureClass() {
        return Features.class;
    }

    @Override
    public StateRepository getStateRepository() {
        Resource r = resourceLoader.getResource(togglzConfigFilePath);
        File configFile = null;
        try {
            configFile = r.getFile();
        } catch (IOException e) {

        }
        if (configFile == null || !configFile.exists()) {
            throw new IllegalArgumentException("Unable to find config file " + togglzConfigFilePath);
        }
        return new FileBasedStateRepository(configFile);
    }

    @Override
    public UserProvider getUserProvider() {
        return new UserProvider() {
            @Override
            public FeatureUser getCurrentUser() {
                boolean isAdmin = false;
                String userOrcid = null;
                SecurityContext context = SecurityContextHolder.getContext();
                if (context != null && context.getAuthentication() != null) {
                    Authentication authentication = context.getAuthentication();
                    if (authentication != null) {
                        Object principal = authentication.getPrincipal();
                        if (principal instanceof OrcidProfileUserDetails) {
                            OrcidProfileUserDetails userDetails = (OrcidProfileUserDetails) principal;
                            isAdmin = OrcidType.ADMIN.equals(userDetails.getOrcidType());
                            userOrcid = userDetails.getOrcid();
                        }
                    }
                }
                return new SimpleFeatureUser(userOrcid, isAdmin);
            }
        };
    }

}
