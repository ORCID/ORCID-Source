package org.orcid.pojo.summary;

import org.orcid.jaxb.model.v3.release.common.VerificationDate;
import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.DateUtils;

public class EmailDomainSummary {
    private String value;

    public String verificationDate;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(String verificationDate) {
        this.verificationDate = verificationDate;
    }


    public static EmailDomainSummary valueOf(ProfileEmailDomainEntity pem) {
        EmailDomainSummary form = new EmailDomainSummary();

        if (pem != null) {
            if(!PojoUtil.isEmpty(pem.getEmailDomain())) {
                form.setValue(pem.getEmailDomain());
            }
            form.setVerificationDate(DateUtils.convertToXMLGregorianCalendar(pem.getDateCreated()).toString());
        }
        return form;
    }
}
