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
 * 
 * @author Angel Montenegro
 * 
 */
@Entity
@Table(name = "funding_external_identifier")
public class FundingExternalIdentifier extends BaseEntity<Long> implements Comparable<FundingExternalIdentifier> {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private OrgFundingRelation orgFunding;
	private String type;
	private String value;
	private String url;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "funding_external_identifier_seq")
    @SequenceGenerator(name = "funding_external_identifier_seq", sequenceName = "funding_external_identifier_seq")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.DETACH, CascadeType.REFRESH })
    @JoinColumn(name = "id", nullable = false)
	public OrgFundingRelation getOrgFunding() {
		return orgFunding;
	}
	
	public void setOrgFunding(OrgFundingRelation orgFunding) {
		this.orgFunding = orgFunding;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public int compareTo(FundingExternalIdentifier o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
