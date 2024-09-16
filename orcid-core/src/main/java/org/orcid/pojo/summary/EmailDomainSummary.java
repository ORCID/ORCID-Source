package org.orcid.pojo.summary;

import java.util.Date;

import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class EmailDomainSummary {
    private String value;
    private Date createdDate;
    private Date lastModified;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    
    public static EmailDomainSummary valueOf(ProfileEmailDomainEntity pem) {
        EmailDomainSummary form = new EmailDomainSummary();

        if (pem != null) {
            if(!PojoUtil.isEmpty(pem.getEmailDomain())) {
                form.setValue(pem.getEmailDomain());
            }

            if (pem.getDateCreated() != null) {
                form.setCreatedDate(pem.getDateCreated());
            }

            if (pem.getLastModified() !=null) {
                form.setLastModified(pem.getLastModified());
            }
        }
        return form;
    }
    
   
}
