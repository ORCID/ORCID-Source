package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.ContactPermission;

public class ContactsForm implements ErrorsInterface, Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    private String accountId;
    private List<Contact> contactsList;
    private Map<String, ContactPermission> permissionsByContactRoleId;
    private Map<String, String> roleMap;

    @Override
    public List<String> getErrors() {
        return errors;
    }

    @Override
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public List<Contact> getContactsList() {
        return contactsList;
    }

    public void setContactsList(List<Contact> contactsList) {
        this.contactsList = contactsList;
    }

    public Map<String, ContactPermission> getPermissionsByContactRoleId() {
        return permissionsByContactRoleId;
    }

    public void setPermissionsByContactRoleId(Map<String, ContactPermission> permissionsByContactRoleId) {
        this.permissionsByContactRoleId = permissionsByContactRoleId;
    }

    public Map<String, String> getRoleMap() {
        return roleMap;
    }

    public void setRoleMap(Map<String, String> roleMap) {
        this.roleMap = roleMap;
    }

}
