package org.orcid.scheduler.loader.source.zenodo.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZenodoRecordsHitLinks {
    
    @JsonProperty("badge")
    private String badge;
    
    @JsonProperty("bucket")
    private String bucket;
    
    @JsonProperty("conceptbadge")
    private String conceptBadge;
    
    @JsonProperty("conceptdoi")
    private String conceptDoi;
    
    @JsonProperty("doi")
    private String doi;
    
    @JsonProperty("html")
    private String html;
    
    @JsonProperty("latest")
    private String latest;
    
    @JsonProperty("latest_html")
    private String latestHtml;
    
    @JsonProperty("self")
    private String self;

    @JsonProperty("badge")
    public String getBadge() {
        return badge;
    }

    @JsonProperty("badge")
    public void setBadge(String badge) {
        this.badge = badge;
    }

    @JsonProperty("bucket")
    public String getBucket() {
        return bucket;
    }

    @JsonProperty("bucket")
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @JsonProperty("conceptbadge")
    public String getConceptBadge() {
        return conceptBadge;
    }

    @JsonProperty("conceptbadge")
    public void setConceptBadge(String conceptBadge) {
        this.conceptBadge = conceptBadge;
    }

    @JsonProperty("conceptdoi")
    public String getConceptDoi() {
        return conceptDoi;
    }

    @JsonProperty("conceptdoi")
    public void setConceptDoi(String conceptDoi) {
        this.conceptDoi = conceptDoi;
    }

    @JsonProperty("doi")
    public String getDoi() {
        return doi;
    }

    @JsonProperty("doi")
    public void setDoi(String doi) {
        this.doi = doi;
    }

    @JsonProperty("html")
    public String getHtml() {
        return html;
    }

    @JsonProperty("html")
    public void setHtml(String html) {
        this.html = html;
    }

    @JsonProperty("latest")
    public String getLatest() {
        return latest;
    }

    @JsonProperty("latest")
    public void setLatest(String latest) {
        this.latest = latest;
    }

    @JsonProperty("latest_html")
    public String getLatestHtml() {
        return latestHtml;
    }
    
    @JsonProperty("latest_html")
    public void setLatestHtml(String latestHtml) {
        this.latestHtml = latestHtml;
    }

    @JsonProperty("self")
    public String getSelf() {
        return self;
    }

    @JsonProperty("self")
    public void setSelf(String self) {
        this.self = self;
    }

}
