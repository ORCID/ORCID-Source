package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.jaxb.model.v3.rc2.common.FuzzyDate;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.PeerReview;
import org.orcid.jaxb.model.v3.rc2.record.summary.PeerReviewSummary;

public class PeerReviewForm extends VisibilityForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = -6291184427922193706L;

    private List<String> errors = new ArrayList<String>();

    private Text putCode;

    private List<ActivityExternalIdentifier> externalIdentifiers;

    private Text url;

    private Text role;

    private Text type;

    private Text orgName;
    
    private Text city;
    
    private Text country;

    private String countryForDisplay;

    private Date completionDate;    
    
    private ActivityExternalIdentifier subjectExternalIdentifier;
    
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

    public List<ActivityExternalIdentifier> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(List<ActivityExternalIdentifier> externalIdentifiers) {
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

    public ActivityExternalIdentifier getSubjectExternalIdentifier() {
        return subjectExternalIdentifier;
    }

    public void setSubjectExternalIdentifier(ActivityExternalIdentifier subjectExternalIdentifier) {
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
    
    public static PeerReviewForm valueOf(PeerReviewSummary peerReviewSummary) {
        PeerReviewForm peerReviewForm = new PeerReviewForm();
        peerReviewForm.setPutCode(Text.valueOf(peerReviewSummary.getPutCode()));

        peerReviewForm.setOrgName(Text.valueOf(peerReviewSummary.getOrganization().getName()));
        
        if (peerReviewSummary.getOrganization().getAddress().getCity() != null) {
            peerReviewForm.setCity(Text.valueOf(peerReviewSummary.getOrganization().getAddress().getCity()));
        }
        
        if (peerReviewSummary.getOrganization().getAddress().getCountry() != null) {
            peerReviewForm.setCountry(Text.valueOf(peerReviewSummary.getOrganization().getAddress().getCountry().name()));
        }
        
        peerReviewForm.setCompletionDate(getCompletionDate(peerReviewSummary.getCompletionDate()));
        peerReviewForm.setCreatedDate(getCreatedDate(peerReviewSummary.getCreatedDate().getValue()));

        peerReviewForm.setSource(peerReviewSummary.getSource().getSourceName().getContent());
        
        peerReviewForm.setSource(peerReviewSummary.getSource().retrieveSourcePath());
        peerReviewForm.setSourceName(peerReviewSummary.getSource().getSourceName().getContent());

        peerReviewForm.setRole(Text.valueOf(peerReviewSummary.getRole().value()));
        peerReviewForm.setType(Text.valueOf(peerReviewSummary.getType().value()));

        peerReviewForm.setVisibility(Visibility.valueOf(peerReviewSummary.getVisibility()));

        if (peerReviewSummary.getUrl() != null) {
            peerReviewForm.setUrl(Text.valueOf(peerReviewSummary.getUrl().getValue()));
        }
        
        if (peerReviewSummary.getExternalIdentifiers().getExternalIdentifier() != null) {
            peerReviewForm.setExternalIdentifiers(new ArrayList<ActivityExternalIdentifier>());
            for(ExternalID extId : peerReviewSummary.getExternalIdentifiers().getExternalIdentifier()) {                
                peerReviewForm.getExternalIdentifiers().add(ActivityExternalIdentifier.valueOf(extId));
            } 
        }
        return peerReviewForm;
    }

    private static Date getCompletionDate(FuzzyDate completionDate) {
        if (completionDate != null) {
            Integer year = PojoUtil.isEmpty(completionDate.getYear()) ? null : Integer.valueOf(completionDate.getYear().getValue());
            Integer month = PojoUtil.isEmpty(completionDate.getMonth()) ? null : Integer.valueOf(completionDate.getMonth().getValue());
            Integer day = PojoUtil.isEmpty(completionDate.getDay()) ? null : Integer.valueOf(completionDate.getDay().getValue());
            if (year != null && year == 0) {
                year = null;
            }
            if (month != null && month == 0) {
                month = null;
            }
            if (day != null && day == 0) {
                day = null;
            }
            return Date.valueOf(FuzzyDate.valueOf(year, month, day));
        }
        return null;
    }

    private static Date getCreatedDate(XMLGregorianCalendar calendar) {
        if (calendar != null) {
            int year = calendar.getYear();
            int month = calendar.getMonth();
            int day = calendar.getDay();
            return Date.valueOf(FuzzyDate.valueOf(year, month, day));
        }
        return null;
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
                    form.setCountry(Text.valueOf(peerReview.getOrganization().getAddress().getCountry().name()));
                }
            }

        }

        // External ids
        if(peerReview.getExternalIdentifiers() != null) {
            List<ExternalID> externalIdentifiers = peerReview.getExternalIdentifiers().getExternalIdentifier();
            form.setExternalIdentifiers(new ArrayList<ActivityExternalIdentifier>());
            for(ExternalID extId : externalIdentifiers) {                
                form.getExternalIdentifiers().add(ActivityExternalIdentifier.valueOf(extId));
            }                                    
        }        

        // Group Id
        if(!PojoUtil.isEmpty(peerReview.getGroupId())) {
            form.setGroupId(Text.valueOf(peerReview.getGroupId()));
        }
        
        // Subject ext Id
        if(peerReview.getSubjectExternalIdentifier() != null) {
            ActivityExternalIdentifier wExtId = new ActivityExternalIdentifier();
            if(peerReview.getSubjectExternalIdentifier().getRelationship() != null) {
                wExtId.setRelationship(Text.valueOf(peerReview.getSubjectExternalIdentifier().getRelationship().value()));
            }
            
            if(peerReview.getSubjectExternalIdentifier().getUrl() != null) {
                wExtId.setUrl(Text.valueOf(peerReview.getSubjectExternalIdentifier().getUrl().getValue()));
            }
            
            if(peerReview.getSubjectExternalIdentifier().getValue() != null) {
                wExtId.setExternalIdentifierId(Text.valueOf(peerReview.getSubjectExternalIdentifier().getValue()));
            }
            
            if(peerReview.getSubjectExternalIdentifier().getType() != null) {
                wExtId.setExternalIdentifierType(Text.valueOf(peerReview.getSubjectExternalIdentifier().getType()));
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
