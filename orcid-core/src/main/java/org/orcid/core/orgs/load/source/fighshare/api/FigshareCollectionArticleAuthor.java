package org.orcid.core.orgs.load.source.fighshare.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "url_name", "is_active", "id", "full_name", "orcid_id" })
public class FigshareCollectionArticleAuthor {

    @JsonProperty("url_name")
    private String urlName;
    @JsonProperty("is_active")
    private Boolean isActive;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("orcid_id")
    private String orcidId;

    @JsonProperty("url_name")
    public String getUrlName() {
        return urlName;
    }

    @JsonProperty("url_name")
    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    @JsonProperty("is_active")
    public Boolean getIsActive() {
        return isActive;
    }

    @JsonProperty("is_active")
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("full_name")
    public String getFullName() {
        return fullName;
    }

    @JsonProperty("full_name")
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @JsonProperty("orcid_id")
    public String getOrcidId() {
        return orcidId;
    }

    @JsonProperty("orcid_id")
    public void setOrcidId(String orcidId) {
        this.orcidId = orcidId;
    }

}