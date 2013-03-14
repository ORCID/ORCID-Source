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
package org.orcid.persistence.jpa.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author Declan Newman (declan)
 */
@Entity
@Table(name = "grant_contributor")
public class GrantContributorEntity extends BaseContributorEntity {

    private static final long serialVersionUID = -371826957062237679L;
    private Long id;
    private GrantEntity grant;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "grant_contributor_seq")
    @SequenceGenerator(name = "grant_contributor_seq", sequenceName = "grant_contributor_seq")
    @Column(name = "grant_contributor_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "grant_id", nullable = false)
    public GrantEntity getGrant() {
        return grant;
    }

    public void setGrant(GrantEntity grant) {
        this.grant = grant;
    }

}
