/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.jpa.entities.keys;

import java.io.Serializable;

import org.orcid.persistence.jpa.entities.ProfileEntity;

/**
 * @author Will Simpson
 */
public class WebhookEntityPk implements Serializable {

    private static final long serialVersionUID = 1L;

    private ProfileEntity profile;
    private String uri;

    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
