package org.orcid.scheduler.loader.source.zenodo.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZenodoRecordsFile {
    
    @JsonProperty("bucket")
    private String bucket;
    
    @JsonProperty("checksum")
    private String checksum;
    
    @JsonProperty("key")
    private String key;
    
    @JsonProperty("links")
    private ZenodoRecordsFileLinks links;
    
    @JsonProperty("size")
    private String Long;
    
    @JsonProperty("type")
    private String type;

    @JsonProperty("bucket")
    public String getBucket() {
        return bucket;
    }

    @JsonProperty("bucket")
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @JsonProperty("checksum")
    public String getChecksum() {
        return checksum;
    }

    @JsonProperty("checksum")
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("links")
    public ZenodoRecordsFileLinks getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(ZenodoRecordsFileLinks links) {
        this.links = links;
    }

    @JsonProperty("size")
    public String getLong() {
        return Long;
    }

    @JsonProperty("size")
    public void setLong(String l) {
        Long = l;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

}
