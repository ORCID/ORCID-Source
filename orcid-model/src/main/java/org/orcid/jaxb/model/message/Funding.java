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
package org.orcid.jaxb.model.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}title" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}description"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}type"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}currency-code" minOccurs="0" maxOrrurs="1"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}amount"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}url"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}organization"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}funding-external-identifiers" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}funding-contributors" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.orcid.org/ns/orcid}put-code"/>
 *       &lt;attGroup ref="{http://www.orcid.org/ns/orcid}visibility"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "putCode", "title", "description", "type",
		"currencyCode", "amount", "url", "startDate", "endDate", "organization", "fundingExternalIdentifiers", "fundingContributors", "visibility",
		"source" })
@XmlRootElement(name = "funding")
public class Funding implements Serializable, VisibilityType {

	private final static long serialVersionUID = 1L;

	@XmlElement
	protected String title;
	@XmlElement
	protected String description;
	@XmlElement
	protected FundingType type;
	@XmlElement(name = "currency-code")
	protected CurrencyCode currencyCode;
	@XmlElement
	protected String amount;
	@XmlElement
	protected Url url;
	@XmlElement(name = "start-date")
    protected FuzzyDate startDate;
    @XmlElement(name = "end-date")
    protected FuzzyDate endDate;
	@XmlElement(required = true)
	protected Organization organization;
	@XmlElement(name="funding-external-identifiers")
	protected FundingExternalIdentifiers fundingExternalIdentifiers;
	@XmlElement(name = "funding-contributors")
    protected FundingContributors fundingContributors;
	@XmlAttribute(required = true)
	protected Visibility visibility;
	protected Source source;
	@XmlAttribute(name = "put-code")
	protected String putCode;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public FundingType getType() {
		return type;
	}

	public void setType(FundingType type) {
		this.type = type;
	}

	public CurrencyCode getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(CurrencyCode currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Url getUrl() {
		return url;
	}

	public void setUrl(Url url) {
		this.url = url;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	public String getPutCode() {
		return putCode;
	}

	public void setPutCode(String putCode) {
		this.putCode = putCode;
	}
		
	public FundingExternalIdentifiers getFundingExternalIdentifiers() {
		return fundingExternalIdentifiers;
	}

	public void setFundingExternalIdentifiers(
			FundingExternalIdentifiers fundingExternalIdentifiers) {
		this.fundingExternalIdentifiers = fundingExternalIdentifiers;
	}
	
	public FundingContributors getFundingContributors() {
		return fundingContributors;
	}

	public void setFundingContributors(FundingContributors fundingContributors) {
		this.fundingContributors = fundingContributors;
	}
	/**
     * Gets the value of the startDate property.
     * 
     * @return possible object is {@link FuzzyDate }
     * 
     */
    public FuzzyDate getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *            allowed object is {@link FuzzyDate }
     * 
     */
    public void setStartDate(FuzzyDate value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return possible object is {@link FuzzyDate }
     * 
     */
    public FuzzyDate getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *            allowed object is {@link FuzzyDate }
     * 
     */
    public void setEndDate(FuzzyDate value) {
        this.endDate = value;
    }
    
	/**
	 * 
	 * Note that put-code is not part of hashCode or equals! This is to allow
	 * better de-duplication.
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result
				+ ((currencyCode == null) ? 0 : currencyCode.hashCode());
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result
				+ ((organization == null) ? 0 : organization.hashCode());
		result = prime * result
				+ ((visibility == null) ? 0 : visibility.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((fundingExternalIdentifiers == null) ? 0 : fundingExternalIdentifiers.hashCode());
		result = prime * result + ((fundingContributors == null) ? 0 : fundingContributors.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		return result;
	}

	/**
	 * 
	 * Note that put-code is not part of hashCode or equals! This is to allow
	 * better de-duplication.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Funding other = (Funding) obj;
		if (title == null) {
			if(other.title != null)
				return false;
		} else {
			if(!title.equals(other.title))
				return false;
		}
		if (description == null) {
			if(other.description != null)
				return false;
		} else {
			if(!description.equals(other.description))
				return false;
		}
		if (organization == null) {
			if(other.organization != null)
				return false;
		} else {
			if(!organization.equals(other.organization))
				return false;
		}
		if (fundingExternalIdentifiers != null) {
			if(other.fundingExternalIdentifiers != null)
				return false;
		} else {
			if(!fundingExternalIdentifiers.equals(other.fundingExternalIdentifiers))
				return false;
		}
		if (fundingContributors != null) {
			if(other.fundingContributors != null)
				return false;
		} else {
			if(!fundingContributors.equals(other.fundingContributors))
				return false;
		}
		if (type == null) {
			if(other.type != null)
				return false;
		} else {
			if(!type.equals(other.type))
				return false;
		}
		if (currencyCode == null) {
			if(other.currencyCode != null)
				return false;
		} else {
			if(!currencyCode.equals(other.currencyCode))
				return false;
		}
		if (amount == null) {
			if(other.amount != null)
				return false;
		} else {
			if(!amount.equals(other.amount))
				return false;
		}
		if (url == null) {
			if(other.url != null)
				return false;
		} else {
			if(!url.equals(other.url))
				return false;
		}
		if (visibility == null) {
			if(other.visibility != null)
				return false;
		} else {
			if(!visibility.equals(other.visibility))
				return false;
		}
		if (source == null) {
			if(other.source != null)
				return false;
		} else {
			if(!source.equals(other.source))
				return false;
		}
		if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
		if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
		return true;
	}
}
