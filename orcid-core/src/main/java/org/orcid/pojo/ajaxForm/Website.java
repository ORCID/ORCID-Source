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

import org.orcid.jaxb.model.common.Source;
import org.orcid.jaxb.model.common.Url;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;

public class Website implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    private String url;
    private String urlName;
    private String putCode;
    private Visibility visibility;
    private String source;
    private String sourceName;

    public static Website valueOf(ResearcherUrl researcherUrl) {
        Website form = new Website();

        if (researcherUrl != null) {
            if (!PojoUtil.isEmpty(researcherUrl.getUrl())) {
                form.setUrl(researcherUrl.getUrl().getValue());
            }

            if (!PojoUtil.isEmpty(researcherUrl.getUrlName())) {
                form.setUrl(researcherUrl.getUrlName());
            }

            if (researcherUrl.getVisibility() != null) {
                form.setVisibility(Visibility.valueOf(researcherUrl.getVisibility()));
            }

            if (researcherUrl.getPutCode() != null) {
                form.setPutCode(String.valueOf(researcherUrl.getPutCode()));
            }

            if (researcherUrl.getSource() != null) {
                // Set source
                form.setSource(researcherUrl.getSource().retrieveSourcePath());
                if (researcherUrl.getSource().getSourceName() != null) {
                    form.setSourceName(researcherUrl.getSource().getSourceName().getContent());
                }
            }
        }
        return form;
    }

    public ResearcherUrl toResearcherUrl() {
        ResearcherUrl researcherUrl = new ResearcherUrl();
        if (!PojoUtil.isEmpty(this.getUrl())) {
            researcherUrl.setUrl(new Url(this.getUrl()));
        }

        if (!PojoUtil.isEmpty(this.getUrlName())) {
            researcherUrl.setUrlName(this.getUrlName());
        }

        if (this.visibility != null && this.visibility.getVisibility() != null) {
            researcherUrl.setVisibility(org.orcid.jaxb.model.common.Visibility.fromValue(this.getVisibility().getVisibility().value()));
        }

        if (!PojoUtil.isEmpty(this.getPutCode())) {
            researcherUrl.setPutCode(Long.valueOf(this.getPutCode()));
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
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

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
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
}
