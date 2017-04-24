package org.orcid.frontend.togglz;

import java.io.File;

import org.togglz.core.Feature;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.file.FileBasedStateRepository;
import org.togglz.core.user.UserProvider;

public class OrcidTogglzConfiguration implements TogglzConfig {

    @Override
    public Class<? extends Feature> getFeatureClass() {
        return Features.class;
    }

    @Override
    public StateRepository getStateRepository() {
        return new FileBasedStateRepository(new File("features.properties"));
    }

    @Override
    public UserProvider getUserProvider() {
        // TODO Auto-generated method stub
        return null;
    }

}
