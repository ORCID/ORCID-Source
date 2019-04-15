package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;

public class ExternalIdentifierForm extends VisibilityForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 8715304593954651166L;
    private List<String> errors = new ArrayList<String>();
    private String commonName;
    private String reference;
    private String url;
    private String source;
    private String sourceName;
    private Long displayIndex;
    private String putCode;
    private Date createdDate;
    private Date lastModified;
    private String assertionOriginOrcid;
    private String assertionOriginClientId;
    private String assertionOriginName;

    public static ExternalIdentifierForm valueOf(PersonExternalIdentifier extId) {
        if (extId == null)
            return null;
        ExternalIdentifierForm form = new ExternalIdentifierForm();
        form.setPutCode(String.valueOf(extId.getPutCode()));
        form.setCommonName(extId.getType());
        form.setReference(extId.getValue());
        if (extId.getVisibility() != null) {
            form.setVisibility(Visibility.valueOf(extId.getVisibility()));
        }
        if (extId.getUrl() != null) {
            form.setUrl(extId.getUrl().getValue());
        }

        if (extId.getSource() != null) {
            form.setSource(extId.getSource().retrieveSourcePath());
            if (extId.getSource().getSourceName() != null) {
                form.setSourceName(extId.getSource().getSourceName().getContent());
            }
            if (extId.getSource().getAssertionOriginClientId() != null) {
                form.setAssertionOriginClientId(extId.getSource().getAssertionOriginClientId().getPath());
            }
            
            if (extId.getSource().getAssertionOriginOrcid() != null) {
                form.setAssertionOriginOrcid(extId.getSource().getAssertionOriginOrcid().getPath());
            }
            
            if (extId.getSource().getAssertionOriginName() != null) {
                form.setAssertionOriginName(extId.getSource().getAssertionOriginName().getContent());
            }
        }

        if (extId.getDisplayIndex() != null) {
            form.setDisplayIndex(extId.getDisplayIndex());
        } else {
            form.setDisplayIndex(Long.valueOf(0L));
        }

        // Set created date
        form.setCreatedDate(Date.valueOf(extId.getCreatedDate()));
        // Set last modified
        form.setLastModified(Date.valueOf(extId.getLastModifiedDate()));
        return form;
    }

    public PersonExternalIdentifier toPersonExternalIdentifier() {
        PersonExternalIdentifier result = new PersonExternalIdentifier();
        if (putCode != null) {
            result.setPutCode(Long.valueOf(putCode));
        }
        result.setDisplayIndex(displayIndex);
        if (visibility != null && visibility.getVisibility() != null) {
            result.setVisibility(org.orcid.jaxb.model.v3.release.common.Visibility.fromValue(visibility.getVisibility().value()));
        }
        if (url != null) {
            result.setUrl(new Url(url));
        }
        result.setType(commonName);
        result.setValue(reference);
        return result;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getPutCode() {
        return putCode;
    }

    public void setPutCode(String putCode) {
        this.putCode = putCode;
    }

    public Long getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(Long displayIndex) {
        this.displayIndex = displayIndex;
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

}
