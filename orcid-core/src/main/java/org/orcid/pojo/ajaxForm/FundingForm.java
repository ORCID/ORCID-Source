package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.v3.rc1.common.Amount;
import org.orcid.jaxb.model.v3.rc1.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.rc1.common.FuzzyDate;
import org.orcid.jaxb.model.v3.rc1.common.Iso3166Country;
import org.orcid.jaxb.model.v3.rc1.common.Organization;
import org.orcid.jaxb.model.v3.rc1.common.OrganizationAddress;
import org.orcid.jaxb.model.v3.rc1.common.OrganizationDefinedFundingSubType;
import org.orcid.jaxb.model.v3.rc1.common.Source;
import org.orcid.jaxb.model.v3.rc1.common.Url;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc1.record.Funding;
import org.orcid.jaxb.model.v3.rc1.record.FundingContributor;
import org.orcid.jaxb.model.v3.rc1.record.FundingContributors;
import org.orcid.jaxb.model.v3.rc1.record.FundingType;

public class FundingForm extends VisibilityForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private FundingTitleForm fundingTitle;

    private Text description;

    private Text fundingName;

    private Text fundingType;
    
    private OrgDefinedFundingSubType organizationDefinedFundingSubType;

    private Text currencyCode;

    private Text amount;

    private Text url;

    private Date startDate;

    private Date endDate;

    private List<Contributor> contributors;

    private List<FundingExternalIdentifierForm> externalIdentifiers;

    private Text putCode;

    private String sourceName;
    
    private String source;

    private Text disambiguatedFundingSourceId;

    private Text disambiguationSource;

    private Text city;

    private Text region;

    private Text country;

    private String countryForDisplay;

    private String fundingTypeForDisplay;
    
    private String dateSortString;  
    
    private Date createdDate;
    
    private Date lastModified;


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

    public FundingTitleForm getFundingTitle() {
        return fundingTitle;
    }

    public void setFundingTitle(FundingTitleForm title) {
        this.fundingTitle = title;
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

    public OrgDefinedFundingSubType getOrganizationDefinedFundingSubType() {
        return organizationDefinedFundingSubType;
    }

    public void setOrganizationDefinedFundingSubType(OrgDefinedFundingSubType organizationDefinedFundingSubType) {
        this.organizationDefinedFundingSubType = organizationDefinedFundingSubType;
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

    public void setExternalIdentifiers(List<FundingExternalIdentifierForm> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    public Text getDisambiguatedFundingSourceId() {
        return disambiguatedFundingSourceId;
    }

    public void setDisambiguatedFundingSourceId(Text disambiguatedFundingSourceId) {
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

    public Funding toFunding() {
        Funding result = new Funding();
        Amount orcidAmount = new Amount();
        if (!PojoUtil.isEmpty(amount))
            orcidAmount.setContent(amount.getValue());
        if (!PojoUtil.isEmpty(currencyCode))
            orcidAmount.setCurrencyCode(currencyCode.getValue());
        result.setAmount(orcidAmount);
        if (!PojoUtil.isEmpty(description))
            result.setDescription(description.getValue());
        if (!PojoUtil.isEmpty(startDate))
            result.setStartDate(new FuzzyDate(startDate.toFuzzyDate()));
        if (!PojoUtil.isEmpty(endDate))
            result.setEndDate(new FuzzyDate(endDate.toFuzzyDate()));
        if (!PojoUtil.isEmpty(putCode))
            result.setPutCode(Long.valueOf(putCode.getValue()));
        if (fundingTitle != null) {
            result.setTitle(fundingTitle.toFundingTitle());
        }
        if (!PojoUtil.isEmpty(fundingType))
            result.setType(FundingType.fromValue(fundingType.getValue()));
        
        if(organizationDefinedFundingSubType != null && !PojoUtil.isEmpty(organizationDefinedFundingSubType.getSubtype()))
            result.setOrganizationDefinedType(new OrganizationDefinedFundingSubType(organizationDefinedFundingSubType.getSubtype().getValue()));
        
        if (!PojoUtil.isEmpty(url))
            result.setUrl(new Url(url.getValue()));
        else
            result.setUrl(new Url());
        if (visibility != null)
            result.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.fromValue(visibility.getVisibility().value()));

        // Set Organization
        Organization organization = new Organization();
        if (!PojoUtil.isEmpty(fundingName))
            organization.setName(fundingName.getValue());
        OrganizationAddress organizationAddress = new OrganizationAddress();
        organization.setAddress(organizationAddress);
        if (!PojoUtil.isEmpty(city))
            organizationAddress.setCity(city.getValue());
        if (!PojoUtil.isEmpty(region)) {
            organizationAddress.setRegion(region.getValue());
        }
        if (!PojoUtil.isEmpty(country)) {
            organizationAddress.setCountry(Iso3166Country.fromValue(country.getValue()));
        }
        if (!PojoUtil.isEmpty(disambiguatedFundingSourceId)) {
            organization.setDisambiguatedOrganization(new DisambiguatedOrganization());
            organization.getDisambiguatedOrganization().setDisambiguatedOrganizationIdentifier(disambiguatedFundingSourceId.getValue());
            organization.getDisambiguatedOrganization().setDisambiguationSource(disambiguationSource.getValue());
        }
        result.setOrganization(organization);

        // Set contributors
        if (contributors != null && !contributors.isEmpty()) {
            FundingContributors fContributors = new FundingContributors();
            for (Contributor contributor : contributors) {
                if (!PojoUtil.isEmtpy(contributor))
                    fContributors.getContributor().add(contributor.toFundingContributor());
            }
            result.setContributors(fContributors);
        }
        // Set external identifiers
        if (externalIdentifiers != null && !externalIdentifiers.isEmpty()) {
            ExternalIDs fExternalIdentifiers = new ExternalIDs();
            for (FundingExternalIdentifierForm fExternalIdentifier : externalIdentifiers) {
                if (!PojoUtil.isEmtpy(fExternalIdentifier))
                    fExternalIdentifiers.getExternalIdentifier().add(fExternalIdentifier.toFundingExternalIdentifier());
            }
            result.setExternalIdentifiers(fExternalIdentifiers);
        }

        return result;
    }

    public static FundingForm valueOf(Funding funding) {
        FundingForm result = new FundingForm();

        result.setDateSortString(PojoUtil.createDateSortString(funding.getStartDate(), funding.getEndDate()));

        if (funding.getPutCode() != null)
            result.setPutCode(Text.valueOf(funding.getPutCode()));

        if (funding.getAmount() != null) {
            if (StringUtils.isNotEmpty(funding.getAmount().getContent())) {
                String cleanNumber = funding.getAmount().getContent().trim();                                
                result.setAmount(Text.valueOf(cleanNumber));
            }
            if (funding.getAmount().getCurrencyCode() != null)
                result.setCurrencyCode(Text.valueOf(funding.getAmount().getCurrencyCode()));
            else 
                result.setCurrencyCode(new Text());
        } else {
            result.setAmount(new Text());
            result.setCurrencyCode(new Text());
        }
        if (StringUtils.isNotEmpty(funding.getDescription()))
            result.setDescription(Text.valueOf(funding.getDescription()));
        else
            result.setDescription(new Text());

        if (funding.getStartDate() != null)
            result.setStartDate(Date.valueOf(funding.getStartDate()));

        if (funding.getEndDate() != null)
            result.setEndDate(Date.valueOf(funding.getEndDate()));        

        if (funding.getType() != null)
            result.setFundingType(Text.valueOf(funding.getType().value()));
        else 
            result.setFundingType(new Text());
        
        if(funding.getOrganizationDefinedType() != null) {
            OrgDefinedFundingSubType OrgDefinedFundingSubType = new OrgDefinedFundingSubType();
            OrgDefinedFundingSubType.setSubtype(Text.valueOf(funding.getOrganizationDefinedType().getContent()));
            OrgDefinedFundingSubType.setAlreadyIndexed(false);
            result.setOrganizationDefinedFundingSubType(OrgDefinedFundingSubType);
        }            
        
        Source source = funding.getSource();
        if (source != null) {
            result.setSource(source.retrieveSourcePath());            
            if(source.getSourceName() != null) {
                result.setSourceName(source.getSourceName().getContent());
            }
        }
        
        if (funding.getTitle() != null) {
            FundingTitleForm fundingTitle = new FundingTitleForm();
            if (funding.getTitle().getTitle() != null)
                fundingTitle.setTitle(Text.valueOf(funding.getTitle().getTitle().getContent()));
            else
                fundingTitle.setTitle(new Text());
            if (funding.getTitle().getTranslatedTitle() != null) {
                TranslatedTitleForm translatedTitle = new TranslatedTitleForm();
                translatedTitle.setContent(funding.getTitle().getTranslatedTitle().getContent());
                translatedTitle.setLanguageCode(funding.getTitle().getTranslatedTitle().getLanguageCode());
                fundingTitle.setTranslatedTitle(translatedTitle);
            }
            result.setFundingTitle(fundingTitle);
        } else {
            FundingTitleForm fundingTitle = new FundingTitleForm();
            fundingTitle.setTitle(new Text());
            result.setFundingTitle(fundingTitle);
        }

        if (funding.getUrl() != null)
            result.setUrl(Text.valueOf(funding.getUrl().getValue()));
        else
            result.setUrl(new Text());

        if (funding.getVisibility() != null)
            result.setVisibility(Visibility.valueOf(funding.getVisibility()));

        // Set the disambiguated organization
        Organization organization = funding.getOrganization();
        result.setFundingName(Text.valueOf(organization.getName()));
        DisambiguatedOrganization disambiguatedOrganization = organization.getDisambiguatedOrganization();
        if (disambiguatedOrganization != null) {
            if (StringUtils.isNotEmpty(disambiguatedOrganization.getDisambiguatedOrganizationIdentifier())) {
                result.setDisambiguatedFundingSourceId(Text.valueOf(disambiguatedOrganization.getDisambiguatedOrganizationIdentifier()));
                result.setDisambiguationSource(Text.valueOf(disambiguatedOrganization.getDisambiguationSource()));
            }
        }
        OrganizationAddress organizationAddress = organization.getAddress();
        if (organizationAddress != null) {
            if (!PojoUtil.isEmpty(organizationAddress.getCity()))
                result.setCity(Text.valueOf(organizationAddress.getCity()));
            else
                result.setCity(new Text());
            if (!PojoUtil.isEmpty(organizationAddress.getRegion()))
                result.setRegion(Text.valueOf(organizationAddress.getRegion()));
            else 
                result.setRegion(new Text());
            if (organizationAddress.getCountry() != null)
                result.setCountry(Text.valueOf(organizationAddress.getCountry().value()));
            else
                result.setCountry(new Text());
                
        } else {
            result.setCountry(new Text());
            result.setCity(new Text());            
            result.setRegion(new Text());
        }

        // Set contributors
        if (funding.getContributors() != null) {
            List<Contributor> contributors = new ArrayList<Contributor>();
            for (FundingContributor fContributor : funding.getContributors().getContributor()) {
                Contributor contributor = Contributor.valueOf(fContributor);
                contributors.add(contributor);
            }
            result.setContributors(contributors);
        }

        List<FundingExternalIdentifierForm> externalIdentifiersList = new ArrayList<FundingExternalIdentifierForm>();
        // Set external identifiers 
        if (funding.getExternalIdentifiers() != null) {            
            for (ExternalID fExternalIdentifier : funding.getExternalIdentifiers().getExternalIdentifier()) {
                FundingExternalIdentifierForm fundingExternalIdentifierForm = FundingExternalIdentifierForm.valueOf(fExternalIdentifier);
                externalIdentifiersList.add(fundingExternalIdentifierForm);
            }            
        } 
        result.setExternalIdentifiers(externalIdentifiersList);
        
        result.setCreatedDate(Date.valueOf(funding.getCreatedDate()));
        result.setLastModified(Date.valueOf(funding.getLastModifiedDate()));


        return result;
    }    
    
    public String getDateSortString() {
        return dateSortString;
    }

    public void setDateSortString(String dateSortString) {
        this.dateSortString = dateSortString;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}
