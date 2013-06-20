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

import org.jbibtex.ParseException;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkSource;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.utils.BibtexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

public class Work implements ErrorsInterface, Serializable {
    
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

    protected String citationForDisplay;

    public static Work valueOf(OrcidWork orcidWork) {
        Work w = new Work();
        if (orcidWork.getPublicationDate() != null) 
            w.setPublicationDate(Date.valueOf(orcidWork.getPublicationDate()));
        if (orcidWork.getPutCode() !=null)
            w.setPutCode(Text.valueOf(orcidWork.getPutCode()));
        if (orcidWork.getShortDescription() != null)
            w.setShortDescription(Text.valueOf(orcidWork.getShortDescription()));
        if (orcidWork.getUrl() != null)
            w.setUrl(Text.valueOf(orcidWork.getUrl().getValue()));
        if (orcidWork.getVisibility() != null)
            w.setVisibility(Visibility.valueOf(orcidWork.getVisibility()));
        if (orcidWork.getWorkCitation() != null)
            w.setCitation(Citation.valueOf(orcidWork.getWorkCitation()));
        
        if (orcidWork.getWorkContributors() != null && orcidWork.getWorkContributors().getContributor() != null) {
            List<Contributor> contributors = new ArrayList<Contributor>();
            for ( org.orcid.jaxb.model.message.Contributor owContributor:orcidWork.getWorkContributors().getContributor()) {
                contributors.add(Contributor.valueOf(owContributor));
            }
            w.setContributors(contributors);
        }
        if (orcidWork.getWorkExternalIdentifiers() != null && orcidWork.getWorkExternalIdentifiers().getWorkExternalIdentifier() != null) {
            List<WorkExternalIdentifier> workExternalIdentifiers =  new ArrayList<WorkExternalIdentifier>();
            for (org.orcid.jaxb.model.message.WorkExternalIdentifier owWorkExternalIdentifier: orcidWork.getWorkExternalIdentifiers().getWorkExternalIdentifier()) {
                workExternalIdentifiers.add(WorkExternalIdentifier.valueOf(owWorkExternalIdentifier));
            }
            w.setWorkExternalIdentifiers(workExternalIdentifiers);
        }
        if (orcidWork.getWorkSource() != null) 
            w.setWorkSource(Text.valueOf(orcidWork.getWorkSource().getContent()));
        if (orcidWork.getWorkTitle() != null)
            w.setWorkTitle(WorkTitle.valueOf(orcidWork.getWorkTitle()));
        if (orcidWork.getWorkType() != null)
            w.setWorkType(Text.valueOf(orcidWork.getWorkType().value()));
        return w;
    }
    
    public OrcidWork toOrcidWork() {
        OrcidWork ow = new OrcidWork();
        if (this.getPublicationDate() != null)
            ow.setPublicationDate(this.getPublicationDate().toPublicationDate());
        if (this.getPutCode() != null)
            ow.setPutCode(this.getPutCode().getValue());
        if (this.getShortDescription() != null)
            ow.setShortDescription(this.shortDescription.getValue());
        if (this.getUrl() != null) 
            ow.setUrl(new Url(this.url.getValue()));
        if (this.getVisibility() != null)
            ow.setVisibility(this.getVisibility().getVisibility());
        if (this.getCitation() != null) 
            ow.setWorkCitation(this.citation.toCitiation());
        if (this.getContributors() != null) {
            List<org.orcid.jaxb.model.message.Contributor> cList =  new ArrayList<org.orcid.jaxb.model.message.Contributor>();
            for (Contributor c: this.getContributors()) {
                cList.add(c.toContributor());
                
            }
            ow.setWorkContributors(new WorkContributors(cList));
        }
        if (this.getWorkExternalIdentifiers() != null) {
            List<org.orcid.jaxb.model.message.WorkExternalIdentifier> wiList = new ArrayList<org.orcid.jaxb.model.message.WorkExternalIdentifier>();
            for (WorkExternalIdentifier wi:this.getWorkExternalIdentifiers()) {
                wiList.add(wi.toWorkExternalIdentifier());
            }
            ow.setWorkExternalIdentifiers(new WorkExternalIdentifiers(wiList));
        }
        if (this.getWorkSource() != null)
            ow.setWorkSource(new WorkSource(this.getWorkSource().getValue()));
        if (this.getWorkTitle() != null)
            ow.setWorkTitle(this.workTitle.toWorkTitle());
        if (this.getWorkType() != null) {
            ow.setWorkType(WorkType.fromValue(this.getWorkType().getValue()));
        }
        return ow;
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
