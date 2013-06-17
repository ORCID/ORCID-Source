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
import org.orcid.persistence.jpa.entities.WorkContributorEntity;
import org.orcid.pojo.ajaxForm.Citation;
import org.orcid.pojo.ajaxForm.Contributor;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.ErrorsInterface;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.Visibility;
import org.orcid.pojo.ajaxForm.WorkExternalIdentifier;
import org.orcid.pojo.ajaxForm.WorkTitle;
import org.orcid.utils.BibtexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

public class Work implements ErrorsInterface {
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Date publicationDate;

    private Visibility visibility;

    private Text putCode;

    private Text shortDescription;

    private Text url;

    private Citation citation;

    private List<Contributor> contributors;

    private List<WorkExternalIdentifier> workExternalIdentifiers;
    
    private Text workSource;
    
    private WorkTitle workTitle;

    private static Logger LOGGER = LoggerFactory.getLogger(Work.class);
    
    private Text workType;

    @XmlAttribute
    protected String citationForDisplay;

    public Work() {

    }

    public Work(OrcidWork orcidWork) {
        this.setPublicationDate(new Date(orcidWork.getPublicationDate()));      
        this.setPutCode(new Text(orcidWork.getPutCode()));
        this.setShortDescription(new Text(orcidWork.getShortDescription()));
        Text url = new Text();
        if (orcidWork.getUrl() != null) url.setValue(orcidWork.getUrl().getValue());
            this.setUrl(new Text(orcidWork.getUrl().getValue()));
        this.setVisibility(new Visibility(orcidWork.getVisibility()));
        this.setCitation(new Citation(orcidWork.getWorkCitation()));
        
        if (orcidWork.getWorkContributors() != null && orcidWork.getWorkContributors().getContributor() != null) {
            List<Contributor> contributors = new ArrayList<Contributor>();
            for ( org.orcid.jaxb.model.message.Contributor owContributor:orcidWork.getWorkContributors().getContributor()) {
                contributors.add(new Contributor(owContributor));
            }
            this.setContributors(contributors);
        }
        if (orcidWork.getWorkExternalIdentifiers() != null && orcidWork.getWorkExternalIdentifiers().getWorkExternalIdentifier() != null) {
            List<WorkExternalIdentifier> workExternalIdentifiers =  new ArrayList<WorkExternalIdentifier>();
            for (org.orcid.jaxb.model.message.WorkExternalIdentifier owWorkExternalIdentifier: orcidWork.getWorkExternalIdentifiers().getWorkExternalIdentifier()) {
                workExternalIdentifiers.add(new WorkExternalIdentifier(owWorkExternalIdentifier));
            }
            this.setWorkExternalIdentifiers(workExternalIdentifiers);
        }
        if (orcidWork.getWorkSource() != null) 
        this.setWorkSource(new Text(orcidWork.getWorkSource().getContent()));
        this.setWorkTitle(new WorkTitle(orcidWork.getWorkTitle()));
        this.setWorkType(new Text(orcidWork.getWorkType().value()));
    }

    /**
     * Return the Bibtex work citations in a readable format.
     * 
     * @return the bibtex citation converted into a readable string
     * */
    public String getCitationForDisplay() {
        if (this.citation != null
                && CitationType.BIBTEX.value().toLowerCase().equals(this.citation.getCitationType().toLowerCase())) {
            try {
                String result = BibtexUtils.toCitation(HtmlUtils.htmlUnescape(this.citation.getCitation()));
                return result;
            } catch (ParseException e) {
                LOGGER.info("Invalid BibTeX. Sending back as a string");
            }
        }
        if (this.citation != null) {
            return this.citation.getCitation();
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

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Text getPutCode() {
        return putCode;
    }

    public void setPutCode(Text putCode) {
        this.putCode = putCode;
    }

    public Text getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(Text shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Text getUrl() {
        return url;
    }

    public void setUrl(Text url) {
        this.url = url;
    }

    public Citation getCitation() {
        return citation;
    }

    public void setCitation(Citation citation) {
        this.citation = citation;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }

    public List<WorkExternalIdentifier> getWorkExternalIdentifiers() {
        return workExternalIdentifiers;
    }

    public void setWorkExternalIdentifiers(List<WorkExternalIdentifier> workExternalIdentifiers) {
        this.workExternalIdentifiers = workExternalIdentifiers;
    }

    public Text getWorkSource() {
        return workSource;
    }

    public void setWorkSource(Text workSource) {
        this.workSource = workSource;
    }

    public WorkTitle getWorkTitle() {
        return workTitle;
    }

    public void setWorkTitle(WorkTitle worksTitle) {
        this.workTitle = worksTitle;
    }

    public Text getWorkType() {
        return workType;
    }

    public void setWorkType(Text workType) {
        this.workType = workType;
    }
}
