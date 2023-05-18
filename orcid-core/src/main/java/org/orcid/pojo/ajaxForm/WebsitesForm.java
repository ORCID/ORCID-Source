package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;

public class WebsitesForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private List<WebsiteForm> websites = new ArrayList<WebsiteForm>();
    
    private Visibility visibility;

    public static WebsitesForm valueOf(ResearcherUrls researcherUrls) {
        WebsitesForm w = new WebsitesForm();
        if (researcherUrls.getResearcherUrls() != null) {
            for (ResearcherUrl ru : researcherUrls.getResearcherUrls()) {
                w.websites.add(WebsiteForm.valueOf(ru));
            }        
        }
        return w;
    }

    public ResearcherUrls toResearcherUrls() {
        ResearcherUrls researcherUrls = new ResearcherUrls();
        List<ResearcherUrl> ruList = new ArrayList<ResearcherUrl>();
        for (WebsiteForm website : websites) {
            ruList.add(website.toResearcherUrl());
        }
        researcherUrls.setResearcherUrls(ruList);
        return researcherUrls;
    }

    public List<WebsiteForm> getWebsites() {
        return websites;
    }

    public void setWebsites(List<WebsiteForm> websites) {
        this.websites = websites;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public boolean compare(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WebsitesForm other = (WebsitesForm) obj;

        if (websites != null && other.getWebsites() != null && websites.size() != other.getWebsites().size()) {
            return false;
        } else {
            for (int i = 0; i < websites.size(); i++) {
                if (!websites.get(i).compare(other.getWebsites().get(i))) {
                    return false;
                }
            }
        }
        return true;
    }
}
