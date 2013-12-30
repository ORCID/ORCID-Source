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
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.message.Amount;
import org.orcid.jaxb.model.message.CurrencyCode;
import org.orcid.jaxb.model.message.DisambiguatedOrganization;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.FundingContributors;
import org.orcid.jaxb.model.message.FundingExternalIdentifier;
import org.orcid.jaxb.model.message.FundingType;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidFunding;
import org.orcid.jaxb.model.message.FundingExternalIdentifiers;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.message.OrganizationAddress;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.Url;


public class GrantForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    
    private GrantTitleForm grantTitle;     

    private Text description;
    
    private Text grantName;
    
    private Text grantType;
    
    private Text currencyCode;
    
    private Text amount;
    
    private Text url;
    
    private Date startDate;

    private Date endDate;    
    
    private List<Contributor> contributors;
    
    private List<FundingExternalIdentifierForm> externalIdentifiers;
    
    private Text putCode;

    private Visibility visibility; 
    
    private String sourceName;
    
    private Text disambiguatedGrantSourceId;

    private Text disambiguationSource;
    
    private Text city;

    private Text region;

    private Text country;
    
    private String countryForDisplay;
    
    private String grantTypeForDisplay;

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
	public Text getPutCode() {
		return putCode;
	}

	public void setPutCode(Text putCode) {
		this.putCode = putCode;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	public GrantTitleForm getGrantTitle() {
		return grantTitle;
	}

	public void setGrantTitle(GrantTitleForm title) {
		this.grantTitle = title;
	}

	public Text getDescription() {
		return description;
	}

	public void setDescription(Text description) {
		this.description = description;
	}

	public Text getGrantName() {
		return grantName;
	}

	public void setGrantName(Text grantName) {
		this.grantName = grantName;
	}

	public Text getGrantType() {
		return grantType;
	}

	public void setGrantType(Text grantType) {
		this.grantType = grantType;
	}

	public Text getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(Text currencyCode) {
		this.currencyCode = currencyCode;
	}

	public Text getAmount() {
		return amount;
	}

	public void setAmount(Text amount) {
		this.amount = amount;
	}

	public Text getUrl() {
		return url;
	}

	public void setUrl(Text url) {
		this.url = url;
	}

	public List<Contributor> getContributors() {
		return contributors;
	}

	public void setContributors(List<Contributor> contributors) {
		this.contributors = contributors;
	}	

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
    
	public List<FundingExternalIdentifierForm> getExternalIdentifiers() {
		return externalIdentifiers;
	}

	public void setExternalIdentifiers(
			List<FundingExternalIdentifierForm> externalIdentifiers) {
		this.externalIdentifiers = externalIdentifiers;
	}		
	
	public Text getDisambiguatedGrantSourceId() {
		return disambiguatedGrantSourceId;
	}

	public void setDisambiguatedGrantSourceId(
			Text disambiguatedGrantSourceId) {
		this.disambiguatedGrantSourceId = disambiguatedGrantSourceId;
	}

	public Text getDisambiguationSource() {
		return disambiguationSource;
	}

	public void setDisambiguationSource(Text disambiguationSource) {
		this.disambiguationSource = disambiguationSource;
	}	
	
	public String getGrantTypeForDisplay() {
		return grantTypeForDisplay;
	}

	public void setGrantTypeForDisplay(String grantTypeForDisplay) {
		this.grantTypeForDisplay = grantTypeForDisplay;
	}
	
	public Text getCity() {
		return city;
	}

	public void setCity(Text city) {
		this.city = city;
	}

	public Text getRegion() {
		return region;
	}

	public void setRegion(Text region) {
		this.region = region;
	}

	public Text getCountry() {
		return country;
	}

	public void setCountry(Text country) {
		this.country = country;
	}

	public String getCountryForDisplay() {
        return countryForDisplay;
    }

    public void setCountryForDisplay(String countryForDisplay) {
        this.countryForDisplay = countryForDisplay;
    }
    
	public OrcidFunding toOrcidGrant() {
		OrcidFunding result = new OrcidFunding();
		Amount orcidAmount = new Amount();
		if(!PojoUtil.isEmpty(amount))
			orcidAmount.setContent(amount.getValue());
		if(!PojoUtil.isEmpty(currencyCode))
			orcidAmount.setCurrencyCode(CurrencyCode.valueOf(currencyCode.getValue()));		
		result.setAmount(orcidAmount);
		if(!PojoUtil.isEmpty(description))
			result.setDescription(description.getValue());
		if(!PojoUtil.isEmpty(startDate))
			result.setStartDate(new FuzzyDate(startDate.toFuzzyDate()));
		if(!PojoUtil.isEmpty(endDate))
			result.setEndDate(new FuzzyDate(endDate.toFuzzyDate()));
		if(!PojoUtil.isEmpty(putCode))
			result.setPutCode(putCode.getValue());		
		if(grantTitle != null) {						
			result.setTitle(grantTitle.toGrantTitle());
		}				
		if(!PojoUtil.isEmpty(grantType))
			result.setType(FundingType.fromValue(grantType.getValue()));
		if(!PojoUtil.isEmpty(url))
			result.setUrl(new Url(url.getValue()));	
		else 
			result.setUrl(new Url(""));
		if(visibility != null)
			result.setVisibility(visibility.getVisibility());		
		
		// Set Organization
		Organization organization = new Organization();
		if(!PojoUtil.isEmpty(grantName))
			organization.setName(grantName.getValue());		
        OrganizationAddress organizationAddress = new OrganizationAddress();
        organization.setAddress(organizationAddress);
        organizationAddress.setCity(city.getValue());
        if (!PojoUtil.isEmpty(region)) {
            organizationAddress.setRegion(region.getValue());
        }  
        if (!PojoUtil.isEmpty(country)) {
            organizationAddress.setCountry(Iso3166Country.fromValue(country.getValue()));
        } 
		if (!PojoUtil.isEmpty(disambiguatedGrantSourceId)) {
            organization.setDisambiguatedOrganization(new DisambiguatedOrganization());
            organization.getDisambiguatedOrganization().setDisambiguatedOrganizationIdentifier(disambiguatedGrantSourceId.getValue());
            organization.getDisambiguatedOrganization().setDisambiguationSource(disambiguationSource.getValue());
        }		
		result.setOrganization(organization);
		
		// Set contributors
		if(contributors != null && !contributors.isEmpty()) {
			FundingContributors gContributors = new FundingContributors();  
			for(Contributor contributor : contributors){
				if(!PojoUtil.isEmtpy(contributor))
					gContributors.getContributor().add(contributor.toContributor());
			}
			result.setFundingContributors(gContributors);
		}
		// Set external identifiers
		if(externalIdentifiers != null && !externalIdentifiers.isEmpty()) {
			FundingExternalIdentifiers fExternalIdentifiers = new FundingExternalIdentifiers(); 
			for(FundingExternalIdentifierForm fExternalIdentifier : externalIdentifiers) {
				if(!PojoUtil.isEmtpy(fExternalIdentifier))
					fExternalIdentifiers.getFundingExternalIdentifier().add(fExternalIdentifier.toGrantExternalIdentifier());
			}
			result.setFundingExternalIdentifiers(fExternalIdentifiers);
		}				
		
		return result;
	}
	
	public static GrantForm valueOf(OrcidFunding funding) {
		GrantForm result = new GrantForm();
		
		if(StringUtils.isNotEmpty(funding.getPutCode()))
			result.setPutCode(Text.valueOf(funding.getPutCode()));
		
		if(funding.getAmount() != null) {			
			if(StringUtils.isNotEmpty(funding.getAmount().getContent()))
				result.setAmount(Text.valueOf(funding.getAmount().getContent()));
			if(funding.getAmount().getCurrencyCode() != null)
				result.setCurrencyCode(Text.valueOf(funding.getAmount().getCurrencyCode().value()));
		}
		if(StringUtils.isNotEmpty(funding.getDescription()))
			result.setDescription(Text.valueOf(funding.getDescription()));
		
		if(funding.getEndDate() != null)
			result.setEndDate(Date.valueOf(funding.getEndDate()));
		
		if(funding.getType() != null)
			result.setGrantType(Text.valueOf(funding.getType().value()));
				
		Source source = funding.getSource();
		if(source != null && source.getSourceName() != null)
			result.setSourceName(source.getSourceName().getContent());
		
		if(funding.getStartDate() != null)
			result.setStartDate(Date.valueOf(funding.getStartDate()));
		
		if(funding.getTitle() != null) {
			GrantTitleForm grantTitle = new GrantTitleForm();
			if(funding.getTitle().getTitle() != null)
				grantTitle.setTitle(Text.valueOf(funding.getTitle().getTitle().getContent()));
			if(funding.getTitle().getTranslatedTitle() != null){
				TranslatedTitle translatedTitle = new TranslatedTitle();
				translatedTitle.setContent(funding.getTitle().getTranslatedTitle().getContent());
				translatedTitle.setLanguageCode(funding.getTitle().getTranslatedTitle().getLanguageCode());
				grantTitle.setTranslatedTitle(translatedTitle);
			}
			result.setGrantTitle(grantTitle);
		}
		
		if(funding.getUrl() != null)
			result.setUrl(Text.valueOf(funding.getUrl().getValue()));
		
		if(funding.getVisibility() != null)
		result.setVisibility(Visibility.valueOf(funding.getVisibility()));
		
		// Set the disambiguated organization
		Organization organization = funding.getOrganization();
		result.setGrantName(Text.valueOf(organization.getName()));		
		DisambiguatedOrganization disambiguatedOrganization = organization.getDisambiguatedOrganization(); 
		if(disambiguatedOrganization != null) {
			if(StringUtils.isNotEmpty(disambiguatedOrganization.getDisambiguatedOrganizationIdentifier())) {
				result.setDisambiguatedGrantSourceId(Text.valueOf(disambiguatedOrganization.getDisambiguatedOrganizationIdentifier()));
				result.setDisambiguationSource(Text.valueOf(disambiguatedOrganization.getDisambiguationSource()));
			}			
		}
		OrganizationAddress organizationAddress = organization.getAddress();
		if(organizationAddress != null) {
			if(!PojoUtil.isEmpty(organizationAddress.getCity()))
				result.setCity(Text.valueOf(organizationAddress.getCity()));
			if(!PojoUtil.isEmpty(organizationAddress.getRegion()))
				result.setRegion(Text.valueOf(organizationAddress.getRegion()));	
			if(organizationAddress.getCountry() != null)
				result.setCountry(Text.valueOf(organizationAddress.getCountry().value()));
		}
		
		// Set contributors
		if(funding.getFundingContributors() != null) {
			List<Contributor> contributors = new ArrayList<Contributor> ();
			for(org.orcid.jaxb.model.message.Contributor gContributor : funding.getFundingContributors().getContributor()) {
				Contributor contributor = Contributor.valueOf(gContributor);
				contributors.add(contributor);
			}
			result.setContributors(contributors);
		}
		
		// Set external identifiers
		if(funding.getFundingExternalIdentifiers() != null) {
			List<FundingExternalIdentifierForm> externalIdentifiersList = new ArrayList<FundingExternalIdentifierForm>();
			for(FundingExternalIdentifier fExternalIdentifier : funding.getFundingExternalIdentifiers().getFundingExternalIdentifier()){
				FundingExternalIdentifierForm grantExternalIdentifierForm = FundingExternalIdentifierForm.valueOf(fExternalIdentifier);
				externalIdentifiersList.add(grantExternalIdentifierForm);
			}			
			result.setExternalIdentifiers(externalIdentifiersList);
		}
		
		return result;
	}
}