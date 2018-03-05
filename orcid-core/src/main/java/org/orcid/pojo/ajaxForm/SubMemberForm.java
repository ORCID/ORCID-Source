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

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;

/**
 * 
 * @author Will Simpson
 *
 */
public class SubMemberForm implements ErrorsInterface, Serializable {
    private static final long serialVersionUID = 1L;

    private String accountId;
    private String parentAccountId;
    private List<String> errors = new ArrayList<String>();
    private Text name;
    private Text website;
    private Text initialContactFirstName;
    private Text initialContactLastName;
    private Text initialContactEmail;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getParentAccountId() {
        return parentAccountId;
    }

    public void setParentAccountId(String parentAccountId) {
        this.parentAccountId = parentAccountId;
    }

    @Override
    public List<String> getErrors() {
        return errors;
    }

    @Override
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getName() {
        return name;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public Text getWebsite() {
        return website;
    }

    public void setWebsite(Text website) {
        this.website = website;
    }

    public Text getInitialContactFirstName() {
        return initialContactFirstName;
    }

    public void setInitialContactFirstName(Text initialContactFirstName) {
        this.initialContactFirstName = initialContactFirstName;
    }

    public Text getInitialContactLastName() {
        return initialContactLastName;
    }

    public void setInitialContactLastName(Text initialContactLastName) {
        this.initialContactLastName = initialContactLastName;
    }

    public Text getInitialContactEmail() {
        return initialContactEmail;
    }

    public void setInitialContactEmail(Text initialContactEmail) {
        this.initialContactEmail = initialContactEmail;
    }

    public static SubMemberForm fromMemberDetails(MemberDetails memberDetails) {
        SubMemberForm form = new SubMemberForm();
        Member member = memberDetails.getMember();
        form.setAccountId(member.getId());
        form.setName(Text.valueOf(member.getPublicDisplayName()));
        form.setWebsite(Text.valueOf(member.getWebsiteUrl().toString()));
        return form;
    }

    public Member toMember() {
        Member member = new Member();
        member.setConsortiumLeadId(getParentAccountId());
        String trimmedName = getName().getValue().trim();
        member.setName(trimmedName.substring(0, 41));
        member.setPublicDisplayName(trimmedName);
        try {
            String websiteValue = getWebsite().getValue();
            if (!websiteValue.startsWith("http")) {
                websiteValue = "http://" + websiteValue;
            }
            member.setWebsiteUrl(new URL(websiteValue));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error parsing website", e);
        }
        String initialContactEmailValue = getInitialContactEmail().getValue();
        
        if (initialContactEmailValue.indexOf("@") > -1) {
            String emailDomainValue = initialContactEmailValue.substring(initialContactEmailValue.indexOf("@") + 1);
            member.setEmailDomains(emailDomainValue);
        } 
        
        return member;
    }

    public Contact toContact() {
        Contact contact = new Contact();
        contact.setFirstName(initialContactFirstName.getValue());
        contact.setLastName(initialContactLastName.getValue());
        contact.setEmail(initialContactEmail.getValue());
        return contact;
    }

}
