package org.orcid.core.salesforce.model;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Will Simpson
 *
 */
public class MemberDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    private Member member;
    private String parentOrgName;
    private String parentOrgSlug;
    private List<Integration> integrations;
    private List<Contact> contacts;
    private List<SubMember> subMembers;

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public String getParentOrgName() {
        return parentOrgName;
    }

    public void setParentOrgName(String parentOrgName) {
        this.parentOrgName = parentOrgName;
    }

    public String getParentOrgSlug() {
        return parentOrgSlug;
    }

    public void setParentOrgSlug(String parentOrgSlug) {
        this.parentOrgSlug = parentOrgSlug;
    }

    public List<Integration> getIntegrations() {
        return integrations;
    }

    public void setIntegrations(List<Integration> integrations) {
        this.integrations = integrations;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<SubMember> getSubMembers() {
        return subMembers;
    }

    public void setSubMembers(List<SubMember> subMembers) {
        this.subMembers = subMembers;
    }

}
