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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.togglz.core.Feature;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.file.FileBasedStateRepository;
import org.togglz.core.user.UserProvider;
import org.togglz.servlet.user.ServletUserProvider;

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
        } catch(IOException ioe){
            
        }
        if(configFile == null || !configFile.exists()) {
            throw new IllegalArgumentException("Unable to find config file " + togglzConfigFilePath);
        }
        return new FileBasedStateRepository(configFile);
    }

    @Override
    public UserProvider getUserProvider() {
        return new ServletUserProvider("admin");
    }

}
