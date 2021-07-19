package org.orcid.core.orgs.load.source.grid.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = { "embargo_options", "embargo_title" })
public class FigshareGridCollectionArticleDetails {

    @JsonProperty("defined_type_name")
    private String definedTypeName;
    @JsonProperty("embargo_date")
    private Object embargoDate;
    @JsonProperty("citation")
    private String citation;
    @JsonProperty("url_private_api")
    private String urlPrivateApi;
    @JsonProperty("embargo_reason")
    private String embargoReason;
    @JsonProperty("references")
    private List<String> references = null;
    @JsonProperty("funding_list")
    private List<Object> fundingList = null;
    @JsonProperty("url_public_api")
    private String urlPublicApi;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("custom_fields")
    private List<Object> customFields = null;
    @JsonProperty("size")
    private Integer size;
    @JsonProperty("metadata_reason")
    private String metadataReason;
    @JsonProperty("funding")
    private Object funding;
    @JsonProperty("figshare_url")
    private String figshareUrl;
    @JsonProperty("embargo_type")
    private String embargoType;
    @JsonProperty("title")
    private String title;
    @JsonProperty("defined_type")
    private Integer definedType;
    @JsonProperty("is_embargoed")
    private Boolean isEmbargoed;
    @JsonProperty("version")
    private Integer version;
    @JsonProperty("resource_doi")
    private Object resourceDoi;
    @JsonProperty("url_public_html")
    private String urlPublicHtml;
    @JsonProperty("confidential_reason")
    private String confidentialReason;
    @JsonProperty("files")
    private List<FigshareGridCollectionArticleFile> files = null;
    @JsonProperty("handle")
    private String handle;
    @JsonProperty("description")
    private String description;
    @JsonProperty("tags")
    private List<String> tags = null;
    @JsonProperty("timeline")
    private GridCollectionTimeline timeline;
    @JsonProperty("url_private_html")
    private String urlPrivateHtml;
    @JsonProperty("published_date")
    private String publishedDate;
    @JsonProperty("modified_date")
    private String modifiedDate;
    @JsonProperty("authors")
    private List<FigshareGridCollectionArticleAuthor> authors = null;
    @JsonProperty("is_public")
    private Boolean isPublic;
    @JsonProperty("categories")
    private List<FigshareGridCollectionArticleCategory> categories = null;
    @JsonProperty("thumb")
    private String thumb;
    @JsonProperty("is_confidential")
    private Boolean isConfidential;
    @JsonProperty("doi")
    private String doi;
    @JsonProperty("has_linked_file")
    private Boolean hasLinkedFile;
    @JsonProperty("license")
    private FigshareGridCollectionArticleLicense license;
    @JsonProperty("url")
    private String url;
    @JsonProperty("resource_title")
    private Object resourceTitle;
    @JsonProperty("status")
    private String status;
    @JsonProperty("created_date")
    private String createdDate;
    @JsonProperty("group_id")
    private Integer groupId;
    @JsonProperty("is_metadata_record")
    private Boolean isMetadataRecord;

    @JsonProperty("defined_type_name")
    public String getDefinedTypeName() {
        return definedTypeName;
    }

    @JsonProperty("defined_type_name")
    public void setDefinedTypeName(String definedTypeName) {
        this.definedTypeName = definedTypeName;
    }

    @JsonProperty("embargo_date")
    public Object getEmbargoDate() {
        return embargoDate;
    }

    @JsonProperty("embargo_date")
    public void setEmbargoDate(Object embargoDate) {
        this.embargoDate = embargoDate;
    }

    @JsonProperty("citation")
    public String getCitation() {
        return citation;
    }

    @JsonProperty("citation")
    public void setCitation(String citation) {
        this.citation = citation;
    }

    @JsonProperty("url_private_api")
    public String getUrlPrivateApi() {
        return urlPrivateApi;
    }

    @JsonProperty("url_private_api")
    public void setUrlPrivateApi(String urlPrivateApi) {
        this.urlPrivateApi = urlPrivateApi;
    }

    @JsonProperty("embargo_reason")
    public String getEmbargoReason() {
        return embargoReason;
    }

    @JsonProperty("embargo_reason")
    public void setEmbargoReason(String embargoReason) {
        this.embargoReason = embargoReason;
    }

    @JsonProperty("references")
    public List<String> getReferences() {
        return references;
    }

    @JsonProperty("references")
    public void setReferences(List<String> references) {
        this.references = references;
    }

    @JsonProperty("funding_list")
    public List<Object> getFundingList() {
        return fundingList;
    }

    @JsonProperty("funding_list")
    public void setFundingList(List<Object> fundingList) {
        this.fundingList = fundingList;
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

    @JsonProperty("custom_fields")
    public List<Object> getCustomFields() {
        return customFields;
    }

    @JsonProperty("custom_fields")
    public void setCustomFields(List<Object> customFields) {
        this.customFields = customFields;
    }

    @JsonProperty("size")
    public Integer getSize() {
        return size;
    }

    @JsonProperty("size")
    public void setSize(Integer size) {
        this.size = size;
    }

    @JsonProperty("metadata_reason")
    public String getMetadataReason() {
        return metadataReason;
    }

    @JsonProperty("metadata_reason")
    public void setMetadataReason(String metadataReason) {
        this.metadataReason = metadataReason;
    }

    @JsonProperty("funding")
    public Object getFunding() {
        return funding;
    }

    @JsonProperty("funding")
    public void setFunding(Object funding) {
        this.funding = funding;
    }

    @JsonProperty("figshare_url")
    public String getFigshareUrl() {
        return figshareUrl;
    }

    @JsonProperty("figshare_url")
    public void setFigshareUrl(String figshareUrl) {
        this.figshareUrl = figshareUrl;
    }

    @JsonProperty("embargo_type")
    public String getEmbargoType() {
        return embargoType;
    }

    @JsonProperty("embargo_type")
    public void setEmbargoType(String embargoType) {
        this.embargoType = embargoType;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("defined_type")
    public Integer getDefinedType() {
        return definedType;
    }

    @JsonProperty("defined_type")
    public void setDefinedType(Integer definedType) {
        this.definedType = definedType;
    }

    @JsonProperty("is_embargoed")
    public Boolean getIsEmbargoed() {
        return isEmbargoed;
    }

    @JsonProperty("is_embargoed")
    public void setIsEmbargoed(Boolean isEmbargoed) {
        this.isEmbargoed = isEmbargoed;
    }

    @JsonProperty("version")
    public Integer getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Integer version) {
        this.version = version;
    }

    @JsonProperty("resource_doi")
    public Object getResourceDoi() {
        return resourceDoi;
    }

    @JsonProperty("resource_doi")
    public void setResourceDoi(Object resourceDoi) {
        this.resourceDoi = resourceDoi;
    }

    @JsonProperty("url_public_html")
    public String getUrlPublicHtml() {
        return urlPublicHtml;
    }

    @JsonProperty("url_public_html")
    public void setUrlPublicHtml(String urlPublicHtml) {
        this.urlPublicHtml = urlPublicHtml;
    }

    @JsonProperty("confidential_reason")
    public String getConfidentialReason() {
        return confidentialReason;
    }

    @JsonProperty("confidential_reason")
    public void setConfidentialReason(String confidentialReason) {
        this.confidentialReason = confidentialReason;
    }

    @JsonProperty("files")
    public List<FigshareGridCollectionArticleFile> getFiles() {
        return files;
    }

    @JsonProperty("files")
    public void setFiles(List<FigshareGridCollectionArticleFile> files) {
        this.files = files;
    }

    @JsonProperty("handle")
    public String getHandle() {
        return handle;
    }

    @JsonProperty("handle")
    public void setHandle(String handle) {
        this.handle = handle;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("tags")
    public List<String> getTags() {
        return tags;
    }

    @JsonProperty("tags")
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @JsonProperty("timeline")
    public GridCollectionTimeline getTimeline() {
        return timeline;
    }

    @JsonProperty("timeline")
    public void setTimeline(GridCollectionTimeline timeline) {
        this.timeline = timeline;
    }

    @JsonProperty("url_private_html")
    public String getUrlPrivateHtml() {
        return urlPrivateHtml;
    }

    @JsonProperty("url_private_html")
    public void setUrlPrivateHtml(String urlPrivateHtml) {
        this.urlPrivateHtml = urlPrivateHtml;
    }

    @JsonProperty("published_date")
    public String getPublishedDate() {
        return publishedDate;
    }

    @JsonProperty("published_date")
    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    @JsonProperty("modified_date")
    public String getModifiedDate() {
        return modifiedDate;
    }

    @JsonProperty("modified_date")
    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @JsonProperty("authors")
    public List<FigshareGridCollectionArticleAuthor> getAuthors() {
        return authors;
    }

    @JsonProperty("authors")
    public void setAuthors(List<FigshareGridCollectionArticleAuthor> authors) {
        this.authors = authors;
    }

    @JsonProperty("is_public")
    public Boolean getIsPublic() {
        return isPublic;
    }

    @JsonProperty("is_public")
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    @JsonProperty("categories")
    public List<FigshareGridCollectionArticleCategory> getCategories() {
        return categories;
    }

    @JsonProperty("categories")
    public void setCategories(List<FigshareGridCollectionArticleCategory> categories) {
        this.categories = categories;
    }

    @JsonProperty("thumb")
    public String getThumb() {
        return thumb;
    }

    @JsonProperty("thumb")
    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    @JsonProperty("is_confidential")
    public Boolean getIsConfidential() {
        return isConfidential;
    }

    @JsonProperty("is_confidential")
    public void setIsConfidential(Boolean isConfidential) {
        this.isConfidential = isConfidential;
    }

    @JsonProperty("doi")
    public String getDoi() {
        return doi;
    }

    @JsonProperty("doi")
    public void setDoi(String doi) {
        this.doi = doi;
    }

    @JsonProperty("has_linked_file")
    public Boolean getHasLinkedFile() {
        return hasLinkedFile;
    }

    @JsonProperty("has_linked_file")
    public void setHasLinkedFile(Boolean hasLinkedFile) {
        this.hasLinkedFile = hasLinkedFile;
    }

    @JsonProperty("license")
    public FigshareGridCollectionArticleLicense getLicense() {
        return license;
    }

    @JsonProperty("license")
    public void setLicense(FigshareGridCollectionArticleLicense license) {
        this.license = license;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("resource_title")
    public Object getResourceTitle() {
        return resourceTitle;
    }

    @JsonProperty("resource_title")
    public void setResourceTitle(Object resourceTitle) {
        this.resourceTitle = resourceTitle;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("created_date")
    public String getCreatedDate() {
        return createdDate;
    }

    @JsonProperty("created_date")
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @JsonProperty("group_id")
    public Integer getGroupId() {
        return groupId;
    }

    @JsonProperty("group_id")
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @JsonProperty("is_metadata_record")
    public Boolean getIsMetadataRecord() {
        return isMetadataRecord;
    }

    @JsonProperty("is_metadata_record")
    public void setIsMetadataRecord(Boolean isMetadataRecord) {
        this.isMetadataRecord = isMetadataRecord;
    }
    
}
