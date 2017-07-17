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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the salesforce database table.
 * 
 */
@Entity
@Table(name = "salesforce_connection")
public class SalesForceConnectionEntity extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String orcid;
    private String email;
    private String salesForceAccountId;

    public SalesForceConnectionEntity() {
        super();
    }

    public SalesForceConnectionEntity(String orcid, String email, String salesForceAccountId) {
        this.orcid = orcid;
        this.email = email;
        this.salesForceAccountId = salesForceAccountId;
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "salesforce_connection_seq")
    @SequenceGenerator(name = "salesforce_connection_seq", sequenceName = "salesforce_connection_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "salesforce_account_id")
    public String getSalesForceAccountId() {
        return salesForceAccountId;
    }

    public void setSalesForceAccountId(String salesForceAccountId) {
        this.salesForceAccountId = salesForceAccountId;
    }

}