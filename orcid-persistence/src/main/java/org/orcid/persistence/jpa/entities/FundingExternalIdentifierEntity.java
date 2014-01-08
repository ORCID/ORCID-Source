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
 * 
 * @author Angel Montenegro
 * 
 */
@Entity
@Table(name = "funding_external_identifier")
public class FundingExternalIdentifierEntity extends BaseEntity<Long> implements Comparable<FundingExternalIdentifierEntity> {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private ProfileFundingEntity profileFunding;
	private String type;
	private String value;
	private String url;
	
	@Id
	@Column(name="funding_external_identifier_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "funding_external_identifier_seq")
    @SequenceGenerator(name = "funding_external_identifier_seq", sequenceName = "funding_external_identifier_seq")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_funding_id")
	public ProfileFundingEntity getProfileFunding() {
		return profileFunding;
	}
	
	public void setProfileFunding(ProfileFundingEntity profileFunding) {
		this.profileFunding = profileFunding;
	}
	
	@Column(name="ext_type")	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name="ext_value")
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	@Column(name="ext_url")
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public int compareTo(FundingExternalIdentifierEntity other) {
		 if (other == null) {
	            return -1;
	       }
		 
		 if(type != null) {
			 if(other.getType() == null)
				 return 1;
			 if(!type.equals(other.getType())) {
				 return type.compareTo(other.getType());
			 }
		 }
		 
		 if(value != null) {
			 if(other.getValue() == null)
				 return 1;
			 if(!value.equals(other.getValue())){
				 return value.compareTo(other.getValue());
			 }
		 }
		 
		 if(url != null) {
			 if(other.getUrl() == null)
				 return 1;
			 if(!url.equals(other.getUrl())) {
				 return url.compareTo(other.getValue());
			 }
		 }
		 
		 return 0;
	}
	
}
