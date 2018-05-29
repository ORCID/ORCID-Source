package org.orcid.jaxb.model.v3.rc1.record;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Angel Montenegro 
 *         Date: 20/02/2014
 */
@XmlType(name = "contributorRole", namespace = "http://www.orcid.org/ns/funding")
@XmlEnum
public enum FundingContributorRole implements Serializable {

    @XmlEnumValue("lead")
    LEAD("lead"), @XmlEnumValue("co-lead")
    CO_LEAD("co_lead"), @XmlEnumValue("supported-by")
    SUPPORTED_BY("supported_by"), @XmlEnumValue("other-contribution")
    OTHER_CONTRIBUTION("other_contribution");

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
