package org.orcid.persistence.jpa.entities;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

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
    private String cachedSourceId;
    private String cachedSourceName;
    private boolean isDetached;

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
        if (cachedSourceName != null) {
            return cachedSourceName;
        }
        if (sourceClient != null) {
            return sourceClient.getClientName();
        }
        if (sourceProfile != null) {
            // Set the source name
            if (sourceProfile.getRecordNameEntity() != null) {
                // If it is a user, check if it have a credit name and is
                // visible
                if (org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.equals(sourceProfile.getRecordNameEntity().getVisibility())) {
                    if (!StringUtils.isEmpty(sourceProfile.getRecordNameEntity().getCreditName())) {
                        return sourceProfile.getRecordNameEntity().getCreditName();
                    } else {
                        // If credit name is empty
                        return sourceProfile.getRecordNameEntity().getGivenNames() + (StringUtils.isEmpty(sourceProfile.getRecordNameEntity().getFamilyName()) ? ""
                                : " " + sourceProfile.getRecordNameEntity().getFamilyName());
                    }
                } else {
                    return null;
                }
            } 
        }
        return null;
    }

    @Transient
    public String getSourceId() {
        if (cachedSourceId != null) {
            return cachedSourceId;
        }
        if (sourceClient != null) {
            return sourceClient.getClientId();
        }
        if (sourceProfile != null) {
            return sourceProfile.getId();
        }
        return null;
    }

    @Transient
    public boolean isDetached() {
        return isDetached;
    }

    public void setDetached(boolean isDetached) {
        this.isDetached = isDetached;
    }

    /**
     * Call this method before storing in cache to prevent a whole profile or
     * client being serialized.
     * 
     * WARNING: The entity must be detached (using DAO) so that the source is
     * not made null in DB.
     */
    public void prepareForCache() {
        if (!isDetached) {
            throw new IllegalStateException("Must not prepare source entity for cache, unless it is detached");
        }
        cachedSourceId = getSourceId();
        cachedSourceName = getSourceName();
        sourceClient = null;
        sourceProfile = null;
    }

}
