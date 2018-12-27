package org.orcid.jaxb.model.common;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Declan Newman (declan) Date: 07/08/2012
 */
@XmlType(name = "contributorRole")
@XmlEnum
public enum ContributorRole implements Serializable {

    AUTHOR("author"), 
    ASSIGNEE("assignee"), 
    EDITOR("editor"), 
    CHAIR_OR_TRANSLATOR("chair-or-translator"), 
    CO_INVESTIGATOR("co-investigator"), 
    CO_INVENTOR("co-inventor"), 
    GRADUATE_STUDENT("graduate-student"), 
    OTHER_INVENTOR("other-inventor"), 
    PRINCIPAL_INVESTIGATOR("principal-investigator"), 
    POSTDOCTORAL_RESEARCHER("postdoctoral-researcher"), 
    SUPPORT_STAFF("support-staff");

    private final String value;

    ContributorRole(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
    
    public static ContributorRole fromValue(String v) {
        for (ContributorRole c : ContributorRole.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
