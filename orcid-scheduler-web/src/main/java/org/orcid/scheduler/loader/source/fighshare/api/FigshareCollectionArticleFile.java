package org.orcid.scheduler.loader.source.fighshare.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "is_link_only", "name", "supplied_md5", "computed_md5", "id", "download_url", "size" })
public class FigshareCollectionArticleFile {

    @JsonProperty("is_link_only")
    private Boolean isLinkOnly;
    @JsonProperty("name")
    private String name;
    @JsonProperty("supplied_md5")
    private String suppliedMd5;
    @JsonProperty("computed_md5")
    private String computedMd5;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("download_url")
    private String downloadUrl;
    @JsonProperty("size")
    private Integer size;

    @JsonProperty("is_link_only")
    public Boolean getIsLinkOnly() {
        return isLinkOnly;
    }

    @JsonProperty("is_link_only")
    public void setIsLinkOnly(Boolean isLinkOnly) {
        this.isLinkOnly = isLinkOnly;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("supplied_md5")
    public String getSuppliedMd5() {
        return suppliedMd5;
    }

    @JsonProperty("supplied_md5")
    public void setSuppliedMd5(String suppliedMd5) {
        this.suppliedMd5 = suppliedMd5;
    }

    @JsonProperty("computed_md5")
    public String getComputedMd5() {
        return computedMd5;
    }

    @JsonProperty("computed_md5")
    public void setComputedMd5(String computedMd5) {
        this.computedMd5 = computedMd5;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("download_url")
    public String getDownloadUrl() {
        return downloadUrl;
    }

    @JsonProperty("download_url")
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @JsonProperty("size")
    public Integer getSize() {
        return size;
    }

    @JsonProperty("size")
    public void setSize(Integer size) {
        this.size = size;
    }

}
