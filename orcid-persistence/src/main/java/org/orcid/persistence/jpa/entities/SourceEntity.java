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
package org.orcid.persistence.jpa.entities;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.message.Visibility;

/**
 * 
 * @author Will Simpson
 * 
 */
@Embeddable
public class SourceEntity {

    private ProfileEntity sourceProfile;
    private ClientDetailsEntity sourceClient;

    public SourceEntity() {
    }

    public SourceEntity(ProfileEntity sourceProfile) {
        this.sourceProfile = sourceProfile;
    }

    public SourceEntity(String sourceId) {
        if (sourceId != null) {
            if (sourceId.startsWith("APP-")) {
                sourceClient = new ClientDetailsEntity();
                sourceClient.setId(sourceId);
            } else {
                sourceProfile = new ProfileEntity(sourceId);
            }
        }
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_id")
    public ProfileEntity getSourceProfile() {
        return sourceProfile;
    }

    public void setSourceProfile(ProfileEntity sourceProfile) {
        this.sourceProfile = sourceProfile;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_source_id")
    public ClientDetailsEntity getSourceClient() {
        return sourceClient;
    }

    public void setSourceClient(ClientDetailsEntity sourceClient) {
        this.sourceClient = sourceClient;
    }

    @Transient
    public String getSourceName() {
        if (sourceClient != null) {
            return sourceClient.getClientName();
        }
        if (sourceProfile != null) {
            // Set the source name
            // If it is a user, check if it have a credit name and is visible
            if (!StringUtils.isEmpty(sourceProfile.getCreditName()) && Visibility.PUBLIC.equals(sourceProfile.getCreditNameVisibility())) {
                return sourceProfile.getCreditName();
            } else {
                // If it doesn't, let's use the give name + family name
                return sourceProfile.getGivenNames() + (StringUtils.isEmpty(sourceProfile.getFamilyName()) ? "" : " " + sourceProfile.getFamilyName());
            }
        }
        return null;
    }

    @Transient
    public String getSourceId() {
        if (sourceClient != null) {
            return sourceClient.getClientId();
        }
        if (sourceProfile != null) {
            return sourceProfile.getId();
        }
        return null;
    }

}
