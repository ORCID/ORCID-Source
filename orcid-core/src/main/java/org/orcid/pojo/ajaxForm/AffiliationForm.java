package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.rc1.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.rc1.common.Iso3166Country;
import org.orcid.jaxb.model.v3.rc1.common.Organization;
import org.orcid.jaxb.model.v3.rc1.common.OrganizationAddress;
import org.orcid.jaxb.model.v3.rc1.common.Source;
import org.orcid.jaxb.model.v3.rc1.common.Url;
import org.orcid.jaxb.model.v3.rc1.record.Affiliation;
import org.orcid.jaxb.model.v3.rc1.record.AffiliationType;
import org.orcid.jaxb.model.v3.rc1.record.Distinction;
import org.orcid.jaxb.model.v3.rc1.record.Education;
import org.orcid.jaxb.model.v3.rc1.record.Employment;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc1.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc1.record.Membership;
import org.orcid.jaxb.model.v3.rc1.record.Qualification;
import org.orcid.jaxb.model.v3.rc1.record.Service;
import org.orcid.jaxb.model.v3.rc1.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.ServiceSummary;
import org.orcid.pojo.OrgDisambiguatedExternalIdentifiers;

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
            form.setCountry(Text.valueOf(address.getCountry().value()));
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

        if (summary.getStartDate() != null) {
            form.setStartDate(Date.valueOf(summary.getStartDate()));
        }
        if (summary.getEndDate() != null) {
            form.setEndDate(Date.valueOf(summary.getEndDate()));
        }
        Source source = summary.getSource();
        if (source != null) {
            form.setSource(source.retrieveSourcePath());
            if (source.getSourceName() != null) {
                form.setSourceName(source.getSourceName().getContent());
            }
        }

        if (summary.getExternalIDs() != null) {
            List<ActivityExternalIdentifier> affiliationExternalIdentifiers = new ArrayList<>();
            for (ExternalID externalID : summary.getExternalIDs().getExternalIdentifier()) {
                affiliationExternalIdentifiers.add(ActivityExternalIdentifier.valueOf(externalID));
            }
            form.setAffiliationExternalIdentifiers(affiliationExternalIdentifiers);
        }

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
            form.setCountry(Text.valueOf(address.getCountry().value()));
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

        if (affiliation.getStartDate() != null) {
            form.setStartDate(Date.valueOf(affiliation.getStartDate()));
        }
        if (affiliation.getEndDate() != null) {
            form.setEndDate(Date.valueOf(affiliation.getEndDate()));
        }
        Source source = affiliation.getSource();
        if (source != null) {
            form.setSource(source.retrieveSourcePath());
            if (source.getSourceName() != null) {
                form.setSourceName(source.getSourceName().getContent());
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
            affiliation.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.fromValue(visibility.getVisibility().value()));
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

}