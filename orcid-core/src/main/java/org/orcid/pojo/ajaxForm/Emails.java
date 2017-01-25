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

public class Emails implements ErrorsInterface {
    private List<Email> emails = null;
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    public static Emails valueOf(org.orcid.jaxb.model.record_v2.Emails e) {
        Emails emails = new Emails();
        if (e != null && !e.getEmails().isEmpty()) {
            emails.setEmails(new ArrayList<Email>());
            for (org.orcid.jaxb.model.record_v2.Email v2Email : e.getEmails()) {
                emails.getEmails().add(Email.valueOf(v2Email));
            }
        }
        return emails;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }
}
