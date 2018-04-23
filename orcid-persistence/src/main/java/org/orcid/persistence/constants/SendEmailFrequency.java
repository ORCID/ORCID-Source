package org.orcid.persistence.constants;

/**
 * 
 * @author Will Simpson
 * 
 */
public enum SendEmailFrequency {

    IMMEDIATELY("0.0"), DAILY("1.0"), WEEKLY("7.0"), QUARTERLY("91.3105"), NEVER(String.valueOf(Float.MAX_VALUE));

    // Value in days
    String value;

    private SendEmailFrequency(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
