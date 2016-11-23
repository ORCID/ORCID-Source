package org.orcid.listener.persistence.util;

public enum AvailableBroker {
    AMAZON_S3("amazon_s3");
    
    private final String name;

    AvailableBroker(String n) {
        this.name = n;
    }

    public String value() {
        return name;
    }

    public static AvailableBroker fromValue(String v) {
        for (AvailableBroker c : AvailableBroker.values()) {
            if (c.name.equals(v.toLowerCase())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    @Override
    public String toString() {
        return name;
    }
}
