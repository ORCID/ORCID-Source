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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.core.version.OrcidMessageVersionConverterChain;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.WorkType;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidMessageVersionConverterChainImpl implements OrcidMessageVersionConverterChain {

    public List<OrcidMessageVersionConverter> converters;
    public List<OrcidMessageVersionConverter> descendingConverters;
    private boolean supportOldWorkTypes = false;
    private static String MESSAGE_VERSION_WITH_NEW_WORK_TYPES = "1.1";

    public void setSupportOldWorkTypes(boolean value){
        this.supportOldWorkTypes = value;
    }
    
    public void setConverters(List<OrcidMessageVersionConverter> converters) {
        this.converters = converters;
        List<OrcidMessageVersionConverter> descendingConverters = new ArrayList<>(converters);
        Collections.reverse(descendingConverters);
        this.descendingConverters = descendingConverters;
    }

    @Override
    public OrcidMessage downgradeMessage(OrcidMessage orcidMessage, String requiredVersion) {
        if (orcidMessage == null) {
            return null;
        }
        for (OrcidMessageVersionConverter converter : descendingConverters) {
            String oldVersion = orcidMessage.getMessageVersion();
            if (requiredVersion.equals(oldVersion)) {
                break;
            }
            if (converter.getFromVersion().compareTo(requiredVersion) >= 0) {
                orcidMessage = converter.downgradeMessage(orcidMessage);
            }
        }
                
        if(supportOldWorkTypes) {
            //If the required message version is lower than V1.1, we need to downgrade the work type
            if(MESSAGE_VERSION_WITH_NEW_WORK_TYPES.compareTo(requiredVersion) > 0){            
                OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
                if (orcidProfile != null) {
                    OrcidActivities orcidActivities = orcidProfile.getOrcidActivities();
                    if (orcidActivities != null) {
                        OrcidWorks orcidWorks = orcidActivities.getOrcidWorks();
                        if (orcidWorks != null) {
                            for (OrcidWork orcidWork : orcidWorks.getOrcidWork()) {
                                WorkType updatedWorkType = null;
                                //Downgrade work type
                                updatedWorkType = downgradeWorkType(orcidWork.getWorkType());
                                orcidWork.setWorkType(updatedWorkType);                        
                            }
                        }
                    }
                }
            }
        }
        
        return orcidMessage;
    }
    
    @Override
    public OrcidMessage upgradeMessage(OrcidMessage orcidMessage, String requiredVersion) {
        if (orcidMessage == null) {
            return null;
        }
        for (OrcidMessageVersionConverter converter : converters) {
            String oldVersion = orcidMessage.getMessageVersion();
            if (requiredVersion.equals(oldVersion)) {
                break;
            }
            if (converter.getToVersion().compareTo(requiredVersion) <= 0) {
                orcidMessage = converter.upgradeMessage(orcidMessage);
            }
        }
        
        if(supportOldWorkTypes) {
            OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
            if (orcidProfile != null) {
                OrcidActivities orcidActivities = orcidProfile.getOrcidActivities();
                if (orcidActivities != null) {
                    OrcidWorks orcidWorks = orcidActivities.getOrcidWorks();
                    if (orcidWorks != null) {
                        for (OrcidWork orcidWork : orcidWorks.getOrcidWork()) {
                            //Upgrade work type
                            WorkType updatedWorkType = upgradeWorkType(orcidWork.getWorkType());                            
                            orcidWork.setWorkType(updatedWorkType);                        
                        }
                    }
                }
            }
        }
        
        return orcidMessage;
    }
    
    /**
     * Determine the sub type based on the old deprecated workType
     * */
    private WorkType upgradeWorkType(WorkType oldWorkType) {
        //If it is null, return undefined
        if(oldWorkType == null)
            return WorkType.OTHER;
        //If it is one of the new work types, just return it
        if (!oldWorkType.isDeprecated())
            return oldWorkType;

        switch (oldWorkType) {
        case ADVERTISEMENT:
            return WorkType.OTHER;
        case AUDIOVISUAL:
            return WorkType.OTHER;
        case BIBLE:
            return WorkType.OTHER;
        case BOOK:
            return WorkType.BOOK;
        case BROCHURE:
            return WorkType.OTHER;
        case CARTOON_COMIC:
            return WorkType.ARTISTIC_PERFORMANCE;
        case CHAPTER_ANTHOLOGY:
            return WorkType.BOOK_CHAPTER;
        case COMPONENTS:
            return WorkType.OTHER;
        case CONFERENCE_PROCEEDINGS:
            return WorkType.CONFERENCE_PAPER;
        case CONGRESSIONAL_PUBLICATION:
            return WorkType.STANDARDS_AND_POLICY;
        case COURT_CASE:
            return WorkType.OTHER;
        case DATABASE:
            return WorkType.DATA_SET;
        case DICTIONARY_ENTRY:
            return WorkType.DICTIONARY_ENTRY;
        case DIGITAL_IMAGE:
            return WorkType.ONLINE_RESOURCE;
        case DISSERTATON_ABSTRACT:
            return WorkType.DISSERTATION;
        case DISSERTATION:
            return WorkType.DISSERTATION;
        case EMAIL:
            return WorkType.OTHER;
        case EDITORIAL:
            return WorkType.MAGAZINE_ARTICLE;
        case ELECTRONIC_ONLY:
            return WorkType.ONLINE_RESOURCE;
        case ENCYCLOPEDIA_ARTICLE:
            return WorkType.ENCYCLOPEDIA_ENTRY;
        case EXECUTIVE_ORDER:
            return WorkType.STANDARDS_AND_POLICY;
        case FEDERAL_BILL:
            return WorkType.STANDARDS_AND_POLICY;
        case FEDERAL_REPORT:
            return WorkType.STANDARDS_AND_POLICY;
        case FEDERAL_RULE:
            return WorkType.STANDARDS_AND_POLICY;
        case FEDERAL_STATUTE:
            return WorkType.STANDARDS_AND_POLICY;
        case FEDERAL_TESTIMONY:
            return WorkType.OTHER;
        case FILM_MOVIE:
            return WorkType.ARTISTIC_PERFORMANCE;
        case GOVERNMENT_PUBLICATION:
            return WorkType.STANDARDS_AND_POLICY;
        case INTERVIEW:
            return WorkType.OTHER;
        case JOURNAL_ARTICLE:
            return WorkType.JOURNAL_ARTICLE;
        case LECTURE_SPEECH:
            return WorkType.LECTURE_SPEECH;
        case LEGAL:
            return WorkType.OTHER;
        case LETTER:
            return WorkType.OTHER;
        case LIVE_PERFORMANCE:
            return WorkType.ARTISTIC_PERFORMANCE;
        case MAGAZINE_ARTICLE:
            return WorkType.MAGAZINE_ARTICLE;
        case MAILING_LIST:
            return WorkType.OTHER;
        case MANUSCRIPT:
            return WorkType.OTHER;
        case MAP_CHART:
            return WorkType.DATA_SET;
        case MUSICAL_RECORDING:
            return WorkType.ARTISTIC_PERFORMANCE;
        case NEWSGROUP:
            return WorkType.OTHER;
        case NEWSLETTER:
            return WorkType.NEWSLETTER_ARTICLE;
        case NEWSPAPER_ARTICLE:
            return WorkType.NEWSPAPER_ARTICLE;
        case NON_PERIODICALS:
            return WorkType.OTHER;
        case OTHER:
            return WorkType.OTHER;
        case PAMPHLET:
            return WorkType.OTHER;
        case PAINTING:
            return WorkType.ARTISTIC_PERFORMANCE;
        case PATENT:
            return WorkType.PATENT;
        case PERIODICALS:
            return WorkType.MAGAZINE_ARTICLE;
        case PHOTOGRAPH:
            return WorkType.ARTISTIC_PERFORMANCE;
        case PRESSRELEASE:
            return WorkType.OTHER;
        case RAW_DATA:
            return WorkType.DATA_SET;
        case RELIGIOUS_TEXT:
            return WorkType.OTHER;
        case REPORT:
            return WorkType.REPORT;
        case REPORTS_WORKING_PAPERS:
            return WorkType.WORKING_PAPER;
        case REVIEW:
            return WorkType.BOOK_REVIEW;
        case SCHOLARLY_PROJECT:
            return WorkType.OTHER;
        case SOFTWARE:
            return WorkType.ONLINE_RESOURCE;
        case STANDARDS:
            return WorkType.STANDARDS_AND_POLICY;
        case TELEVISION_RADIO:
            return WorkType.ARTISTIC_PERFORMANCE;
        case THESIS:
            return WorkType.SUPERVISED_STUDENT_PUBLICATION;
        case WEBSITE:
            return WorkType.WEB_SITE;
        case UNDEFINED:
            return WorkType.OTHER;
        default:
            // This should never happens, but just in case
            return WorkType.OTHER;
        }
    }

    /**
     * Get a new work type and downgrade it to an old work type
     * */
    private WorkType downgradeWorkType(WorkType workType) {
        //This should never happen, but just in case
        if(workType == null)
            return WorkType.UNDEFINED;
        //If it is one of the old work types just return it
        if(workType.isDeprecated()){
                return workType;
        }
        
        switch (workType) {
        case ARTISTIC_PERFORMANCE:
            return WorkType.LIVE_PERFORMANCE;
        case BOOK_CHAPTER:
            return WorkType.CHAPTER_ANTHOLOGY;
        case BOOK_REVIEW:
            return WorkType.REVIEW;
        case BOOK:
            return WorkType.BOOK;
        case CONFERENCE_ABSTRACT:
            return WorkType.UNDEFINED;
        case CONFERENCE_PAPER:
            return WorkType.CONFERENCE_PROCEEDINGS;
        case CONFERENCE_POSTER:
            return WorkType.UNDEFINED;
        case DATA_SET:
            return WorkType.DATABASE;
        case DICTIONARY_ENTRY:
            return WorkType.DICTIONARY_ENTRY;
        case DISCLOSURE:
            return WorkType.UNDEFINED;
        case DISSERTATION:
            return WorkType.DISSERTATION;
        case EDITED_BOOK:
            return WorkType.UNDEFINED;
        case ENCYCLOPEDIA_ENTRY:
            return WorkType.ENCYCLOPEDIA_ARTICLE;
        case INVENTION:
            return WorkType.UNDEFINED;
        case JOURNAL_ARTICLE:
            return WorkType.JOURNAL_ARTICLE;
        case JOURNAL_ISSUE:
            return WorkType.UNDEFINED;
        case LECTURE_SPEECH:
            return WorkType.LECTURE_SPEECH;
        case LICENSE:
            return WorkType.UNDEFINED;
        case MAGAZINE_ARTICLE:
            return WorkType.MAGAZINE_ARTICLE;
        case MANUAL:
            return WorkType.UNDEFINED;
        case NEWSLETTER_ARTICLE:
            return WorkType.NEWSLETTER;
        case NEWSPAPER_ARTICLE:
            return WorkType.NEWSPAPER_ARTICLE;
        case ONLINE_RESOURCE:
            return WorkType.ELECTRONIC_ONLY;
        case OTHER:
            return WorkType.OTHER;
        case PATENT:
            return WorkType.PATENT;
        case REGISTERED_COPYRIGHT:
            return WorkType.UNDEFINED;
        case REPORT:
            return WorkType.REPORT;
        case RESEARCH_TECHNIQUE:
            return WorkType.UNDEFINED;
        case RESEARCH_TOOL:
            return WorkType.UNDEFINED;
        case SPIN_OFF_COMPANY:
            return WorkType.UNDEFINED;
        case STANDARDS_AND_POLICY:
            return WorkType.GOVERNMENT_PUBLICATION;
        case SUPERVISED_STUDENT_PUBLICATION:
            return WorkType.THESIS;
        case TECHNICAL_STANDARD:
            return WorkType.UNDEFINED;
        case TEST:
            return WorkType.UNDEFINED;
        case TRADEMARK:
            return WorkType.UNDEFINED;
        case TRANSLATION:
            return WorkType.UNDEFINED;        
        case WEBSITE:
            return WorkType.WEB_SITE;
        case WORKING_PAPER:
            return WorkType.REPORTS_WORKING_PAPERS;
        case UNDEFINED:
            return WorkType.UNDEFINED;
        default:
            return WorkType.UNDEFINED;
        }
    }

}
