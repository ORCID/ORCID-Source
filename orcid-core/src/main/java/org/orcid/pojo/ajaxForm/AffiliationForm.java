package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.jaxb.model.v3.release.common.Organization;
import org.orcid.jaxb.model.v3.release.common.OrganizationAddress;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.record.Affiliation;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.pojo.OrgDisambiguatedExternalIdentifiers;
import org.orcid.utils.OrcidStringUtils;

public class AffiliationForm extends VisibilityForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text putCode;

    private Text affiliationName;

    private Text city;

    private Text region;

    private Text country;

    private Text roleTitle;

    private String countryForDisplay;

    private Text departmentName;

    private Text affiliationType;

    private Text disambiguatedAffiliationSourceId;

    private Text disambiguationSource;
    
    private String orgDisambiguatedCity;
    
    private String orgDisambiguatedCountry;

    private Text orgDisambiguatedId;
    
    private String orgDisambiguatedName;
    
    private String orgDisambiguatedRegion;
    
    private String orgDisambiguatedUrl;

    private String affiliationTypeForDisplay;

    private Date startDate;

    private Date endDate;

    private String sourceName;

    private String source;
    
    private String assertionOriginOrcid;
    
    private String assertionOriginClientId;
    
    private String assertionOriginName;

    private String dateSortString;

    private Date createdDate;

    private Date lastModified;

    private Text url;

    private List<OrgDisambiguatedExternalIdentifiers> orgDisambiguatedExternalIdentifiers;

    private List<ActivityExternalIdentifier> affiliationExternalIdentifiers;

    public static AffiliationForm valueOf(AffiliationSummary summary) {
        AffiliationForm form = new AffiliationForm();
        
        if(summary instanceof DistinctionSummary) {
            form.setAffiliationType(Text.valueOf(AffiliationType.DISTINCTION.value()));
        } else if (summary instanceof EducationSummary) {
            form.setAffiliationType(Text.valueOf(AffiliationType.EDUCATION.value()));
        } else if(summary instanceof EmploymentSummary) {
            form.setAffiliationType(Text.valueOf(AffiliationType.EMPLOYMENT.value()));
        } else if(summary instanceof InvitedPositionSummary) {
            form.setAffiliationType(Text.valueOf(AffiliationType.INVITED_POSITION.value()));
        } else if(summary instanceof MembershipSummary) {
            form.setAffiliationType(Text.valueOf(AffiliationType.MEMBERSHIP.value()));
        } else if(summary instanceof QualificationSummary) {
            form.setAffiliationType(Text.valueOf(AffiliationType.QUALIFICATION.value()));
        } else if(summary instanceof ServiceSummary) {
            form.setAffiliationType(Text.valueOf(AffiliationType.SERVICE.value()));
        }
        
        form.setPutCode(Text.valueOf(summary.getPutCode()));
        form.setVisibility(Visibility.valueOf(summary.getVisibility()));
        
        initDateFields(form, summary.getStartDate(), summary.getEndDate());
        
        Organization organization = summary.getOrganization();

        form.setDateSortString(PojoUtil.createDateSortString(summary));
        form.setAffiliationName(Text.valueOf(organization.getName()));
        OrganizationAddress address = organization.getAddress();
        form.setCity(Text.valueOf(address.getCity()));
        if (organization.getDisambiguatedOrganization() != null) {
            if (organization.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier() != null) {
                form.setDisambiguatedAffiliationSourceId(Text.valueOf(organization.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier()));
                form.setDisambiguationSource(Text.valueOf(organization.getDisambiguatedOrganization().getDisambiguationSource()));
                form.setOrgDisambiguatedId(Text.valueOf(String.valueOf(organization.getDisambiguatedOrganization().getId())));  
            }
        }
        if (address.getRegion() != null) {
            form.setRegion(Text.valueOf(address.getRegion()));
        } else {
            form.setRegion(new Text());
        }

        if (address.getCountry() != null) {
            form.setCountry(Text.valueOf(address.getCountry().name()));
        } else {
            form.setCountry(new Text());
        }

        if (summary.getDepartmentName() != null) {
            form.setDepartmentName(Text.valueOf(summary.getDepartmentName()));
        } else {
            form.setDepartmentName(new Text());
        }

        if (summary.getRoleTitle() != null) {
            form.setRoleTitle(Text.valueOf(summary.getRoleTitle()));
        } else {
            form.setRoleTitle(new Text());
        }

        Source source = summary.getSource();
        if (source != null) {
            form.setSource(source.retrieveSourcePath());
            if (source.getSourceName() != null) {
                form.setSourceName(source.getSourceName().getContent());
            }
            
            if (source.getAssertionOriginClientId() != null) {
                form.setAssertionOriginClientId(summary.getSource().getAssertionOriginClientId().getPath());
            }
            
            if (source.getAssertionOriginOrcid() != null) {
                form.setAssertionOriginOrcid(summary.getSource().getAssertionOriginOrcid().getPath());
            }
            
            if (source.getAssertionOriginName() != null) {
                form.setAssertionOriginName(summary.getSource().getAssertionOriginName().getContent());
            }
        }

        if (summary.getExternalIDs() != null) {
            List<ActivityExternalIdentifier> affiliationExternalIdentifiers = new ArrayList<>();
            for (ExternalID externalID : summary.getExternalIDs().getExternalIdentifier()) {
                affiliationExternalIdentifiers.add(ActivityExternalIdentifier.valueOf(externalID));
            }
            form.setAffiliationExternalIdentifiers(affiliationExternalIdentifiers);
        }

        // Set empty url field
        form.setUrl(new Text());
        
        form.setCreatedDate(Date.valueOf(summary.getCreatedDate()));
        form.setLastModified(Date.valueOf(summary.getLastModifiedDate()));
        
        return form;
    }
    
    public static AffiliationForm valueOf(Affiliation affiliation) {
        AffiliationForm form = new AffiliationForm();

        if(affiliation instanceof Distinction) {
            form.setAffiliationType(Text.valueOf(AffiliationType.DISTINCTION.value()));
        } else if (affiliation instanceof Education) {
            form.setAffiliationType(Text.valueOf(AffiliationType.EDUCATION.value()));
        } else if(affiliation instanceof Employment) {
            form.setAffiliationType(Text.valueOf(AffiliationType.EMPLOYMENT.value()));
        } else if(affiliation instanceof InvitedPosition) {
            form.setAffiliationType(Text.valueOf(AffiliationType.INVITED_POSITION.value()));
        } else if(affiliation instanceof Membership) {
            form.setAffiliationType(Text.valueOf(AffiliationType.MEMBERSHIP.value()));
        } else if(affiliation instanceof Qualification) {
            form.setAffiliationType(Text.valueOf(AffiliationType.QUALIFICATION.value()));
        } else if(affiliation instanceof Service) {
            form.setAffiliationType(Text.valueOf(AffiliationType.SERVICE.value()));
        } 

        form.setPutCode(Text.valueOf(affiliation.getPutCode()));
        form.setVisibility(Visibility.valueOf(affiliation.getVisibility()));
        
        initDateFields(form, affiliation.getStartDate(), affiliation.getEndDate());
        
        Organization organization = affiliation.getOrganization();

        form.setDateSortString(PojoUtil.createDateSortString(affiliation));
        form.setAffiliationName(Text.valueOf(organization.getName()));
        OrganizationAddress address = organization.getAddress();
        form.setCity(Text.valueOf(address.getCity()));
        if (organization.getDisambiguatedOrganization() != null) {
            if (organization.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier() != null) {
                form.setDisambiguatedAffiliationSourceId(Text.valueOf(organization.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier()));
                form.setDisambiguationSource(Text.valueOf(organization.getDisambiguatedOrganization().getDisambiguationSource()));
                form.setOrgDisambiguatedId(Text.valueOf(String.valueOf(organization.getDisambiguatedOrganization().getId())));  
            }
        }
        if (address.getRegion() != null) {
            form.setRegion(Text.valueOf(address.getRegion()));
        } else {
            form.setRegion(new Text());
        }

        if (address.getCountry() != null) {
            form.setCountry(Text.valueOf(address.getCountry().name()));
        } else {
            form.setCountry(new Text());
        }

        if (affiliation.getDepartmentName() != null) {
            form.setDepartmentName(Text.valueOf(affiliation.getDepartmentName()));
        } else {
            form.setDepartmentName(new Text());
        }

        if (affiliation.getRoleTitle() != null) {
            form.setRoleTitle(Text.valueOf(affiliation.getRoleTitle()));
        } else {
            form.setRoleTitle(new Text());
        }

        
        Source source = affiliation.getSource();
        if (source != null) {
            form.setSource(source.retrieveSourcePath());
            if (source.getSourceName() != null) {
                form.setSourceName(source.getSourceName().getContent());
            }
            
            if (affiliation.getSource().getAssertionOriginClientId() != null) {
                form.setAssertionOriginClientId(affiliation.getSource().getAssertionOriginClientId().getPath());
            }
            
            if (affiliation.getSource().getAssertionOriginOrcid() != null) {
                form.setAssertionOriginOrcid(affiliation.getSource().getAssertionOriginOrcid().getPath());
            }
            
            if (affiliation.getSource().getAssertionOriginName() != null) {
                form.setAssertionOriginName(affiliation.getSource().getAssertionOriginName().getContent());
            }
        }

        if (affiliation.getUrl() != null) {
            form.setUrl(Text.valueOf(affiliation.getUrl().getValue()));
        } else {
            form.setUrl(new Text());
        }

        if (affiliation.getExternalIDs() != null) {
            List<ActivityExternalIdentifier> affiliationExternalIdentifiers = new ArrayList<>();
            for (ExternalID externalID : affiliation.getExternalIDs().getExternalIdentifier()) {
                affiliationExternalIdentifiers.add(ActivityExternalIdentifier.valueOf(externalID));
            }
            form.setAffiliationExternalIdentifiers(affiliationExternalIdentifiers);
        }

        form.setCreatedDate(Date.valueOf(affiliation.getCreatedDate()));
        form.setLastModified(Date.valueOf(affiliation.getLastModifiedDate()));
        return form;
    }

    private static void initDateFields(AffiliationForm form, FuzzyDate startDate, FuzzyDate endDate) {
        if (startDate != null) {
            form.setStartDate(Date.valueOf(startDate));
            if (form.getStartDate().getDay() == null) {
                form.getStartDate().setDay(new String());
            }
            if (form.getStartDate().getMonth() == null) {
                form.getStartDate().setMonth(new String());
            }
            if (form.getStartDate().getYear() == null) {
                form.getStartDate().setYear(new String());
            }
        } else {
            form.setStartDate(getEmptyDate());
        }
        
        if (endDate != null) {
            form.setEndDate(Date.valueOf(endDate));
            if (form.getEndDate().getDay() == null) {
                form.getEndDate().setDay(new String());
            }
            if (form.getEndDate().getMonth() == null) {
                form.getEndDate().setMonth(new String());
            }
            if (form.getEndDate().getYear() == null) {
                form.getEndDate().setYear(new String());
            }
        } else {
            form.setEndDate(getEmptyDate());
        }
    }
    
    public Affiliation toAffiliation() {
        Affiliation affiliation = null;

        if (AffiliationType.DISTINCTION.value().equals(affiliationType.getValue())) {
            affiliation = new Distinction();
        } else if(AffiliationType.EDUCATION.value().equals(affiliationType.getValue())) {
            affiliation = new Education();
        }else if(AffiliationType.EMPLOYMENT.value().equals(affiliationType.getValue())) {
            affiliation = new Employment();
        }else if(AffiliationType.INVITED_POSITION.value().equals(affiliationType.getValue())) {
            affiliation = new InvitedPosition();
        }else if(AffiliationType.MEMBERSHIP.value().equals(affiliationType.getValue())) {
            affiliation = new Membership();
        }else if(AffiliationType.QUALIFICATION.value().equals(affiliationType.getValue())) {
            affiliation = new Qualification();
        }else if(AffiliationType.SERVICE.value().equals(affiliationType.getValue())) {
            affiliation = new Service();
        }

        if (!PojoUtil.isEmpty(putCode)) {
            affiliation.setPutCode(Long.valueOf(putCode.getValue()));
        }
        if (visibility != null && visibility.getVisibility() != null) {
            affiliation.setVisibility(org.orcid.jaxb.model.v3.release.common.Visibility.fromValue(visibility.getVisibility().value()));
        }
        Organization organization = new Organization();
        affiliation.setOrganization(organization);
        organization.setName(affiliationName.getValue());
        OrganizationAddress organizationAddress = new OrganizationAddress();
        organization.setAddress(organizationAddress);
        organizationAddress.setCity(city.getValue());
        if (!PojoUtil.isEmpty(region)) {
            organizationAddress.setRegion(region.getValue());
        }
        if (!PojoUtil.isEmpty(disambiguatedAffiliationSourceId)) {
            organization.setDisambiguatedOrganization(new DisambiguatedOrganization());
            organization.getDisambiguatedOrganization().setDisambiguatedOrganizationIdentifier(disambiguatedAffiliationSourceId.getValue());
            organization.getDisambiguatedOrganization().setDisambiguationSource(disambiguationSource.getValue());
        }
        organizationAddress.setCountry(Iso3166Country.fromValue(country.getValue()));
        if (!PojoUtil.isEmpty(roleTitle)) {
            affiliation.setRoleTitle(roleTitle.getValue());
        }
        if (!PojoUtil.isEmpty(departmentName)) {
            affiliation.setDepartmentName(departmentName.getValue());
        }
        if (!PojoUtil.isEmpty(startDate)) {
            affiliation.setStartDate(startDate.toV3FuzzyDate());
        }
        if (!PojoUtil.isEmpty(endDate)) {
            affiliation.setEndDate(endDate.toV3FuzzyDate());
        }
        if (!PojoUtil.isEmpty(url)) {
            affiliation.setUrl(new Url(url.getValue()));
        }
        
        if (affiliationExternalIdentifiers != null) {
            ExternalIDs externalIDs = new ExternalIDs();
            for (ActivityExternalIdentifier affiliationExternalIdentifier : affiliationExternalIdentifiers) {
                externalIDs.getExternalIdentifier().add(affiliationExternalIdentifier.toExternalIdentifier());
            }
            affiliation.setExternalIDs(externalIDs);
        }

        if(!PojoUtil.isEmpty(source)) {
            org.orcid.jaxb.model.v3.release.common.Source source = new org.orcid.jaxb.model.v3.release.common.Source();
            
            if(OrcidStringUtils.isClientId(this.getSource())) {
                source.setSourceClientId(new SourceClientId(this.getSource()));
            } else {
                source.setSourceOrcid(new SourceOrcid(this.getSource()));
            }
                       
            affiliation.setSource(source);
        }
        
        return affiliation;
    }

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

    public Text getAffiliationName() {
        return affiliationName;
    }

    public void setAffiliationName(Text affiliationName) {
        this.affiliationName = affiliationName;
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

    public Text getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(Text departmentName) {
        this.departmentName = departmentName;
    }

    public Text getAffiliationType() {
        return affiliationType;
    }

    public void setAffiliationType(Text affiliationType) {
        this.affiliationType = affiliationType;
    }

    public String getAffiliationTypeForDisplay() {
        return affiliationTypeForDisplay;
    }

    public void setAffiliationTypeForDisplay(String affiliationTypeForDisplay) {
        this.affiliationTypeForDisplay = affiliationTypeForDisplay;
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

    public Text getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(Text roleTitle) {
        this.roleTitle = roleTitle;
    }

    public Text getDisambiguatedAffiliationSourceId() {
        return disambiguatedAffiliationSourceId;
    }

    public void setDisambiguatedAffiliationSourceId(Text disambiguatedAffiliationSourceId) {
        this.disambiguatedAffiliationSourceId = disambiguatedAffiliationSourceId;
    }

    public Text getDisambiguationSource() {
        return disambiguationSource;
    }

    public void setDisambiguationSource(Text disambiguationSource) {
        this.disambiguationSource = disambiguationSource;
    }

    public String getDateSortString() {
        return dateSortString;
    }

    public void setDateSortString(String dateSortString) {
        this.dateSortString = dateSortString;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    
    public String getAssertionOriginOrcid() {
        return assertionOriginOrcid;
    }

    public void setAssertionOriginOrcid(String assertionOriginOrcid) {
        this.assertionOriginOrcid = assertionOriginOrcid;
    }

    public String getAssertionOriginClientId() {
        return assertionOriginClientId;
    }

    public void setAssertionOriginClientId(String assertionOriginClientId) {
        this.assertionOriginClientId = assertionOriginClientId;
    }

    public String getAssertionOriginName() {
        return assertionOriginName;
    }

    public void setAssertionOriginName(String assertionOriginName) {
        this.assertionOriginName = assertionOriginName;
    }

    public String getOrgDisambiguatedCity() {
        return orgDisambiguatedCity;
    }

    public void setOrgDisambiguatedCity(String orgDisambiguatedCity) {
        this.orgDisambiguatedCity = orgDisambiguatedCity;
    }
    
    public String getOrgDisambiguatedCountry() {
        return orgDisambiguatedCountry;
    }

    public void setOrgDisambiguatedCountry(String orgDisambiguatedCountry) {
        this.orgDisambiguatedCountry = orgDisambiguatedCountry;
    }

    public Text getOrgDisambiguatedId() {
        return orgDisambiguatedId;
    }

    public void setOrgDisambiguatedId(Text orgDisambiguatedId) {
        this.orgDisambiguatedId = orgDisambiguatedId;
    }
    
    public String getOrgDisambiguatedName() {
        return orgDisambiguatedName;
    }

    public void setOrgDisambiguatedName(String orgDisambiguatedName) {
        this.orgDisambiguatedName = orgDisambiguatedName;
    }
    
    public String getOrgDisambiguatedRegion() {
        return orgDisambiguatedRegion;
    }

    public void setOrgDisambiguatedRegion(String orgDisambiguatedRegion) {
        this.orgDisambiguatedRegion = orgDisambiguatedRegion;
    }
    
    public String getOrgDisambiguatedUrl() {
        return orgDisambiguatedUrl;
    }

    public void setOrgDisambiguatedUrl(String orgDisambiguatedUrl) {
        this.orgDisambiguatedUrl = orgDisambiguatedUrl;
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

    public Text getUrl() {
        return url;
    }

    public void setUrl(Text url) {
        this.url = url;
    }

    public List<OrgDisambiguatedExternalIdentifiers> getOrgDisambiguatedExternalIdentifiers() {
        return orgDisambiguatedExternalIdentifiers;
    }

    public void setOrgDisambiguatedExternalIdentifiers(List<OrgDisambiguatedExternalIdentifiers> orgDisambiguatedExternalIdentifiers) {
        this.orgDisambiguatedExternalIdentifiers = orgDisambiguatedExternalIdentifiers;
    }        

    public List<ActivityExternalIdentifier> getAffiliationExternalIdentifiers() {
        return affiliationExternalIdentifiers;
    }

    public void setAffiliationExternalIdentifiers(List<ActivityExternalIdentifier> affiliationExternalIdentifiers) {
        this.affiliationExternalIdentifiers = affiliationExternalIdentifiers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((affiliationExternalIdentifiers == null) ? 0 : affiliationExternalIdentifiers.hashCode());
        result = prime * result + ((affiliationName == null) ? 0 : affiliationName.hashCode());
        result = prime * result + ((affiliationType == null) ? 0 : affiliationType.hashCode());
        result = prime * result + ((affiliationTypeForDisplay == null) ? 0 : affiliationTypeForDisplay.hashCode());
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((countryForDisplay == null) ? 0 : countryForDisplay.hashCode());
        result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
        result = prime * result + ((dateSortString == null) ? 0 : dateSortString.hashCode());
        result = prime * result + ((departmentName == null) ? 0 : departmentName.hashCode());
        result = prime * result + ((disambiguatedAffiliationSourceId == null) ? 0 : disambiguatedAffiliationSourceId.hashCode());
        result = prime * result + ((disambiguationSource == null) ? 0 : disambiguationSource.hashCode());
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((errors == null) ? 0 : errors.hashCode());
        result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
        result = prime * result + ((orgDisambiguatedCity == null) ? 0 : orgDisambiguatedCity.hashCode());
        result = prime * result + ((orgDisambiguatedCountry == null) ? 0 : orgDisambiguatedCountry.hashCode());
        result = prime * result + ((orgDisambiguatedExternalIdentifiers == null) ? 0 : orgDisambiguatedExternalIdentifiers.hashCode());
        result = prime * result + ((orgDisambiguatedId == null) ? 0 : orgDisambiguatedId.hashCode());
        result = prime * result + ((orgDisambiguatedName == null) ? 0 : orgDisambiguatedName.hashCode());
        result = prime * result + ((orgDisambiguatedRegion == null) ? 0 : orgDisambiguatedRegion.hashCode());
        result = prime * result + ((orgDisambiguatedUrl == null) ? 0 : orgDisambiguatedUrl.hashCode());
        result = prime * result + ((putCode == null) ? 0 : putCode.hashCode());
        result = prime * result + ((region == null) ? 0 : region.hashCode());
        result = prime * result + ((roleTitle == null) ? 0 : roleTitle.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((sourceName == null) ? 0 : sourceName.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AffiliationForm other = (AffiliationForm) obj;
        if (affiliationExternalIdentifiers == null) {
            if (other.affiliationExternalIdentifiers != null)
                return false;
        } else if (!affiliationExternalIdentifiers.equals(other.affiliationExternalIdentifiers))
            return false;
        if (affiliationName == null) {
            if (other.affiliationName != null)
                return false;
        } else if (!affiliationName.equals(other.affiliationName))
            return false;
        if (affiliationType == null) {
            if (other.affiliationType != null)
                return false;
        } else if (!affiliationType.equals(other.affiliationType))
            return false;
        if (affiliationTypeForDisplay == null) {
            if (other.affiliationTypeForDisplay != null)
                return false;
        } else if (!affiliationTypeForDisplay.equals(other.affiliationTypeForDisplay))
            return false;
        if (city == null) {
            if (other.city != null)
                return false;
        } else if (!city.equals(other.city))
            return false;
        if (country == null) {
            if (other.country != null)
                return false;
        } else if (!country.equals(other.country))
            return false;
        if (countryForDisplay == null) {
            if (other.countryForDisplay != null)
                return false;
        } else if (!countryForDisplay.equals(other.countryForDisplay))
            return false;
        if (createdDate == null) {
            if (other.createdDate != null)
                return false;
        } else if (!createdDate.equals(other.createdDate))
            return false;
        if (dateSortString == null) {
            if (other.dateSortString != null)
                return false;
        } else if (!dateSortString.equals(other.dateSortString))
            return false;
        if (departmentName == null) {
            if (other.departmentName != null)
                return false;
        } else if (!departmentName.equals(other.departmentName))
            return false;
        if (disambiguatedAffiliationSourceId == null) {
            if (other.disambiguatedAffiliationSourceId != null)
                return false;
        } else if (!disambiguatedAffiliationSourceId.equals(other.disambiguatedAffiliationSourceId))
            return false;
        if (disambiguationSource == null) {
            if (other.disambiguationSource != null)
                return false;
        } else if (!disambiguationSource.equals(other.disambiguationSource))
            return false;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (errors == null) {
            if (other.errors != null)
                return false;
        } else if (!errors.equals(other.errors))
            return false;
        if (lastModified == null) {
            if (other.lastModified != null)
                return false;
        } else if (!lastModified.equals(other.lastModified))
            return false;
        if (orgDisambiguatedCity == null) {
            if (other.orgDisambiguatedCity != null)
                return false;
        } else if (!orgDisambiguatedCity.equals(other.orgDisambiguatedCity))
            return false;
        if (orgDisambiguatedCountry == null) {
            if (other.orgDisambiguatedCountry != null)
                return false;
        } else if (!orgDisambiguatedCountry.equals(other.orgDisambiguatedCountry))
            return false;
        if (orgDisambiguatedExternalIdentifiers == null) {
            if (other.orgDisambiguatedExternalIdentifiers != null)
                return false;
        } else if (!orgDisambiguatedExternalIdentifiers.equals(other.orgDisambiguatedExternalIdentifiers))
            return false;
        if (orgDisambiguatedId == null) {
            if (other.orgDisambiguatedId != null)
                return false;
        } else if (!orgDisambiguatedId.equals(other.orgDisambiguatedId))
            return false;
        if (orgDisambiguatedName == null) {
            if (other.orgDisambiguatedName != null)
                return false;
        } else if (!orgDisambiguatedName.equals(other.orgDisambiguatedName))
            return false;
        if (orgDisambiguatedRegion == null) {
            if (other.orgDisambiguatedRegion != null)
                return false;
        } else if (!orgDisambiguatedRegion.equals(other.orgDisambiguatedRegion))
            return false;
        if (orgDisambiguatedUrl == null) {
            if (other.orgDisambiguatedUrl != null)
                return false;
        } else if (!orgDisambiguatedUrl.equals(other.orgDisambiguatedUrl))
            return false;
        if (putCode == null) {
            if (other.putCode != null)
                return false;
        } else if (!putCode.equals(other.putCode))
            return false;
        if (region == null) {
            if (other.region != null)
                return false;
        } else if (!region.equals(other.region))
            return false;
        if (roleTitle == null) {
            if (other.roleTitle != null)
                return false;
        } else if (!roleTitle.equals(other.roleTitle))
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (sourceName == null) {
            if (other.sourceName != null)
                return false;
        } else if (!sourceName.equals(other.sourceName))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }  

    private static Date getEmptyDate() {
        Date date = new Date();
        date.setDay(new String());
        date.setMonth(new String());
        date.setYear(new String());
        return date;
    }
}