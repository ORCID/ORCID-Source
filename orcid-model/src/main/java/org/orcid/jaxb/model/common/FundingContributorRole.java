package org.orcid.jaxb.model.common;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Angel Montenegro 
 *         Date: 20/02/2014
 */
@XmlType(name = "contributorRole", namespace = "http://www.orcid.org/ns/funding")
@XmlEnum
public enum FundingContributorRole implements Serializable {

    LEAD("lead"), 
    CO_LEAD("co-lead"), 
    SUPPORTED_BY("supported-by"), 
    OTHER_CONTRIBUTION("other-contribution");

    private final String value;

    FundingContributorRole(String v) {
        value = v;
    }
    
    public String value() {
        return value;
    }
    
    public static FundingContributorRole fromValue(String v) {
        for (FundingContributorRole c : FundingContributorRole.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
