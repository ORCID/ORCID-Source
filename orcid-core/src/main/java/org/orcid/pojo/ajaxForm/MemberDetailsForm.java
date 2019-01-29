package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.salesforce.model.CommunityType;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.SubMember;

public class MemberDetailsForm implements ErrorsInterface, Serializable {
    private static final long serialVersionUID = 1L;

    private String accountId;
    private List<String> errors = new ArrayList<String>();
    private Text name;
    private Text website;
    private Text email; 
    private URL logo;
    private Text description;
    private Text community;
    private List<Contact> contactsList;
    private List<SubMember> subMembers;
    private Map<String, String> roleMap;
    private boolean isConsortiumLead;
    private boolean isAllowedFullAccess;

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

    public URL getLogo() {
        return logo;
    }


    public void setEmail(Text email) {
        this.email = email;
    }

    public void setLogo(URL logo) {
        this.logo = logo;
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

    public boolean isConsortiumLead() {
        return isConsortiumLead;
    }

    public void setConsortiumLead(boolean isConsortiumLead) {
        this.isConsortiumLead = isConsortiumLead;
    }

    public boolean isAllowedFullAccess() {
        return isAllowedFullAccess;
    }

    public void setAllowedFullAccess(boolean isAllowedFullAccess) {
        this.isAllowedFullAccess = isAllowedFullAccess;
    }

    public static MemberDetailsForm fromMemberDetails(MemberDetails memberDetails) {
        MemberDetailsForm form = new MemberDetailsForm();
        Member member = memberDetails.getMember();
        form.setAccountId(member.getId());
        form.setName(Text.valueOf(member.getPublicDisplayName()));
        URL websiteUrl = member.getWebsiteUrl();
        if (websiteUrl != null) {
            form.setWebsite(Text.valueOf(websiteUrl.toString()));
        }
        else {
            form.setWebsite(Text.valueOf((String) null));
        }
        form.setEmail(Text.valueOf(member.getPublicDisplayEmail()));
        form.setDescription(Text.valueOf(member.getDescription()));
        CommunityType researchCommunity = member.getResearchCommunity();
        form.setCommunity(Text.valueOf(researchCommunity != null ? researchCommunity.name() : null));
        form.setSubMembers(memberDetails.getSubMembers());
        String consortiumLeadId = member.getConsortiumLeadId();
        form.setConsortiumLead(consortiumLeadId == null || consortiumLeadId.equals(member.getId()));
        form.setLogo(member.getLogoUrl());
        return form;
    }

    public MemberDetails toMemberDetails() {
        MemberDetails memberDetails = new MemberDetails();
        Member member = new Member();
        memberDetails.setMember(member);
        member.setId(getAccountId());
        member.setPublicDisplayName(getName().getValue());
        Text website = getWebsite();
        if (website != null) {
            try {
                String websiteValue = website.getValue();
                if (StringUtils.isNotBlank(websiteValue)) {
                    if (!websiteValue.startsWith("http")) {
                        websiteValue = "http://" + websiteValue;
                    }
                    member.setWebsiteUrl(new URL(websiteValue));
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException("Error parsing website", e);
            }
        }
        member.setPublicDisplayEmail(getEmail().getValue());
        member.setDescription(getDescription().getValue());
        Text communityText = getCommunity();
        if (communityText != null) {
            member.setResearchCommunity(CommunityType.valueOf(communityText.getValue()));
        }
        return memberDetails;
    }

}
