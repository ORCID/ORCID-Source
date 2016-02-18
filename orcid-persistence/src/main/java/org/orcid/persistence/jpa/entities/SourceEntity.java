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

import java.io.Serializable;

import javax.persistence.Embeddable;
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
public class SourceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private ProfileEntity sourceProfile;
    private ClientDetailsEntity sourceClient;

    public SourceEntity() {
    }

    public SourceEntity(ProfileEntity sourceProfile) {
        this.sourceProfile = sourceProfile;
    }

    public SourceEntity(ClientDetailsEntity cde) {
        this.sourceClient = cde;
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

    @ManyToOne
    @JoinColumn(name = "source_id")
    public ProfileEntity getSourceProfile() {
        return sourceProfile;
    }

    public void setSourceProfile(ProfileEntity sourceProfile) {
        this.sourceProfile = sourceProfile;
    }

    @ManyToOne
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
            if(Visibility.PUBLIC.equals(sourceProfile.getNameEntity().getVisibility())) {
                if (!StringUtils.isEmpty(sourceProfile.getNameEntity().getCreditName())) {
                    return sourceProfile.getNameEntity().getCreditName();
                } else {
                    //If credit name is empty
                    return sourceProfile.getNameEntity().getGivenName() + (StringUtils.isEmpty(sourceProfile.getNameEntity().getFamilyName()) ? "" : " " + sourceProfile.getNameEntity().getFamilyName());
                }                
            } else {
                return null;
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
