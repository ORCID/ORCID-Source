package org.orcid.persistence.jpa.entities;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

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
    public boolean isDetached() {
        return isDetached;
    }

    public void setDetached(boolean isDetached) {
        this.isDetached = isDetached;
    }

    @Transient
    public String getCachedSourceId() {
        return cachedSourceId;
    }

    public void setCachedSourceId(String cachedSourceId) {
        this.cachedSourceId = cachedSourceId;
    }

    @Transient
    public String getCachedSourceName() {
        return cachedSourceName;
    }

    public void setCachedSourceName(String cachedSourceName) {
        this.cachedSourceName = cachedSourceName;
    }
    
}
