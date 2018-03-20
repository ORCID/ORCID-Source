package org.orcid.core.salesforce.model;

/**
 * 
 * @author Will Simpson
 *
 */
public enum ContactRoleType {
    
    MAIN_CONTACT("Main relationship contact (OFFICIAL)"), TECHNICAL_CONTACT("Technical contact"), INVOICE_CONTACT("Invoice contact"), COMMS_CONTACT(
            "Comms contact"), OTHER_CONTACT("Other contact"), AGREEMENT_SIGNATORY("Agreement signatory (OFFICIAL)");

    private final String value;

    ContactRoleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ContactRoleType fromValue(String v) {
        for (ContactRoleType c : ContactRoleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }

}
