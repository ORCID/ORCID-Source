package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.message.CurrencyCode;
import org.orcid.jaxb.model.message.DisambiguatedOrganization;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingContributors;
import org.orcid.jaxb.model.message.FundingExternalIdentifier;
import org.orcid.jaxb.model.message.FundingExternalIdentifiers;
import org.orcid.jaxb.model.message.FundingType;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.Url;


public class FundingForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    
    private Text title;

    private Text description;
    
    private Text fundingName;
    
    private Text fundingType;
    
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
    
    private Text disambiguatedFundingSourceId;

    private Text disambiguationSource;
    
    private String fundingTypeForDisplay;

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

	public Text getTitle() {
		return title;
	}

	public void setTitle(Text title) {
		this.title = title;
	}

	public Text getDescription() {
		return description;
	}

	public void setDescription(Text description) {
		this.description = description;
	}

	public Text getFundingName() {
		return fundingName;
	}

	public void setFundingName(Text fundingName) {
		this.fundingName = fundingName;
	}

	public Text getFundingType() {
		return fundingType;
	}

	public void setFundingType(Text fundingType) {
		this.fundingType = fundingType;
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
	
	public Text getDisambiguatedFundingSourceId() {
		return disambiguatedFundingSourceId;
	}

	public void setDisambiguatedFundingSourceId(
			Text disambiguatedFundingSourceId) {
		this.disambiguatedFundingSourceId = disambiguatedFundingSourceId;
	}

	public Text getDisambiguationSource() {
		return disambiguationSource;
	}

	public void setDisambiguationSource(Text disambiguationSource) {
		this.disambiguationSource = disambiguationSource;
	}	
	
	public String getFundingTypeForDisplay() {
		return fundingTypeForDisplay;
	}

	public void setFundingTypeForDisplay(String fundingTypeForDisplay) {
		this.fundingTypeForDisplay = fundingTypeForDisplay;
	}

	public Funding toFunding() {
		Funding result = new Funding();
		if(!PojoUtil.isEmpty(amount))
			result.setAmount(amount.getValue());
		if(!PojoUtil.isEmpty(currencyCode))
			result.setCurrencyCode(CurrencyCode.valueOf(currencyCode.getValue()));
		if(!PojoUtil.isEmpty(description))
			result.setDescription(description.getValue());
		if(!PojoUtil.isEmpty(endDate))
			result.setEndDate(endDate.toFuzzyDate());
				
		if(contributors != null && !contributors.isEmpty()) {
			FundingContributors fContributors = new FundingContributors();  
			for(Contributor contributor : contributors){
				fContributors.getContributor().add(contributor.toContributor());
			}
			result.setFundingContributors(fContributors);
		}
		
		if(externalIdentifiers != null && !externalIdentifiers.isEmpty()) {
			FundingExternalIdentifiers fExternalIdentifiers = new FundingExternalIdentifiers(); 
			for(FundingExternalIdentifierForm fExternalIdentifier : externalIdentifiers) {
				fExternalIdentifiers.getFundingExternalIdentifier().add(fExternalIdentifier.toFundingExternalIdentifier());
			}
			result.setFundingExternalIdentifiers(fExternalIdentifiers);
		}
		
		Organization organization = new Organization();
		if(!PojoUtil.isEmpty(fundingName))
			organization.setName(fundingName.getValue());
		if (!PojoUtil.isEmpty(disambiguatedFundingSourceId)) {
            organization.setDisambiguatedOrganization(new DisambiguatedOrganization());
            organization.getDisambiguatedOrganization().setDisambiguatedOrganizationIdentifier(disambiguatedFundingSourceId.getValue());
            organization.getDisambiguatedOrganization().setDisambiguationSource(disambiguationSource.getValue());
        }
		
		result.setOrganization(organization);
		
		if(!PojoUtil.isEmpty(putCode))
			result.setPutCode(putCode.getValue());
		if(!PojoUtil.isEmpty(startDate))
			result.setStartDate(startDate.toFuzzyDate());
		if(!PojoUtil.isEmpty(title))
			result.setTitle(title.getValue());
		if(!PojoUtil.isEmpty(fundingType))
			result.setType(FundingType.valueOf(fundingType.getValue()));
		if(!PojoUtil.isEmpty(url))
			result.setUrl(new Url(url.getValue()));
		if(visibility != null)
			result.setVisibility(visibility.getVisibility());
		return result;
	}
	
	public static FundingForm valueOf(Funding funding) {
		FundingForm result = new FundingForm();
		
		if(StringUtils.isNotEmpty(funding.getPutCode()))
			result.setPutCode(Text.valueOf(funding.getPutCode()));
		
		if(StringUtils.isNotEmpty(funding.getAmount()))
			result.setAmount(Text.valueOf(funding.getAmount()));
		if(funding.getCurrencyCode() != null)
			result.setCurrencyCode(Text.valueOf(funding.getCurrencyCode().value()));
		if(StringUtils.isNotEmpty(funding.getDescription()))
			result.setDescription(Text.valueOf(funding.getDescription()));
		
		if(funding.getEndDate() != null)
			result.setEndDate(Date.valueOf(funding.getEndDate()));
		
		if(funding.getType() != null)
			result.setFundingType(Text.valueOf(funding.getType().value()));
				
		Source source = funding.getSource();
		if(source != null && source.getSourceName() != null)
			result.setSourceName(source.getSourceName().getContent());
		
		if(funding.getStartDate() != null)
			result.setStartDate(Date.valueOf(funding.getStartDate()));
		
		if(StringUtils.isNotEmpty(funding.getTitle()))
			result.setTitle(Text.valueOf(funding.getTitle()));
		
		if(funding.getUrl() != null)
			result.setUrl(Text.valueOf(funding.getUrl().getValue()));
		
		if(funding.getVisibility() != null)
		result.setVisibility(Visibility.valueOf(funding.getVisibility()));
		
		// Set the disambiguated organization
		Organization organization = funding.getOrganization();
		result.setFundingName(Text.valueOf(organization.getName()));
		DisambiguatedOrganization disambiguatedOrganization = organization.getDisambiguatedOrganization(); 
		if(disambiguatedOrganization != null) {
			if(StringUtils.isNotEmpty(disambiguatedOrganization.getDisambiguatedOrganizationIdentifier())) {
				result.setDisambiguatedFundingSourceId(Text.valueOf(disambiguatedOrganization.getDisambiguatedOrganizationIdentifier()));
				result.setDisambiguationSource(Text.valueOf(disambiguatedOrganization.getDisambiguationSource()));
			}			
		}
		
		// Set contributors
		if(funding.getFundingContributors() != null) {
			List<Contributor> contributors = new ArrayList<Contributor> ();
			for(org.orcid.jaxb.model.message.Contributor fContributor : funding.getFundingContributors().getContributor()) {
				Contributor contributor = Contributor.valueOf(fContributor);
				contributors.add(contributor);
			}
			result.setContributors(contributors);
		}
		
		// Set external identifiers
		if(funding.getFundingExternalIdentifiers() != null) {
			List<FundingExternalIdentifierForm> externalIdentifiersList = new ArrayList<FundingExternalIdentifierForm>();
			for(FundingExternalIdentifier fExternalIdentifier : funding.getFundingExternalIdentifiers().getFundingExternalIdentifier()){
				FundingExternalIdentifierForm fundingExternalIdentifierForm = FundingExternalIdentifierForm.valueOf(fExternalIdentifier);
				externalIdentifiersList.add(fundingExternalIdentifierForm);
			}			
			result.setExternalIdentifiers(externalIdentifiersList);
		}
		
		return result;
	}
}