/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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

import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.common_v2.Visibility;

public class Email implements ErrorsInterface {

    protected String value;

    protected Boolean primary;

    protected Boolean current;

    protected Boolean verified;

    protected Visibility visibility;

    private String source;

    private String sourceName;

    private List<String> errors = new ArrayList<String>();

    public static Email valueOf(org.orcid.jaxb.model.record_v2.Email e) {
        Email email = new Email();
        if (e != null) {
            email.setCurrent(e.isCurrent());
            email.setPrimary(e.isPrimary());
            email.setSource(e.retrieveSourcePath());
            email.setValue(e.getEmail());
            email.setVerified(e.isVerified());
            email.setVisibility(e.getVisibility());
        }
        return email;
    }
    
    public org.orcid.jaxb.model.record_v2.Email toV2Email() {
        org.orcid.jaxb.model.record_v2.Email email = new org.orcid.jaxb.model.record_v2.Email();
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
