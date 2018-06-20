package org.orcid.core.salesforce.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @author Will Simpson
 *
 */
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String accountId;
    private String firstName;
    private String lastName;
    private String email;
    private ContactRole role;
    private String orcid;
    private Member member;
    private boolean selfServiceEnabled;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ContactRole getRole() {
        return role;
    }

    public void setRole(ContactRole role) {
        this.role = role;
    }

    public boolean isMainContact() {
        return ContactRoleType.MAIN_CONTACT.equals(role);
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public boolean isSelfServiceEnabled() {
        return selfServiceEnabled;
    }

    public void setSelfServiceEnabled(boolean selfServiceEnabled) {
        this.selfServiceEnabled = selfServiceEnabled;
    }

    @Override
    public String toString() {
        return "Contact [id=" + id + ", accountId=" + accountId + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", role=" + role
                + ", orcid=" + orcid + ", selfServiceEnabled=" + selfServiceEnabled + "]";
    }

    public static Map<String, Contact> mapByContactRoleId(Collection<Contact> contacts) {
        return contacts.stream().collect(Collectors.toMap(c -> c.getRole().getId(), Function.identity()));
    }

}
