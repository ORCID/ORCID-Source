package org.orcid.scheduler.loader.source.zenodo.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZenodoRecordsHit {
    
    @JsonProperty("conceptdoi")
    private String conceptdoi;
    
    @JsonProperty("conceptrecid")
    private String conceptrecid;
    
    @JsonProperty("doi")
    private String doi;
    
    @JsonProperty("created")
    private String created;
    
    @JsonProperty("files")
    private List<ZenodoRecordsFile> files;
    
    @JsonProperty("id")
    private Long id;
    
    public String getConceptdoi() {
        return conceptdoi;
    }

    public void setConceptdoi(String conceptdoi) {
        this.conceptdoi = conceptdoi;
    }

    public String getConceptrecid() {
        return conceptrecid;
    }

    public void setConceptrecid(String conceptrecid) {
        this.conceptrecid = conceptrecid;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public List<ZenodoRecordsFile> getFiles() {
        return files;
    }

    public void setFiles(List<ZenodoRecordsFile> files) {
        this.files = files;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZenodoRecordsHitLinks getLinks() {
        return links;
    }

    public void setLinks(ZenodoRecordsHitLinks links) {
        this.links = links;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    @JsonProperty("links")
    private ZenodoRecordsHitLinks links;
    
    @JsonProperty("updated")
    private String updated;

}
