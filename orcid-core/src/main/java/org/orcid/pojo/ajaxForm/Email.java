package org.orcid.pojo.ajaxForm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.release.common.Visibility;

public class Email implements ErrorsInterface {

    protected String value;

    protected Boolean primary;

    protected Boolean current;

    protected Boolean verified;

    protected Visibility visibility;

    private String source;

    private String sourceName;
    
    private String assertionOriginOrcid;
    
    private String assertionOriginClientId;
    
    private String assertionOriginName;

    private Date createdDate;

    private Date lastModified;

    private Date verificationDate;

    private List<String> errors = new ArrayList<String>();

    public static Email valueOf(org.orcid.jaxb.model.v3.release.record.Email e) {
        Email email = new Email();
        if (e != null) {
            email.setCurrent(e.isCurrent());
            email.setPrimary(e.isPrimary());
            email.setSource(e.getSource().retrieveSourcePath());
            email.setValue(e.getEmail());
            email.setVerified(e.isVerified());
            email.setVisibility(e.getVisibility());

            if (e.getSource().getSourceName() != null) {
                email.setSourceName(e.getSource().getSourceName().getContent());
            }
            
            if (e.getSource().getAssertionOriginClientId() != null) {
                email.setAssertionOriginClientId(e.getSource().getAssertionOriginClientId().getPath());
            }
            
            if (e.getSource().getAssertionOriginOrcid() != null) {
                email.setAssertionOriginOrcid(e.getSource().getAssertionOriginOrcid().getPath());
            }
            
            if (e.getSource().getAssertionOriginName() != null) {
                email.setAssertionOriginName(e.getSource().getAssertionOriginName().getContent());
            }

            if (e.getCreatedDate() != null) {
                Date createdDate = new Date();
                createdDate.setYear(String.valueOf(e.getCreatedDate().getValue().getYear()));
                createdDate.setMonth(String.valueOf(e.getCreatedDate().getValue().getMonth()));
                createdDate.setDay(String.valueOf(e.getCreatedDate().getValue().getDay()));
                createdDate.setTimestamp(e.getCreatedDate().getValue().toGregorianCalendar().getTimeInMillis());
                email.setCreatedDate(createdDate);
            }

            if (e.getLastModifiedDate() != null) {
                Date lastModifiedDate = new Date();
                lastModifiedDate.setYear(String.valueOf(e.getLastModifiedDate().getValue().getYear()));
                lastModifiedDate.setMonth(String.valueOf(e.getLastModifiedDate().getValue().getMonth()));
                lastModifiedDate.setDay(String.valueOf(e.getLastModifiedDate().getValue().getDay()));
                email.setLastModified(lastModifiedDate);
            }

            if (e.getVerificationDate() != null) {
                Date verificationDate = new Date();
                verificationDate.setYear(String.valueOf(e.getVerificationDate().getValue().getYear()));
                verificationDate.setMonth(String.valueOf(e.getVerificationDate().getValue().getMonth()));
                verificationDate.setDay(String.valueOf(e.getVerificationDate().getValue().getDay()));
                email.setVerificationDate(verificationDate);
            }
        }
        return email;
    }
    
    public org.orcid.jaxb.model.v3.release.record.Email toV3Email() {
        org.orcid.jaxb.model.v3.release.record.Email email = new org.orcid.jaxb.model.v3.release.record.Email();
        email.setCurrent(current);
        email.setEmail(value);
        email.setLastModifiedDate(null);
        email.setPrimary(primary);
        email.setVerified(verified);
        email.setVisibility(visibility);        
        return email;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean isPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public Boolean isCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public Boolean isVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
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

    public Date getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(Date verificationDate) {
        this.verificationDate = verificationDate;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
