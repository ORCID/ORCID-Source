/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;

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

}
