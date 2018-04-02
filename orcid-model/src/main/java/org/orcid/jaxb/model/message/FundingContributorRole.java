package org.orcid.jaxb.model.message;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

/**
 * @author Angel Montenegro 
 *         Date: 20/02/2014
 */
@XmlType(name = "funding-contributor-role")
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

    @JsonValue
    public String jsonValue() {
        return this.name();
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
