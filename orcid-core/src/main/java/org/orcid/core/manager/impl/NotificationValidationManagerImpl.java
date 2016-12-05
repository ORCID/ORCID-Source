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
package org.orcid.core.manager.impl;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.manager.NotificationValidationManager;
import org.orcid.core.manager.validator.ExternalIDValidator;
import org.orcid.jaxb.model.notification.permission_rc4.AuthorizationUrl;
import org.orcid.jaxb.model.notification.permission_rc4.NotificationPermission;

public class NotificationValidationManagerImpl implements NotificationValidationManager {

    @Resource
    private ExternalIDValidator externalIDValidator;
    
    @Override
    public void validateNotificationPermission(NotificationPermission notification) {
        AuthorizationUrl authorizationUrl = notification.getAuthorizationUrl();
        String uriString = authorizationUrl.getUri();
        if (StringUtils.isNotBlank(uriString)) {
            try {
                new URI(uriString);
            } catch (URISyntaxException e) {
                throw new OrcidValidationException("Bad authorization uri", e);
            }
        }
        
        externalIDValidator.validateNotificationItems(notification.getItems());
    }

}
