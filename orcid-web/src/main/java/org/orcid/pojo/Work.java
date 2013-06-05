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
package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang.StringUtils;
import org.jbibtex.ParseException;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.pojo.ajaxForm.ErrorsInterface;
import org.orcid.utils.BibtexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

public class Work extends OrcidWork implements ErrorsInterface {
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private static Logger LOGGER = LoggerFactory.getLogger(Work.class);
    
    @XmlAttribute
    protected String citationForDisplay;

    public Work() {

    }

    public Work(OrcidWork orcidWork) {
        this.setPublicationDate(orcidWork.getPublicationDate());
        this.setPutCode(orcidWork.getPutCode());
        this.setShortDescription(orcidWork.getShortDescription());
        this.setUrl(orcidWork.getUrl());
        this.setVisibility(orcidWork.getVisibility());
        this.setWorkCitation(orcidWork.getWorkCitation());
        this.setWorkContributors(orcidWork.getWorkContributors());
        this.setWorkExternalIdentifiers(orcidWork.getWorkExternalIdentifiers());
        this.setWorkSource(orcidWork.getWorkSource());
        this.setWorkTitle(orcidWork.getWorkTitle());
        this.setWorkType(orcidWork.getWorkType());
    }

    /**
     * Return the Bibtex work citations in a readable format.
     * 
     * @return the bibtex citation converted into a readable string
     * */
    public String getCitationForDisplay() {
        if (this.workCitation != null && this.workCitation.getCitation() != null
                && CitationType.BIBTEX.value().toLowerCase().equals(this.workCitation.getWorkCitationType().value().toLowerCase())) {
            try {
                String result = BibtexUtils.toCitation(HtmlUtils.htmlUnescape(this.workCitation.getCitation()));
                return result;
            } catch (ParseException e) {
                LOGGER.info("Invalid BibTeX. Sending back as a string");
            }
        }
        if (this.workCitation != null && StringUtils.isNotBlank(this.workCitation.getCitation())) {
            return this.workCitation.getCitation();
        }
        return null;
    }

    public void setCitationForDisplay(String citation) {
        this.citationForDisplay = citation;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
