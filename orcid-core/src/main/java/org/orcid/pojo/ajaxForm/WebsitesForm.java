/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;

public class WebsitesForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private List<Website> websites = new ArrayList<Website>();
    
    private Visibility visibility;

    public static WebsitesForm valueOf(ResearcherUrls researcherUrls) {
        WebsitesForm w = new WebsitesForm();
        if (researcherUrls.getResearcherUrl() != null)
            for (ResearcherUrl ru : researcherUrls.getResearcherUrl()) {
                w.websites.add(Website.valueOf(ru));
            }
        if (researcherUrls.getVisibility() != null)
            w.setVisibility(Visibility.valueOf(researcherUrls.getVisibility()));
        return w;
    }

    public ResearcherUrls toResearcherUrls() {
        ResearcherUrls researcherUrls = new ResearcherUrls();
        List<ResearcherUrl> ruList = new ArrayList<ResearcherUrl>();
        for (Website website : websites) {
            ruList.add(website.toResearcherUrl());
        }
        researcherUrls.setResearcherUrl(ruList);
        if (visibility!= null)
            researcherUrls.setVisibility(visibility.getVisibility());
        return researcherUrls;
    }

    public List<Website> getWebsites() {
        return websites;
    }

    public void setWebsites(List<Website> websites) {
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
