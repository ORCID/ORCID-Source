package org.orcid.pojo.summary;

import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class EmailDomainSummary {
    private String value;
    private String createdDate;
    private String lastModified;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    
    public static EmailDomainSummary valueOf(ProfileEmailDomainEntity pem) {
        EmailDomainSummary form = new EmailDomainSummary();

        if (pem != null) {
            if(!PojoUtil.isEmpty(pem.getEmailDomain())) {
                form.setValue(pem.getEmailDomain());
            }

            if (pem.getDateCreated() != null) {
                form.setCreatedDate(org.orcid.pojo.ajaxForm.Date.valueOf(pem.getDateCreated()).toFuzzyDate().toString());
            }

            if (pem.getLastModified() !=null) {
                form.setLastModified(org.orcid.pojo.ajaxForm.Date.valueOf(pem.getLastModified()).toFuzzyDate().toString());
            }
        }
        return form;
    }
    
   
}
