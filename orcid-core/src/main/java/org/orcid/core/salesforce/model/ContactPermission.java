package org.orcid.core.salesforce.model;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @author Will Simpson
 *
 */
public class ContactPermission {

    private String contactRoleId;
    private boolean allowedEdit;

    public boolean isAllowedEdit() {
        return allowedEdit;
    }

    public void setAllowedEdit(boolean canEdit) {
        this.allowedEdit = canEdit;
    }

    public String getContactRoleId() {
        return contactRoleId;
    }

    public void setContactRoleId(String contactRoleId) {
        this.contactRoleId = contactRoleId;
    }

    @Override
    public String toString() {
        return "ContactPermission [contactRoleId=" + contactRoleId + ", allowedEdit=" + allowedEdit + "]";
    }

    public static Map<String, ContactPermission> mapByContactRoleId(Collection<ContactPermission> permissions) {
        return permissions.stream().collect(Collectors.toMap(ContactPermission::getContactRoleId, Function.identity()));
    }

}
