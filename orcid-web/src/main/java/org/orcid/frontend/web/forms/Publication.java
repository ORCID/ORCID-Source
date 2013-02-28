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
package org.orcid.frontend.web.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.crossref.CrossRefContext;
import org.orcid.core.crossref.CrossRefMetadata;
import org.orcid.jaxb.model.message.Citation;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.ContributorAttributes;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Day;
import org.orcid.jaxb.model.message.Month;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.PublicationDate;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.message.Year;
import org.orcid.utils.DateUtils;

/**
 * 
 * @author Will Simpson
 * 
 */
public class Publication {

    private String doi;

    private String title;

    private String fullCitation;

    private String citationType = CitationType.FORMATTED_UNSPECIFIED.value();

    private String coins;

    private CrossRefContext crossRefContext;

    private boolean selected;

    public Publication() {
    }

    public Publication(CrossRefMetadata metadata) {
        doi = metadata.getDoi();
        fullCitation = metadata.getFullCitation();
        title = metadata.getTitle();
        coins = metadata.getCoins();
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public Publication(String doi) {
        this.doi = doi;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullCitation() {
        return fullCitation;
    }

    public String getTidyFullCitation() {
        if (fullCitation == null) {
            return null;
        }
        return fullCitation.replaceAll("^null,( , )*", "");
    }

    public String getCitationType() {
        return citationType;
    }

    public void setCitationType(String citationType) {
        this.citationType = citationType;
    }

    public void setFullCitation(String fullCitation) {
        this.fullCitation = fullCitation;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public CrossRefContext getCrossRefContext() {
        initCrossRefContext();
        return crossRefContext;
    }

    private void initCrossRefContext() {
        if (crossRefContext == null) {
            crossRefContext = new CrossRefContext(coins);
            crossRefContext.parse();
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public static List<Publication> createFromCrossRefMetadata(Collection<CrossRefMetadata> metadatas) {
        List<Publication> publications = new ArrayList<Publication>(metadatas.size());
        for (CrossRefMetadata metadata : metadatas) {
            publications.add(new Publication(metadata));
        }
        return publications;
    }

    public OrcidWork getOrcidWork() {
        initCrossRefContext();
        OrcidWork orcidWork = new OrcidWork();

        if (StringUtils.isNotBlank(doi)) {
            WorkExternalIdentifier doiExtId = new WorkExternalIdentifier();
            doiExtId.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
            doiExtId.setWorkExternalIdentifierId(new WorkExternalIdentifierId(doi));
            WorkExternalIdentifiers workExtIds = new WorkExternalIdentifiers();
            orcidWork.setWorkExternalIdentifiers(workExtIds);
            workExtIds.getWorkExternalIdentifier().add(doiExtId);
        }

        if (StringUtils.isNotBlank(title)) {
            WorkTitle workTitle = new WorkTitle();
            orcidWork.setWorkTitle(workTitle);
            workTitle.setTitle(new Title(title));
        }

        // Will throw an IllegalArgumentException if not valid
        CitationType cType = CitationType.fromValue(citationType);
        Citation citation = new Citation(fullCitation, cType);
        orcidWork.setWorkCitation(citation);

        String publicationDateString = crossRefContext.getDate();
        if (StringUtils.isNotBlank(publicationDateString)) {
            XMLGregorianCalendar publicationDateGregCal = DateUtils.convertToXMLGregorianCalendar(publicationDateString);
            if (publicationDateGregCal != null) {
                Year publicationyear = new Year(publicationDateGregCal.getYear());
                Month publicationMonth = publicationDateGregCal.getMonth() == DatatypeConstants.FIELD_UNDEFINED ? null : new Month(publicationDateGregCal.getMonth());
                Day publicationDay = publicationDateGregCal.getDay() == DatatypeConstants.FIELD_UNDEFINED ? null : new Day(publicationDateGregCal.getDay());
                orcidWork.setPublicationDate(new PublicationDate(publicationyear, publicationMonth, publicationDay));
            }
        }

        String author = crossRefContext.getAuthor();
        if (StringUtils.isNotBlank(author)) {
            WorkContributors workContributors = new WorkContributors();
            orcidWork.setWorkContributors(workContributors);
            Contributor contributor = new Contributor();
            workContributors.getContributor().add(contributor);
            contributor.setCreditName(new CreditName(author));
            ContributorAttributes contributorAttributes = new ContributorAttributes();
            contributor.setContributorAttributes(contributorAttributes);
            contributorAttributes.setContributorRole(ContributorRole.AUTHOR);
            contributorAttributes.setContributorSequence(SequenceType.FIRST);
        }

        return orcidWork;
    }

}
