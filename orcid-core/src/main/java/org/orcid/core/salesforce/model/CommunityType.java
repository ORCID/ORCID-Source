package org.orcid.core.salesforce.model;

/**
 * 
 * @author Will Simpson
 *
 */
public enum CommunityType {

    ASSOCIATION("Association"), FUNDER("Funder"), GOVERNMENT("Government"), OTHER("Other"), PUBLISHER("Publisher"), REPOSITORY("Repository/Profile Org"), RESEARCH_INSTITUTE("Research Institute");

    private final String value;

    CommunityType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CommunityType fromValue(String v) {
        for (CommunityType c : CommunityType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }

}
