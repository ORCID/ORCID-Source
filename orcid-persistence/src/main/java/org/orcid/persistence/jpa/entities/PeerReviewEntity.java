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

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record.peer_review.PeerReviewType;
import org.orcid.jaxb.model.record.peer_review.Role;
import org.orcid.utils.OrcidStringUtils;

@Entity
@Table(name = "peer_review")
public class PeerReviewEntity extends BaseEntity<Long> implements ProfileAware, SourceAware {
    
    private static final long serialVersionUID = -172752706595347541L;
    private Long id;
    private ProfileEntity profile;
    private Role role;
    private OrgEntity org;
    private String externalIdentifiersJson;
    private String url;
    private PeerReviewType type;
    private CompletionDateEntity completionDate;    
    private SourceEntity source;
    private Visibility visibility;    
    private PeerReviewSubjectEntity subject;
    
    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "peer_review_seq")
    @SequenceGenerator(name = "peer_review_seq", sequenceName = "peer_review_seq")
    public Long getId() {
        return id;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.REFRESH })
    @JoinColumn(name = "org_id", nullable = false)
    public OrgEntity getOrg() {
        return org;
    }

    public void setOrg(OrgEntity org) {
        this.org = org;
    }

    @Column(name = "external_identifiers_json")
    public String getExternalIdentifiersJson() {
        return externalIdentifiersJson;
    }

    public void setExternalIdentifiersJson(String externalIdentifiersJson) {
        this.externalIdentifiersJson = externalIdentifiersJson;
    }

    @Column(name = "url", length = 350)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Basic
    @Enumerated(EnumType.STRING)    
    public PeerReviewType getType() {
        return type;
    }

    public void setType(PeerReviewType type) {
        this.type = type;
    }

    public CompletionDateEntity getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(CompletionDateEntity completionDate) {
        this.completionDate = completionDate;
    }

    public SourceEntity getSource() {
        return source;
    }

    public void setSource(SourceEntity source) {
        this.source = source;
    }

    @Basic
    @Enumerated(EnumType.STRING)
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
    @ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "orcid", nullable = false)
    public ProfileEntity getProfile() {
        return profile;
    }  
    
    @ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "peer_review_subject_id", nullable = false)
    public PeerReviewSubjectEntity getSubject() {
        return subject;
    }

    public void setSubject(PeerReviewSubjectEntity subject) {
        this.subject = subject;
    }

    public int compareTo(PeerReviewEntity other) {        
        if (other == null) {
            throw new NullPointerException("Can't compare with null");
        }
        
        int compareSubject = subject.compareTo(other.getSubject());
        if(compareSubject != 0) {
            return compareSubject;
        }
        
        int urlCompare = OrcidStringUtils.compareStrings(url, other.getUrl());
        if(urlCompare != 0) {
            return urlCompare;
        }
        
        return 0;
    }
    
    public void clean() {
        externalIdentifiersJson = null;
        url = null;
        type = null;
        completionDate = null;
        subject = null;
        visibility = null;
    }
}
