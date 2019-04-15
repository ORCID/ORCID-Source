package org.orcid.pojo.ajaxForm;

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

    private List<String> errors = new ArrayList<String>();

    public static Email valueOf(org.orcid.jaxb.model.v3.release.record.Email e) {
        Email email = new Email();
        if (e != null) {
            email.setCurrent(e.isCurrent());
            email.setPrimary(e.isPrimary());
            email.setSource(e.retrieveSourcePath());
            email.setValue(e.getEmail());
            email.setVerified(e.isVerified());
            email.setVisibility(e.getVisibility());
            
            if (e.getSource().getAssertionOriginClientId() != null) {
                email.setAssertionOriginClientId(e.getSource().getAssertionOriginClientId().getPath());
            }
            
            if (e.getSource().getAssertionOriginOrcid() != null) {
                email.setAssertionOriginOrcid(e.getSource().getAssertionOriginOrcid().getPath());
            }
            
            if (e.getSource().getAssertionOriginName() != null) {
                email.setAssertionOriginName(e.getSource().getAssertionOriginName().getContent());
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

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
