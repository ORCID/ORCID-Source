package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.rc1.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.rc1.common.FuzzyDate;
import org.orcid.jaxb.model.v3.rc1.common.Iso3166Country;
import org.orcid.jaxb.model.v3.rc1.common.Organization;
import org.orcid.jaxb.model.v3.rc1.common.OrganizationAddress;
import org.orcid.jaxb.model.v3.rc1.common.Title;
import org.orcid.jaxb.model.v3.rc1.common.Url;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc1.record.PeerReview;
import org.orcid.jaxb.model.v3.rc1.record.PeerReviewType;
import org.orcid.jaxb.model.v3.rc1.record.Relationship;
import org.orcid.jaxb.model.v3.rc1.record.Role;
import org.orcid.jaxb.model.v3.rc1.record.WorkTitle;
import org.orcid.jaxb.model.v3.rc1.record.WorkType;

public class PeerReviewForm extends VisibilityForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = -6291184427922193706L;

    private List<String> errors = new ArrayList<String>();

    private Text putCode;

    private List<WorkExternalIdentifier> externalIdentifiers;

    private Text url;

    private Text role;

    private Text type;

    private Text orgName;
    
    private Text city;

    private Text region;

    private Text country;

    private String countryForDisplay;

    private Text disambiguatedOrganizationSourceId;

    private Text disambiguationSource;

    private Date completionDate;    
    
    private WorkExternalIdentifier subjectExternalIdentifier;
    
    private Text groupId;
    
    private Text groupIdPutCode;
    
    private Text subjectContainerName;
    
    private Text subjectType;
    
    private Text subjectName;
    
    private TranslatedTitleForm translatedSubjectName;
    
    private Text subjectUrl;

    private String source;

    private String sourceName;

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

    public List<WorkExternalIdentifier> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(List<WorkExternalIdentifier> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    public Text getUrl() {
        return url;
    }

    public void setUrl(Text url) {
        this.url = url;
    }

    public Text getRole() {
        return role;
    }

    public void setRole(Text role) {
        this.role = role;
    }

    public Text getType() {
        return type;
    }

    public void setType(Text type) {
        this.type = type;
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

    public Text getDisambiguatedOrganizationSourceId() {
        return disambiguatedOrganizationSourceId;
    }

    public void setDisambiguatedOrganizationSourceId(Text disambiguatedOrganizationSourceId) {
        this.disambiguatedOrganizationSourceId = disambiguatedOrganizationSourceId;
    }

    public Text getDisambiguationSource() {
        return disambiguationSource;
    }

    public void setDisambiguationSource(Text disambiguationSource) {
        this.disambiguationSource = disambiguationSource;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
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
    
    public Text getOrgName() {
        return orgName;
    }

    public void setOrgName(Text orgName) {
        this.orgName = orgName;
    }

    public Text getGroupId() {
        return groupId;
    }

    public void setGroupId(Text groupId) {
        this.groupId = groupId;
    }

    public WorkExternalIdentifier getSubjectExternalIdentifier() {
        return subjectExternalIdentifier;
    }

    public void setSubjectExternalIdentifier(WorkExternalIdentifier subjectExternalIdentifier) {
        this.subjectExternalIdentifier = subjectExternalIdentifier;
    }

    public Text getSubjectContainerName() {
        return subjectContainerName;
    }

    public void setSubjectContainerName(Text subjectContainerName) {
        this.subjectContainerName = subjectContainerName;
    }

    public Text getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(Text subjectType) {
        this.subjectType = subjectType;
    }

    public Text getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(Text subjectName) {
        this.subjectName = subjectName;
    }

    public TranslatedTitleForm getTranslatedSubjectName() {
        return translatedSubjectName;
    }

    public void setTranslatedSubjectName(TranslatedTitleForm translatedSubjectName) {
        this.translatedSubjectName = translatedSubjectName;
    }

    public Text getSubjectUrl() {
        return subjectUrl;
    }

    public void setSubjectUrl(Text subjectUrl) {
        this.subjectUrl = subjectUrl;
    }
        
    public Text getGroupIdPutCode() {
        return groupIdPutCode;
    }

    public void setGroupIdPutCode(Text groupIdPutCode) {
        this.groupIdPutCode = groupIdPutCode;
    }

    public PeerReview toPeerReview() {
        PeerReview peerReview = new PeerReview();
        // Put Code
        if (!PojoUtil.isEmpty(putCode)) {            
            peerReview.setPutCode(Long.valueOf(putCode.getValue()));
        }
        // Visibility
        if (visibility != null && visibility.getVisibility() != null) {
            peerReview.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.fromValue(visibility.getVisibility().value()));
        }

        // Completion date
        if (completionDate != null) {
            peerReview.setCompletionDate(new FuzzyDate(completionDate.toFuzzyDate()));
        }

        // External identifiers
        if (externalIdentifiers != null && !externalIdentifiers.isEmpty()) {
            peerReview.setExternalIdentifiers(new ExternalIDs());
            for (WorkExternalIdentifier extId : externalIdentifiers) {
                peerReview.getExternalIdentifiers().getExternalIdentifier().add(extId.toRecordWorkExternalIdentifier());
            }
        }

        // Set Organization
        Organization organization = new Organization();
        OrganizationAddress organizationAddress = new OrganizationAddress();
        organization.setAddress(organizationAddress);
        if(!PojoUtil.isEmpty(orgName)) {
            organization.setName(orgName.getValue());
        }
        if (!PojoUtil.isEmpty(city)) {
            organizationAddress.setCity(city.getValue());
        }
        if (!PojoUtil.isEmpty(region)) {
            organizationAddress.setRegion(region.getValue());
        }
        if (!PojoUtil.isEmpty(country)) {
            organizationAddress.setCountry(Iso3166Country.fromValue(country.getValue()));
        }
        if (!PojoUtil.isEmpty(disambiguatedOrganizationSourceId)) {
            organization.setDisambiguatedOrganization(new DisambiguatedOrganization());
            organization.getDisambiguatedOrganization().setDisambiguatedOrganizationIdentifier(disambiguatedOrganizationSourceId.getValue());
            organization.getDisambiguatedOrganization().setDisambiguationSource(disambiguationSource.getValue());
        }
        peerReview.setOrganization(organization);

        // Role
        if (!PojoUtil.isEmpty(role)) {
            peerReview.setRole(Role.fromValue(role.getValue()));
        }

        // Type
        if (!PojoUtil.isEmpty(type)) {
            peerReview.setType(PeerReviewType.fromValue(type.getValue()));
        }

        // Url
        if (!PojoUtil.isEmpty(url)) {
            peerReview.setUrl(new Url(url.getValue()));
        }

        // Group id
        if(!PojoUtil.isEmpty(groupId)) {
            peerReview.setGroupId(groupId.getValue());
        }
        
        // Subject external id
        if(!PojoUtil.isEmpty(subjectExternalIdentifier)) {
            ExternalID subjectExtId = new ExternalID();
            if(!PojoUtil.isEmpty(subjectExternalIdentifier.getRelationship())) {
                subjectExtId.setRelationship(Relationship.fromValue(subjectExternalIdentifier.getRelationship().getValue()));
            }
            
            if(!PojoUtil.isEmpty(subjectExternalIdentifier.getUrl())) {
                subjectExtId.setUrl(new Url(subjectExternalIdentifier.getUrl().getValue()));
            }
            
            if(!PojoUtil.isEmpty(subjectExternalIdentifier.getWorkExternalIdentifierId())) {
                subjectExtId.setValue(subjectExternalIdentifier.getWorkExternalIdentifierId().getValue());
            }
            
            if(!PojoUtil.isEmpty(subjectExternalIdentifier.getWorkExternalIdentifierType())) {
                subjectExtId.setType(subjectExternalIdentifier.getWorkExternalIdentifierType().getValue());
            }
            
            peerReview.setSubjectExternalIdentifier(subjectExtId);
        }
        
        // Subject container name
        if(!PojoUtil.isEmpty(subjectContainerName)) {
            Title containerName = new Title(subjectContainerName.getValue());            
            peerReview.setSubjectContainerName(containerName);
        }
        
        // Subject type
        if(!PojoUtil.isEmpty(subjectType)) {
            peerReview.setSubjectType(WorkType.fromValue(subjectType.getValue()));
        }
        
        // Subject name and subject translated name
        if(!PojoUtil.isEmpty(subjectName) || !PojoUtil.isEmpty(translatedSubjectName)) {
            WorkTitle workTitle = new WorkTitle();
            if(!PojoUtil.isEmpty(subjectName)){
                workTitle.setTitle(new Title(subjectName.getValue()));
            }
            
            if(translatedSubjectName != null) {
                org.orcid.jaxb.model.v3.rc1.common.TranslatedTitle tTitle = new org.orcid.jaxb.model.v3.rc1.common.TranslatedTitle();
                if(!PojoUtil.isEmpty(translatedSubjectName.getContent())) {
                    tTitle.setContent(translatedSubjectName.getContent());
                }
                if(!PojoUtil.isEmpty(translatedSubjectName.getLanguageCode())) {
                    tTitle.setLanguageCode(translatedSubjectName.getLanguageCode());
                }
                workTitle.setTranslatedTitle(tTitle);
            }            
            
            peerReview.setSubjectName(workTitle);
        }
        
        //Subject url
        if(!PojoUtil.isEmpty(subjectUrl)) {
            peerReview.setSubjectUrl(new Url(subjectUrl.getValue()));
        }
        
        return peerReview;
    }

    public static PeerReviewForm valueOf(PeerReview peerReview) {
        PeerReviewForm form = new PeerReviewForm();

        // Put code
        if (peerReview.getPutCode() != null) {
            form.setPutCode(Text.valueOf(peerReview.getPutCode()));
        }

        // Visibility
        if (peerReview.getVisibility() != null) {
            form.setVisibility(Visibility.valueOf(peerReview.getVisibility()));
        }

        // Completion date
        if (!PojoUtil.isEmpty(peerReview.getCompletionDate())) {
            form.setCompletionDate(Date.valueOf(peerReview.getCompletionDate()));
        }

        // Role
        if (peerReview.getRole() != null) {
            form.setRole(Text.valueOf(peerReview.getRole().value()));
        }

        // Type
        if (peerReview.getType() != null) {
            form.setType(Text.valueOf(peerReview.getType().value()));
        }

        // Url
        if (!PojoUtil.isEmpty(peerReview.getUrl())) {
            form.setUrl(Text.valueOf(peerReview.getUrl().getValue()));
        }

        // Org info
        if (peerReview.getOrganization() != null) {
            if(!PojoUtil.isEmpty(peerReview.getOrganization().getName())) {
                form.setOrgName(Text.valueOf(peerReview.getOrganization().getName()));
            }
            if (peerReview.getOrganization().getAddress() != null) {
                if (!PojoUtil.isEmpty(peerReview.getOrganization().getAddress().getCity())) {
                    form.setCity(Text.valueOf(peerReview.getOrganization().getAddress().getCity()));
                }
                if (peerReview.getOrganization().getAddress().getCountry() != null) {
                    form.setCountry(Text.valueOf(peerReview.getOrganization().getAddress().getCountry().value()));
                }
                if (!PojoUtil.isEmpty(peerReview.getOrganization().getAddress().getRegion())) {
                    form.setRegion(Text.valueOf(peerReview.getOrganization().getAddress().getRegion()));
                }
            }

            if (peerReview.getOrganization().getDisambiguatedOrganization() != null) {
                if (!PojoUtil.isEmpty(peerReview.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier())) {
                    form.setDisambiguatedOrganizationSourceId(Text
                            .valueOf(peerReview.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier()));
                }
                if (!PojoUtil.isEmpty(peerReview.getOrganization().getDisambiguatedOrganization().getDisambiguationSource())) {
                    form.setDisambiguationSource(Text.valueOf(peerReview.getOrganization().getDisambiguatedOrganization().getDisambiguationSource()));
                }
            }
        }

        // External ids
        if(peerReview.getExternalIdentifiers() != null) {
            List<ExternalID> externalIdentifiers = peerReview.getExternalIdentifiers().getExternalIdentifier();
            form.setExternalIdentifiers(new ArrayList<WorkExternalIdentifier>());
            for(ExternalID extId : externalIdentifiers) {                
                form.getExternalIdentifiers().add(WorkExternalIdentifier.valueOf(extId));
            }                                    
        }        

        // Group Id
        if(!PojoUtil.isEmpty(peerReview.getGroupId())) {
            form.setGroupId(Text.valueOf(peerReview.getGroupId()));
        }
        
        // Subject ext Id
        if(peerReview.getSubjectExternalIdentifier() != null) {
            WorkExternalIdentifier wExtId = new WorkExternalIdentifier();
            if(peerReview.getSubjectExternalIdentifier().getRelationship() != null) {
                wExtId.setRelationship(Text.valueOf(peerReview.getSubjectExternalIdentifier().getRelationship().value()));
            }
            
            if(peerReview.getSubjectExternalIdentifier().getUrl() != null) {
                wExtId.setUrl(Text.valueOf(peerReview.getSubjectExternalIdentifier().getUrl().getValue()));
            }
            
            if(peerReview.getSubjectExternalIdentifier().getValue() != null) {
                wExtId.setWorkExternalIdentifierId(Text.valueOf(peerReview.getSubjectExternalIdentifier().getValue()));
            }
            
            if(peerReview.getSubjectExternalIdentifier().getType() != null) {
                wExtId.setWorkExternalIdentifierType(Text.valueOf(peerReview.getSubjectExternalIdentifier().getType()));
            }            
            
            form.setSubjectExternalIdentifier(wExtId);
        }
        
        
        // Subject Container name
        if(peerReview.getSubjectContainerName() != null) {
            form.setSubjectContainerName(Text.valueOf(peerReview.getSubjectContainerName().getContent()));
        }
        
        // Subject type
        if(peerReview.getSubjectType() != null) {
            form.setSubjectType(Text.valueOf(peerReview.getSubjectType().value()));
        }

        // Subject name
        if(peerReview.getSubjectName() != null) {
            if(peerReview.getSubjectName().getTitle() != null) {
                form.setSubjectName(Text.valueOf(peerReview.getSubjectName().getTitle().getContent()));
            }
            
            TranslatedTitleForm tTitle = new TranslatedTitleForm();
            if(peerReview.getSubjectName().getTranslatedTitle() != null) {
                tTitle.setContent(peerReview.getSubjectName().getTranslatedTitle().getContent());
                tTitle.setLanguageCode(peerReview.getSubjectName().getTranslatedTitle().getLanguageCode());
            }
            form.setTranslatedSubjectName(tTitle);
        }
        
        // Subject url
        if(peerReview.getSubjectUrl() != null) {
            form.setSubjectUrl(Text.valueOf(peerReview.getSubjectUrl().getValue()));
        }
        
        // Source
        if(peerReview.getSource() != null) {
            form.setSource(peerReview.getSource().retrieveSourcePath());
            if(peerReview.getSource().getSourceName() != null)
                form.setSourceName(peerReview.getSource().getSourceName().getContent());
        }        

        // Created Date
        if(peerReview.getCreatedDate() != null) {
            form.setCreatedDate(Date.valueOf(peerReview.getCreatedDate()));
        }        

        // Last modified
        if(peerReview.getLastModifiedDate() != null) {
            form.setLastModified(Date.valueOf(peerReview.getLastModifiedDate()));
        }        

        return form;
    }
    
}
