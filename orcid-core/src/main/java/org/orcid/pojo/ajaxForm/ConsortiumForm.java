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
import java.util.Map;

import org.orcid.core.salesforce.model.CommunityType;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.SubMember;

public class ConsortiumForm implements ErrorsInterface, Serializable {
    private static final long serialVersionUID = 1L;

    private String accountId;
    private List<String> errors = new ArrayList<String>();
    private Text name;
    private Text website;
    private Text email;
    private Text description;
    private Text community;
    private List<Contact> contactsList;
    private List<SubMember> subMembers;
    private Map<String, String> roleMap;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public Text getEmail() {
        return email;
    }

    public void setEmail(Text email) {
        this.email = email;
    }

    public Text getDescription() {
        return description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public Text getCommunity() {
        return community;
    }

    public void setCommunity(Text community) {
        this.community = community;
    }

    public List<Contact> getContactsList() {
        return contactsList;
    }

    public void setContactsList(List<Contact> contactsList) {
        this.contactsList = contactsList;
    }

    public List<SubMember> getSubMembers() {
        return subMembers;
    }

    public void setSubMembers(List<SubMember> subMembers) {
        this.subMembers = subMembers;
    }

    public Map<String, String> getRoleMap() {
        return roleMap;
    }

    public void setRoleMap(Map<String, String> roleMap) {
        this.roleMap = roleMap;
    }

    public static ConsortiumForm fromMemberDetails(MemberDetails memberDetails) {
        ConsortiumForm form = new ConsortiumForm();
        Member member = memberDetails.getMember();
        form.setAccountId(member.getId());
        form.setName(Text.valueOf(member.getPublicDisplayName()));
        form.setWebsite(Text.valueOf(member.getWebsiteUrl().toString()));
        form.setEmail(Text.valueOf(member.getPublicDisplayEmail()));
        form.setDescription(Text.valueOf(member.getDescription()));
        CommunityType researchCommunity = member.getResearchCommunity();
        if (researchCommunity != null) {
            form.setCommunity(Text.valueOf(researchCommunity.name()));
        }
        form.setSubMembers(memberDetails.getSubMembers());
        return form;
    }

    public MemberDetails toMemberDetails() {
        MemberDetails memberDetails = new MemberDetails();
        Member member = new Member();
        memberDetails.setMember(member);
        member.setId(getAccountId());
        member.setPublicDisplayName(getName().getValue());
        try {
            member.setWebsiteUrl(new URL(getWebsite().getValue()));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error parsing website", e);
        }
        member.setPublicDisplayEmail(getEmail().getValue());
        member.setDescription(getDescription().getValue());
        member.setResearchCommunity(CommunityType.valueOf(getCommunity().getValue()));
        return memberDetails;
    }

}
