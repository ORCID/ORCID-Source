package org.orcid.scheduler.loader.source.fighshare.api;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "defined_type_name", "handle", "url_private_html", "timeline", "url_private_api", "url_public_api", "id", "doi", "thumb", "title", "url",
        "defined_type", "resource_title", "url_public_html", "resource_doi", "published_date", "group_id" })
public class FigshareCollectionArticleSummary {

    @JsonProperty("defined_type_name")
    private String definedTypeName;
    @JsonProperty("handle")
    private String handle;
    @JsonProperty("url_private_html")
    private String urlPrivateHtml;
    @JsonProperty("timeline")
    private FigshareCollectionTimeline timeline;
    @JsonProperty("url_private_api")
    private String urlPrivateApi;
    @JsonProperty("url_public_api")
    private String urlPublicApi;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("doi")
    private String doi;
    @JsonProperty("thumb")
    private String thumb;
    @JsonProperty("title")
    private String title;
    @JsonProperty("url")
    private String url;
    @JsonProperty("defined_type")
    private Integer definedType;
    @JsonProperty("resource_title")
    private Object resourceTitle;
    @JsonProperty("url_public_html")
    private String urlPublicHtml;
    @JsonProperty("resource_doi")
    private Object resourceDoi;
    @JsonProperty("published_date")
    private String publishedDate;
    @JsonProperty("group_id")
    private Integer groupId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("defined_type_name")
    public String getDefinedTypeName() {
        return definedTypeName;
    }

    @JsonProperty("defined_type_name")
    public void setDefinedTypeName(String definedTypeName) {
        this.definedTypeName = definedTypeName;
    }

    @JsonProperty("handle")
    public String getHandle() {
        return handle;
    }

    @JsonProperty("handle")
    public void setHandle(String handle) {
        this.handle = handle;
    }

    @JsonProperty("url_private_html")
    public String getUrlPrivateHtml() {
        return urlPrivateHtml;
    }

    @JsonProperty("url_private_html")
    public void setUrlPrivateHtml(String urlPrivateHtml) {
        this.urlPrivateHtml = urlPrivateHtml;
    }

    @JsonProperty("timeline")
    public FigshareCollectionTimeline getTimeline() {
        return timeline;
    }

    @JsonProperty("timeline")
    public void setTimeline(FigshareCollectionTimeline timeline) {
        this.timeline = timeline;
    }

    @JsonProperty("url_private_api")
    public String getUrlPrivateApi() {
        return urlPrivateApi;
    }

    @JsonProperty("url_private_api")
    public void setUrlPrivateApi(String urlPrivateApi) {
        this.urlPrivateApi = urlPrivateApi;
    }

    @JsonProperty("url_public_api")
    public String getUrlPublicApi() {
        return urlPublicApi;
    }

    @JsonProperty("url_public_api")
    public void setUrlPublicApi(String urlPublicApi) {
        this.urlPublicApi = urlPublicApi;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("doi")
    public String getDoi() {
        return doi;
    }

    @JsonProperty("doi")
    public void setDoi(String doi) {
        this.doi = doi;
    }

    @JsonProperty("thumb")
    public String getThumb() {
        return thumb;
    }

    @JsonProperty("thumb")
    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("defined_type")
    public Integer getDefinedType() {
        return definedType;
    }

    @JsonProperty("defined_type")
    public void setDefinedType(Integer definedType) {
        this.definedType = definedType;
    }

    @JsonProperty("resource_title")
    public Object getResourceTitle() {
        return resourceTitle;
    }

    @JsonProperty("resource_title")
    public void setResourceTitle(Object resourceTitle) {
        this.resourceTitle = resourceTitle;
    }

    @JsonProperty("url_public_html")
    public String getUrlPublicHtml() {
        return urlPublicHtml;
    }

    @JsonProperty("url_public_html")
    public void setUrlPublicHtml(String urlPublicHtml) {
        this.urlPublicHtml = urlPublicHtml;
    }

    @JsonProperty("resource_doi")
    public Object getResourceDoi() {
        return resourceDoi;
    }

    @JsonProperty("resource_doi")
    public void setResourceDoi(Object resourceDoi) {
        this.resourceDoi = resourceDoi;
    }

    @JsonProperty("published_date")
    public String getPublishedDate() {
        return publishedDate;
    }

    @JsonProperty("published_date")
    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    @JsonProperty("group_id")
    public Integer getGroupId() {
        return groupId;
    }

    @JsonProperty("group_id")
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
// -----------------------------------com.example.Timeline.java-----------------------------------
//
// package com.example;
//
// import java.util.HashMap;
// import java.util.Map;
// import com.fasterxml.jackson.annotation.JsonAnyGetter;
// import com.fasterxml.jackson.annotation.JsonAnySetter;
// import com.fasterxml.jackson.annotation.JsonIgnore;
// import com.fasterxml.jackson.annotation.JsonInclude;
// import com.fasterxml.jackson.annotation.JsonProperty;
// import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//
// @JsonInclude(JsonInclude.Include.NON_NULL)
// @JsonPropertyOrder({
// "revision",
// "firstOnline",
// "posted"
// })
// public class Timeline {
//
// @JsonProperty("revision")
// private String revision;
// @JsonProperty("firstOnline")
// private String firstOnline;
// @JsonProperty("posted")
// private String posted;
// @JsonIgnore
// private Map<String, Object> additionalProperties = new HashMap<String,
// Object>();
//
// @JsonProperty("revision")
// public String getRevision() {
// return revision;
// }
//
// @JsonProperty("revision")
// public void setRevision(String revision) {
// this.revision = revision;
// }
//
// @JsonProperty("firstOnline")
// public String getFirstOnline() {
// return firstOnline;
// }
//
// @JsonProperty("firstOnline")
// public void setFirstOnline(String firstOnline) {
// this.firstOnline = firstOnline;
// }
//
// @JsonProperty("posted")
// public String getPosted() {
// return posted;
// }
//
// @JsonProperty("posted")
// public void setPosted(String posted) {
// this.posted = posted;
// }
//
// @JsonAnyGetter
// public Map<String, Object> getAdditionalProperties() {
// return this.additionalProperties;
// }
//
// @JsonAnySetter
// public void setAdditionalProperty(String name, Object value) {
// this.additionalProperties.put(name, value);
// }
//
// }