package org.orcid.core.manager.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.manager.NotificationValidationManager;
import org.orcid.jaxb.model.notification.addactivities.AuthorizationUrl;
import org.orcid.jaxb.model.notification.addactivities.NotificationAddActivities;

public class NotificationValidationManagerImpl implements NotificationValidationManager {

    @Override
    public void validateNotificationAddActivities(NotificationAddActivities notification) {
        AuthorizationUrl authorizationUrl = notification.getAuthorizationUrl();
        String uriString = authorizationUrl.getUri();
        if (StringUtils.isBlank(uriString)) {
            throw new OrcidValidationException("Authorization uri must not be blank");
        }
        try {
            new URI(uriString);
        } catch (URISyntaxException e) {
            throw new OrcidValidationException("Bad authorization uri", e);
        }
    }

}
