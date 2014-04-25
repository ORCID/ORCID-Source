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
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.UrlName;

public class Website implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text name;

    private Text url;

    public static Website valueOf(ResearcherUrl researcherUrl) {
        Website w = new Website();
        if (!PojoUtil.isEmpty(researcherUrl.getUrl()))
            w.setUrl(Text.valueOf(researcherUrl.getUrl().getValue()));
        if (!PojoUtil.isEmpty(researcherUrl.getUrlName()))
            w.setName(Text.valueOf(researcherUrl.getUrlName().getContent()));
        return w;
    }

    public ResearcherUrl toResearcherUrl() {
        ResearcherUrl ru = new ResearcherUrl();
        if (!PojoUtil.isEmpty(url))
            ru.setUrl(new Url(url.getValue()));
        if (!PojoUtil.isEmpty(name))
            ru.setUrlName(new UrlName(name.getValue()));
        return ru;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getName() {
        return name;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public Text getUrl() {
        return url;
    }

    public void setUrl(Text url) {
        this.url = url;
    }

}
