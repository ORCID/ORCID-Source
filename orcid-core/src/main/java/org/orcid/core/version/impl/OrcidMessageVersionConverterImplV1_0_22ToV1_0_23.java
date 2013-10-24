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
package org.orcid.core.version.impl;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.jaxb.model.message.NewWorkType;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.WorkSubtype;
import org.orcid.jaxb.model.message.WorkTitle;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class OrcidMessageVersionConverterImplV1_0_22ToV1_0_23 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.0.22";
    private static final String TO_VERSION = "1.0.23";

    private static final String EMPTY_TITLE = "NOT_DEFINED";

    @Override
    public String getFromVersion() {
        return FROM_VERSION;
    }

    @Override
    public String getToVersion() {
        return TO_VERSION;
    }

    @Override
    public OrcidMessage downgradeMessage(OrcidMessage orcidMessage) {
        if (orcidMessage == null) {
            return null;
        }
        orcidMessage.setMessageVersion(FROM_VERSION);

        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        if (orcidProfile != null) {
            OrcidActivities orcidActivities = orcidProfile.getOrcidActivities();
            if (orcidActivities != null) {
                OrcidWorks orcidWorks = orcidActivities.getOrcidWorks();
                if (orcidWorks != null) {
                    for (OrcidWork orcidWork : orcidWorks.getOrcidWork()) {
                        NewWorkType downgradedWorkType = downgradeWorkType(orcidWork.getWorkType(), orcidWork.getWorkSubtype());                        
                        //Downgrade work type
                        orcidWork.setWorkType(downgradedWorkType);
                        //Remove the work subtype
                        orcidWork.setWorkSubtype(null);
                    }
                }
            }
        }

        return orcidMessage;
    }

    @Override
    public OrcidMessage upgradeMessage(OrcidMessage orcidMessage) {
        if (orcidMessage == null) {
            return null;
        }
        orcidMessage.setMessageVersion(TO_VERSION);

        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        if (orcidProfile != null) {
            OrcidActivities orcidActivities = orcidProfile.getOrcidActivities();
            if (orcidActivities != null) {
                OrcidWorks orcidWorks = orcidActivities.getOrcidWorks();
                if (orcidWorks != null) {
                    for (OrcidWork orcidWork : orcidWorks.getOrcidWork()) {
                        //Upgrade work type and subtype for each work
                        NewWorkType updatedWorkType = upgradeWorkType(orcidWork.getWorkType());
                        WorkSubtype workSubtype = upgradeWorkSubtype(orcidWork.getWorkType());
                        orcidWork.setWorkType(updatedWorkType);
                        orcidWork.setWorkSubtype(workSubtype);
                    }
                }
            }
        }
        return orcidMessage;
    }

    /**
     * Determine the work type based on the old deprecated workType
     * */
    private NewWorkType upgradeWorkType(NewWorkType oldWorkType) {
        // If it is not deprecated, it is not an old type
        if (!oldWorkType.isDeprecated())
            return oldWorkType;
        switch (oldWorkType) {
        case ADVERTISEMENT:
            return NewWorkType.OTHER_OUTPUT;
        case AUDIOVISUAL:
            return NewWorkType.OTHER_OUTPUT;
        case BIBLE:
            return NewWorkType.OTHER_OUTPUT;
        case BOOK:
            return NewWorkType.PUBLICATION;
        case BROCHURE:
            return NewWorkType.OTHER_OUTPUT;
        case CARTOON_COMIC:
            return NewWorkType.OTHER_OUTPUT;
        case CHAPTER_ANTHOLOGY:
            return NewWorkType.PUBLICATION;
        case COMPONENTS:
            return NewWorkType.OTHER_OUTPUT;
        case CONFERENCE_PROCEEDINGS:
            return NewWorkType.CONFERENCE;
        case CONGRESSIONAL_PUBLICATION:
            return NewWorkType.OTHER_OUTPUT;
        case COURT_CASE:
            return NewWorkType.OTHER_OUTPUT;
        case DATABASE:
            return NewWorkType.OTHER_OUTPUT;
        case DICTIONARY_ENTRY:
            return NewWorkType.PUBLICATION;
        case DIGITAL_IMAGE:
            return NewWorkType.PUBLICATION;
        case DISSERTATON_ABSTRACT:
            return NewWorkType.PUBLICATION;
        case DISSERTATION:
            return NewWorkType.PUBLICATION;
        case EMAIL:
            return NewWorkType.OTHER_OUTPUT;
        case EDITORIAL:
            return NewWorkType.PUBLICATION;
        case ELECTRONIC_ONLY:
            return NewWorkType.PUBLICATION;
        case ENCYCLOPEDIA_ARTICLE:
            return NewWorkType.PUBLICATION;
        case EXECUTIVE_ORDER:
            return NewWorkType.OTHER_OUTPUT;
        case FEDERAL_BILL:
            return NewWorkType.OTHER_OUTPUT;
        case FEDERAL_REPORT:
            return NewWorkType.OTHER_OUTPUT;
        case FEDERAL_RULE:
            return NewWorkType.OTHER_OUTPUT;
        case FEDERAL_STATUTE:
            return NewWorkType.OTHER_OUTPUT;
        case FEDERAL_TESTIMONY:
            return NewWorkType.OTHER_OUTPUT;
        case FILM_MOVIE:
            return NewWorkType.OTHER_OUTPUT;
        case GOVERNMENT_PUBLICATION:
            return NewWorkType.OTHER_OUTPUT;
        case INTERVIEW:
            return NewWorkType.OTHER_OUTPUT;
        case JOURNAL_ARTICLE:
            return NewWorkType.PUBLICATION;
        case LECTURE_SPEECH:
            return NewWorkType.OTHER_OUTPUT;
        case LEGAL:
            return NewWorkType.OTHER_OUTPUT;
        case LETTER:
            return NewWorkType.OTHER_OUTPUT;
        case LIVE_PERFORMANCE:
            return NewWorkType.OTHER_OUTPUT;
        case MAGAZINE_ARTICLE:
            return NewWorkType.PUBLICATION;
        case MAILING_LIST:
            return NewWorkType.OTHER_OUTPUT;
        case MANUSCRIPT:
            return NewWorkType.OTHER_OUTPUT;
        case MAP_CHART:
            return NewWorkType.OTHER_OUTPUT;
        case MUSICAL_RECORDING:
            return NewWorkType.OTHER_OUTPUT;
        case NEWSGROUP:
            return NewWorkType.OTHER_OUTPUT;
        case NEWSLETTER:
            return NewWorkType.PUBLICATION;
        case NEWSPAPER_ARTICLE:
            return NewWorkType.PUBLICATION;
        case NON_PERIODICALS:
            return NewWorkType.OTHER_OUTPUT;
        case OTHER:
            return NewWorkType.OTHER_OUTPUT;
        case PAMPHLET:
            return NewWorkType.OTHER_OUTPUT;
        case PAINTING:
            return NewWorkType.OTHER_OUTPUT;
        case PATENT:
            return NewWorkType.INTELLECTUAL_PROPERTY;
        case PERIODICALS:
            return NewWorkType.PUBLICATION;
        case PHOTOGRAPH:
            return NewWorkType.OTHER_OUTPUT;
        case PRESSRELEASE:
            return NewWorkType.OTHER_OUTPUT;
        case RAW_DATA:
            return NewWorkType.OTHER_OUTPUT;
        case RELIGIOUS_TEXT:
            return NewWorkType.OTHER_OUTPUT;
        case REPORT:
            return NewWorkType.PUBLICATION;
        case REPORTS_WORKING_PAPERS:
            return NewWorkType.PUBLICATION;
        case REVIEW:
            return NewWorkType.PUBLICATION;
        case SCHOLARLY_PROJECT:
            return NewWorkType.OTHER_OUTPUT;
        case SOFTWARE:
            return NewWorkType.PUBLICATION;
        case STANDARDS:
            return NewWorkType.OTHER_OUTPUT;
        case TELEVISION_RADIO:
            return NewWorkType.OTHER_OUTPUT;
        case THESIS:
            return NewWorkType.PUBLICATION;
        case WEBSITE:
            return NewWorkType.PUBLICATION;
        case UNDEFINED:
            return NewWorkType.OTHER_OUTPUT;
        default:
            // This should never happens, but just in case
            return oldWorkType;
        }
    }

    /**
     * Determine the sub type based on the old deprecated workType
     * */
    private WorkSubtype upgradeWorkSubtype(NewWorkType oldWorkType) {
        if (!oldWorkType.isDeprecated())
            throw new IllegalArgumentException("WorkType: " + oldWorkType.value() + " is not deprecated");

        switch (oldWorkType) {
        case ADVERTISEMENT:
            return WorkSubtype.OTHER;
        case AUDIOVISUAL:
            return WorkSubtype.OTHER;
        case BIBLE:
            return WorkSubtype.OTHER;
        case BOOK:
            return WorkSubtype.BOOK;
        case BROCHURE:
            return WorkSubtype.OTHER;
        case CARTOON_COMIC:
            return WorkSubtype.ARTISTIC_PERFORMANCE;
        case CHAPTER_ANTHOLOGY:
            return WorkSubtype.BOOK_CHAPTER;
        case COMPONENTS:
            return WorkSubtype.OTHER;
        case CONFERENCE_PROCEEDINGS:
            return WorkSubtype.CONFERENCE_PAPER;
        case CONGRESSIONAL_PUBLICATION:
            return WorkSubtype.STANDARDS_AND_POLICY;
        case COURT_CASE:
            return WorkSubtype.OTHER;
        case DATABASE:
            return WorkSubtype.DATA_SET;
        case DICTIONARY_ENTRY:
            return WorkSubtype.DICTIONARY_ENTRY;
        case DIGITAL_IMAGE:
            return WorkSubtype.ONLINE_RESOURCE;
        case DISSERTATON_ABSTRACT:
            return WorkSubtype.DISSERTATION;
        case DISSERTATION:
            return WorkSubtype.DISSERTATION;
        case EMAIL:
            return WorkSubtype.OTHER;
        case EDITORIAL:
            return WorkSubtype.MAGAZINE_ARTICLE;
        case ELECTRONIC_ONLY:
            return WorkSubtype.ONLINE_RESOURCE;
        case ENCYCLOPEDIA_ARTICLE:
            return WorkSubtype.ENCYCLOPEDIA_ENTRY;
        case EXECUTIVE_ORDER:
            return WorkSubtype.STANDARDS_AND_POLICY;
        case FEDERAL_BILL:
            return WorkSubtype.STANDARDS_AND_POLICY;
        case FEDERAL_REPORT:
            return WorkSubtype.STANDARDS_AND_POLICY;
        case FEDERAL_RULE:
            return WorkSubtype.STANDARDS_AND_POLICY;
        case FEDERAL_STATUTE:
            return WorkSubtype.STANDARDS_AND_POLICY;
        case FEDERAL_TESTIMONY:
            return WorkSubtype.OTHER;
        case FILM_MOVIE:
            return WorkSubtype.ARTISTIC_PERFORMANCE;
        case GOVERNMENT_PUBLICATION:
            return WorkSubtype.STANDARDS_AND_POLICY;
        case INTERVIEW:
            return WorkSubtype.OTHER;
        case JOURNAL_ARTICLE:
            return WorkSubtype.JOURNAL_ARTICLE;
        case LECTURE_SPEECH:
            return WorkSubtype.LECTURE_SPEECH;
        case LEGAL:
            return WorkSubtype.OTHER;
        case LETTER:
            return WorkSubtype.OTHER;
        case LIVE_PERFORMANCE:
            return WorkSubtype.ARTISTIC_PERFORMANCE;
        case MAGAZINE_ARTICLE:
            return WorkSubtype.MAGAZINE_ARTICLE;
        case MAILING_LIST:
            return WorkSubtype.OTHER;
        case MANUSCRIPT:
            return WorkSubtype.OTHER;
        case MAP_CHART:
            return WorkSubtype.DATA_SET;
        case MUSICAL_RECORDING:
            return WorkSubtype.ARTISTIC_PERFORMANCE;
        case NEWSGROUP:
            return WorkSubtype.OTHER;
        case NEWSLETTER:
            return WorkSubtype.NEWSLETTER_ARTICLE;
        case NEWSPAPER_ARTICLE:
            return WorkSubtype.NEWSPAPER_ARTICLE;
        case NON_PERIODICALS:
            return WorkSubtype.OTHER;
        case OTHER:
            return WorkSubtype.OTHER;
        case PAMPHLET:
            return WorkSubtype.OTHER;
        case PAINTING:
            return WorkSubtype.ARTISTIC_PERFORMANCE;
        case PATENT:
            return WorkSubtype.PATENT;
        case PERIODICALS:
            return WorkSubtype.MAGAZINE_ARTICLE;
        case PHOTOGRAPH:
            return WorkSubtype.ARTISTIC_PERFORMANCE;
        case PRESSRELEASE:
            return WorkSubtype.OTHER;
        case RAW_DATA:
            return WorkSubtype.DATA_SET;
        case RELIGIOUS_TEXT:
            return WorkSubtype.OTHER;
        case REPORT:
            return WorkSubtype.REPORT;
        case REPORTS_WORKING_PAPERS:
            return WorkSubtype.WORKING_PAPER;
        case REVIEW:
            return WorkSubtype.BOOK_REVIEW;
        case SCHOLARLY_PROJECT:
            return WorkSubtype.OTHER;
        case SOFTWARE:
            return WorkSubtype.ONLINE_RESOURCE;
        case STANDARDS:
            return WorkSubtype.STANDARDS_AND_POLICY;
        case TELEVISION_RADIO:
            return WorkSubtype.ARTISTIC_PERFORMANCE;
        case THESIS:
            return WorkSubtype.SUPERVISED_STUDENT_PUBLICATION;
        case WEBSITE:
            return WorkSubtype.WEBSITE;
        case UNDEFINED:
            return WorkSubtype.OTHER;
        default:
            // This should never happens, but just in case
            return WorkSubtype.UNDEFINED;
        }
    }

    /**
     * Determine the old work type based on the new work type and subtype
     * */
    private NewWorkType downgradeWorkType(NewWorkType newWorkType, WorkSubtype workSubtype) {
        // If the work type is not deprecated it means it should not be
        // downgraded
        if (newWorkType.isDeprecated()) {
            return newWorkType;
        }

        switch (workSubtype) {
        case ARTISTIC_PERFORMANCE:
            return NewWorkType.LIVE_PERFORMANCE;
        case BOOK_CHAPTER:
            return NewWorkType.CHAPTER_ANTHOLOGY;
        case BOOK_REVIEW:
            return NewWorkType.REVIEW;
        case BOOK:
            return NewWorkType.BOOK;
        case CONFERENCE_ABSTRACT:
            return NewWorkType.UNDEFINED;
        case CONFERENCE_PAPER:
            return NewWorkType.CONFERENCE_PROCEEDINGS;
        case CONFERENCE_POSTER:
            return NewWorkType.UNDEFINED;
        case DATA_SET:
            return NewWorkType.DATABASE;
        case DICTIONARY_ENTRY:
            return NewWorkType.DICTIONARY_ENTRY;
        case DISCLOSURE:
            return NewWorkType.UNDEFINED;
        case DISSERTATION:
            return NewWorkType.DISSERTATION;
        case EDITED_BOOK:
            return NewWorkType.UNDEFINED;
        case ENCYCLOPEDIA_ENTRY:
            return NewWorkType.ENCYCLOPEDIA_ARTICLE;
        case INVENTION:
            return NewWorkType.UNDEFINED;
        case JOURNAL_ARTICLE:
            return NewWorkType.JOURNAL_ARTICLE;
        case JOURNAL_ISSUE:
            return NewWorkType.UNDEFINED;
        case LECTURE_SPEECH:
            return NewWorkType.LECTURE_SPEECH;
        case LICENSE:
            return NewWorkType.UNDEFINED;
        case MAGAZINE_ARTICLE:
            return NewWorkType.MAGAZINE_ARTICLE;
        case MANUAL:
            return NewWorkType.UNDEFINED;
        case NEWSLETTER_ARTICLE:
            return NewWorkType.NEWSLETTER;
        case NEWSPAPER_ARTICLE:
            return NewWorkType.NEWSPAPER_ARTICLE;
        case ONLINE_RESOURCE:
            return NewWorkType.ELECTRONIC_ONLY;
        case OTHER:
            return NewWorkType.OTHER;
        case PATENT:
            return NewWorkType.PATENT;
        case REGISTERED_COPYRIGHT:
            return NewWorkType.UNDEFINED;
        case REPORT:
            return NewWorkType.REPORT;
        case RESEARCH_TECHNIQUE:
            return NewWorkType.UNDEFINED;
        case RESEARCH_TOOL:
            return NewWorkType.UNDEFINED;
        case SPIN_OFF_COMPANY:
            return NewWorkType.UNDEFINED;
        case STANDARDS_AND_POLICY:
            return NewWorkType.GOVERNMENT_PUBLICATION;
        case SUPERVISED_STUDENT_PUBLICATION:
            return NewWorkType.THESIS;
        case TECHNICAL_STANDARD:
            return NewWorkType.UNDEFINED;
        case TEST:
            return NewWorkType.UNDEFINED;
        case TRADEMARK:
            return NewWorkType.UNDEFINED;
        case TRANSLATION:
            return NewWorkType.UNDEFINED;        
        case WEBSITE:
            return NewWorkType.WEBSITE;
        case WORKING_PAPER:
            return NewWorkType.REPORTS_WORKING_PAPERS;
        case UNDEFINED:
            return NewWorkType.UNDEFINED;
        default:
            return NewWorkType.UNDEFINED;
        }
    }
}
