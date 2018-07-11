package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.List;

import org.orcid.jaxb.model.v3.rc1.record.ResearchResource;
import org.orcid.pojo.OrgDisambiguated;

@Deprecated
public class ResearchResourceForm implements Serializable {

    private static final long serialVersionUID = 1L;

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Date getEndDate() {
        return endDate;
    }

    public List<OrgDisambiguated> getHosts() {
        return hosts;
    }

    public Text getPutCode() {
        return putCode;
    }

    public String getSource() {
        return source;
    }

    public String getSourceName() {
        return sourceName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Text getTitle() {
        return title;
    }

    public TranslatedTitleForm getTranslatedTitle() {
        return translatedTitle;
    }

    public Text getUrl() {
        return url;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public List<WorkExternalIdentifier> getWorkExternalIdentifiers() {
        return workExternalIdentifiers;
    }

    private Date endDate;
    private List<OrgDisambiguated> hosts;
    private Text putCode;
    private String source;
    private String sourceName;
    private Date startDate;
    private Text title;
    private TranslatedTitleForm translatedTitle;
    private Text url;
    private Visibility visibility;
    private List<WorkExternalIdentifier> workExternalIdentifiers;

    public static ResearchResourceForm valueOf(ResearchResource r) {
        return null;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setHosts(List<OrgDisambiguated> hosts) {
        this.hosts = hosts;
    }

    public void setPutCode(Text putCode) {
        this.putCode = putCode;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setTitle(Text title) {
        this.title = title;
    }

    public void setTranslatedTitle(TranslatedTitleForm tt) {
        this.translatedTitle = tt;
    }

    public void setUrl(Text url) {
        this.url = url;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;

    }

    public void setWorkExternalIdentifiers(List<WorkExternalIdentifier> workExternalIdentifiers) {
        this.workExternalIdentifiers = workExternalIdentifiers;
    }

}
