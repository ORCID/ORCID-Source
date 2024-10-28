package org.orcid.pojo.summary;

import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class EmailDomainSummary {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    
    public static EmailDomainSummary valueOf(ProfileEmailDomainEntity pem) {
        EmailDomainSummary form = new EmailDomainSummary();

        if (pem != null) {
            if(!PojoUtil.isEmpty(pem.getEmailDomain())) {
                form.setValue(pem.getEmailDomain());
            }
        }
        return form;
    }
    
   
}
