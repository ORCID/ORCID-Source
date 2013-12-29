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
import org.orcid.jaxb.model.message.CurrencyCode;
import org.orcid.jaxb.model.message.DisambiguatedOrganization;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.GrantContributors;
import org.orcid.jaxb.model.message.GrantExternalIdentifier;
import org.orcid.jaxb.model.message.GrantTitle;
import org.orcid.jaxb.model.message.GrantType;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidGrant;
import org.orcid.jaxb.model.message.OrcidGrantExternalIdentifiers;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.message.OrganizationAddress;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.Title;
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
    
    private List<GrantExternalIdentifierForm> externalIdentifiers;
    
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
    
	public List<GrantExternalIdentifierForm> getExternalIdentifiers() {
		return externalIdentifiers;
	}

	public void setExternalIdentifiers(
			List<GrantExternalIdentifierForm> externalIdentifiers) {
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
    
	public OrcidGrant toOrcidGrant() {
		OrcidGrant result = new OrcidGrant();
		if(!PojoUtil.isEmpty(amount))
			result.setAmount(amount.getValue());
		if(!PojoUtil.isEmpty(currencyCode))
			result.setCurrencyCode(CurrencyCode.valueOf(currencyCode.getValue()));
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
			result.setType(GrantType.fromValue(grantType.getValue()));
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
			GrantContributors gContributors = new GrantContributors();  
			for(Contributor contributor : contributors){
				if(!PojoUtil.isEmtpy(contributor))
					gContributors.getContributor().add(contributor.toContributor());
			}
			result.setGrantContributors(gContributors);
		}
		// Set external identifiers
		if(externalIdentifiers != null && !externalIdentifiers.isEmpty()) {
			OrcidGrantExternalIdentifiers gExternalIdentifiers = new OrcidGrantExternalIdentifiers(); 
			for(GrantExternalIdentifierForm gExternalIdentifier : externalIdentifiers) {
				if(!PojoUtil.isEmtpy(gExternalIdentifier))
					gExternalIdentifiers.getGrantExternalIdentifier().add(gExternalIdentifier.toGrantExternalIdentifier());
			}
			result.setGrantExternalIdentifiers(gExternalIdentifiers);
		}				
		
		return result;
	}
	
	public static GrantForm valueOf(OrcidGrant grant) {
		GrantForm result = new GrantForm();
		
		if(StringUtils.isNotEmpty(grant.getPutCode()))
			result.setPutCode(Text.valueOf(grant.getPutCode()));
		
		if(StringUtils.isNotEmpty(grant.getAmount()))
			result.setAmount(Text.valueOf(grant.getAmount()));
		if(grant.getCurrencyCode() != null)
			result.setCurrencyCode(Text.valueOf(grant.getCurrencyCode().value()));
		if(StringUtils.isNotEmpty(grant.getDescription()))
			result.setDescription(Text.valueOf(grant.getDescription()));
		
		if(grant.getEndDate() != null)
			result.setEndDate(Date.valueOf(grant.getEndDate()));
		
		if(grant.getType() != null)
			result.setGrantType(Text.valueOf(grant.getType().value()));
				
		Source source = grant.getSource();
		if(source != null && source.getSourceName() != null)
			result.setSourceName(source.getSourceName().getContent());
		
		if(grant.getStartDate() != null)
			result.setStartDate(Date.valueOf(grant.getStartDate()));
		
		if(grant.getGrantTitle() != null) {
			GrantTitleForm grantTitle = new GrantTitleForm();
			if(grant.getGrantTitle().getTitle() != null)
				grantTitle.setTitle(Text.valueOf(grant.getGrantTitle().getTitle().getContent()));
			if(grant.getGrantTitle().getTranslatedTitle() != null){
				TranslatedTitle translatedTitle = new TranslatedTitle();
				translatedTitle.setContent(grant.getGrantTitle().getTranslatedTitle().getContent());
				translatedTitle.setLanguageCode(grant.getGrantTitle().getTranslatedTitle().getLanguageCode());
				grantTitle.setTranslatedTitle(translatedTitle);
			}
			result.setGrantTitle(grantTitle);
		}
		
		if(grant.getUrl() != null)
			result.setUrl(Text.valueOf(grant.getUrl().getValue()));
		
		if(grant.getVisibility() != null)
		result.setVisibility(Visibility.valueOf(grant.getVisibility()));
		
		// Set the disambiguated organization
		Organization organization = grant.getOrganization();
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
		if(grant.getGrantContributors() != null) {
			List<Contributor> contributors = new ArrayList<Contributor> ();
			for(org.orcid.jaxb.model.message.Contributor gContributor : grant.getGrantContributors().getContributor()) {
				Contributor contributor = Contributor.valueOf(gContributor);
				contributors.add(contributor);
			}
			result.setContributors(contributors);
		}
		
		// Set external identifiers
		if(grant.getGrantExternalIdentifiers() != null) {
			List<GrantExternalIdentifierForm> externalIdentifiersList = new ArrayList<GrantExternalIdentifierForm>();
			for(GrantExternalIdentifier fExternalIdentifier : grant.getGrantExternalIdentifiers().getGrantExternalIdentifier()){
				GrantExternalIdentifierForm grantExternalIdentifierForm = GrantExternalIdentifierForm.valueOf(fExternalIdentifier);
				externalIdentifiersList.add(grantExternalIdentifierForm);
			}			
			result.setExternalIdentifiers(externalIdentifiersList);
		}
		
		return result;
	}
}