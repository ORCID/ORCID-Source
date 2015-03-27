package org.orcid.persistence.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record.peer_review.Role;
import org.orcid.jaxb.model.record.peer_review.PeerReviewType;

@Entity
@Table(name = "peer_review")
public class PeerReviewEntity extends BaseEntity<Long> implements ProfileAware, SourceAware {

    private static final long serialVersionUID = -5834113137659672968L;
    private Long id;
    private ProfileEntity profile;
    private Role role;
    private OrgEntity org;
    private String externalIdentifiersJson;
    private String url;
    private PeerReviewType type;
    private FuzzyDateEntity completionDate;
    private SourceEntity source;
    private Visibility visibility;
    
    @Override
    public Long getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public OrgEntity getOrg() {
        return org;
    }

    public void setOrg(OrgEntity org) {
        this.org = org;
    }

    public String getExternalIdentifiersJson() {
        return externalIdentifiersJson;
    }

    public void setExternalIdentifiersJson(String externalIdentifiersJson) {
        this.externalIdentifiersJson = externalIdentifiersJson;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PeerReviewType getType() {
        return type;
    }

    public void setType(PeerReviewType type) {
        this.type = type;
    }

    public FuzzyDateEntity getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(FuzzyDateEntity completionDate) {
        this.completionDate = completionDate;
    }

    public SourceEntity getSource() {
        return source;
    }

    public void setSource(SourceEntity source) {
        this.source = source;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }
    
    @Override
    public ProfileEntity getProfile() {
        return profile;
    }   
}
