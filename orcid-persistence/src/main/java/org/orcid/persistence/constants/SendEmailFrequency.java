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
    Float floatValue;

    private SendEmailFrequency(String value) {
        this.value = value;
        this.floatValue = Float.valueOf(value);
    }

    public String value() {
        return value;
    }
    
    public Float floatValue() {
        return floatValue;
    }
    
    public static SendEmailFrequency fromValue(String frequency) {
        for(SendEmailFrequency f : SendEmailFrequency.values()) {
            if(f.value.equals(frequency)) {
                return f;
            }
        }
        throw new IllegalArgumentException("Invalid frequency " + frequency);
    }

    public static SendEmailFrequency fromValue(Float frequency) {
        for(SendEmailFrequency f : SendEmailFrequency.values()) {
            if(f.floatValue.equals(frequency)) {
                return f;
            }
        }
        throw new IllegalArgumentException("Invalid frequency " + frequency);
    }
}
