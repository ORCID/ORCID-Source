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
package org.orcid.core.salesforce.model;

/**
 * 
 * @author Will Simpson
 *
 */
public enum ContactRoleType {
    AGREEMENT_SIGNATORY("Agreement signatory (OFFICIAL)"), COMMS_CONTACT("Comms contact"), INVOICE_CONTACT("Invoice contact"),
    MAIN_CONTACT("Main relationship contact (OFFICIAL)"), OTHER_CONTACT("Other contact"), TECHNICAL_CONTACT("Technical contact");

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
