package org.orcid.listener.persistence.util;

public enum AvailableBroker {
    /**
     * The name variable of the available broker should match the column name in
     * the record_status table.
     * 
     * The lastIndexedColumnName variable of the available broker should match
     * the column name to track the last indexed date in the record_status table
     */
    // @formatter:off
    DUMP_STATUS_1_2_API("api_1_2_dump_status", "api_1_2_dump_last_indexed"), 
    DUMP_STATUS_2_0_API("api_2_0_dump_status", "api_2_0_dump_last_indexed"), 
    DUMP_STATUS_2_0_ACTIVITIES_API("api_2_0_activities_dump_status", "api_2_0_activities_dump_last_indexed"), 
    SOLR("api_2_0_solr_status", "api_2_0_solr_last_indexed"),
    MONGO("mongo_status","mongo_last_indexed");
    // @formatter:on

    private final String name;
    private final String lastIndexedColumnName;

    AvailableBroker(String name, String lastIndexedColumnName) {
        this.name = name;
        this.lastIndexedColumnName = lastIndexedColumnName;
    }

    public String value() {
        return name;
    }

    public String getLastIndexedColumnName() {
        return lastIndexedColumnName;
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
