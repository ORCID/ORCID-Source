package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.dev1.common.Source;
import org.orcid.jaxb.model.v3.dev1.common.Url;
import org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl;

public class WebsiteForm extends VisibilityForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    private Text url;
    private String urlName;
    private String putCode;
    private Long displayIndex;
    private Date createdDate;
    private Date lastModified;
    private String source;
    private String sourceName;

    public static WebsiteForm valueOf(ResearcherUrl researcherUrl) {
        WebsiteForm form = new WebsiteForm();

        if (researcherUrl != null) {
            if (!PojoUtil.isEmpty(researcherUrl.getUrl())) {
                form.setUrl(Text.valueOf(researcherUrl.getUrl().getValue()));
            }

            if (!PojoUtil.isEmpty(researcherUrl.getUrlName())) {
                form.setUrlName(researcherUrl.getUrlName());
            }

            if (researcherUrl.getVisibility() != null) {
                form.setVisibility(Visibility.valueOf(researcherUrl.getVisibility()));
            }

            if (researcherUrl.getPutCode() != null) {
                form.setPutCode(String.valueOf(researcherUrl.getPutCode()));
            }

            if (researcherUrl.getCreatedDate() != null) {
                Date createdDate = new Date();
                createdDate.setYear(String.valueOf(researcherUrl.getCreatedDate().getValue().getYear()));
                createdDate.setMonth(String.valueOf(researcherUrl.getCreatedDate().getValue().getMonth()));
                createdDate.setDay(String.valueOf(researcherUrl.getCreatedDate().getValue().getDay()));
                form.setCreatedDate(createdDate);
            }

            if (researcherUrl.getLastModifiedDate() != null) {
                Date lastModifiedDate = new Date();
                lastModifiedDate.setYear(String.valueOf(researcherUrl.getLastModifiedDate().getValue().getYear()));
                lastModifiedDate.setMonth(String.valueOf(researcherUrl.getLastModifiedDate().getValue().getMonth()));
                lastModifiedDate.setDay(String.valueOf(researcherUrl.getLastModifiedDate().getValue().getDay()));
                form.setLastModified(lastModifiedDate);
            }

            if (researcherUrl.getSource() != null) {
                // Set source
                form.setSource(researcherUrl.getSource().retrieveSourcePath());
                if (researcherUrl.getSource().getSourceName() != null) {
                    form.setSourceName(researcherUrl.getSource().getSourceName().getContent());
                }
            }

            if (researcherUrl.getDisplayIndex() != null) {
                form.setDisplayIndex(researcherUrl.getDisplayIndex());
            } else {
                form.setDisplayIndex(0L);
            }
        }
        return form;
    }

    public ResearcherUrl toResearcherUrl() {
        ResearcherUrl researcherUrl = new ResearcherUrl();
        if (!PojoUtil.isEmpty(this.getUrl())) {
            researcherUrl.setUrl(new Url(this.getUrl().getValue()));
        }

        if (!PojoUtil.isEmpty(this.getUrlName())) {
            researcherUrl.setUrlName(this.getUrlName());
        }

        if (this.visibility != null && this.visibility.getVisibility() != null) {
            researcherUrl.setVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.fromValue(this.getVisibility().getVisibility().value()));
        }

        if (!PojoUtil.isEmpty(this.getPutCode())) {
            researcherUrl.setPutCode(Long.valueOf(this.getPutCode()));
        }

        if (displayIndex != null) {
            researcherUrl.setDisplayIndex(displayIndex);
        } else {
            researcherUrl.setDisplayIndex(0L);
        }

        researcherUrl.setSource(new Source(source));
        return researcherUrl;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getUrl() {
        return url;
    }

    public void setUrl(Text url) {
        this.url = url;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    public String getPutCode() {
        return putCode;
    }

    public void setPutCode(String putCode) {
        this.putCode = putCode;
    }
    
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Long getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(Long displayIndex) {
        this.displayIndex = displayIndex;
    }
}
