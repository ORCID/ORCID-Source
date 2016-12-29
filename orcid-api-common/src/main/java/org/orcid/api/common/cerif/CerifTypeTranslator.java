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
package org.orcid.api.common.cerif;

import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.WorkType;

/**
 * Maps ORCID-CERIF work/identifier types and distinguishes between Publications
 * and products
 * 
 * OpenAIRE work types for publications Defined here:
 * https://github.com/openaire/guidelines/blob/master/docs/cris/ and products:
 * https://github.com/openaire/guidelines/blob/master/docs/cris/
 * cerif_xml_product_entity.rst
 * 
 * Note we also use the OTHER type, which is NOT in the OpenAIRE vocab.
 */
public class CerifTypeTranslator {

    /**
     * Map an ORCID work type to a Cerif/OpenAIRE type.
     * 
     * @param type
     * @return
     */
    public CerifClassEnum translate(WorkType type) {
        CerifClassEnum cc = publicationTypefromWorkType(type);
        if (cc == null)
            cc = productTypefromWorkType(type);
        return cc;
    }

    /**
     * Map an ORCID identifier type to a Cerif/OpenAIRE type.
     * 
     * @param type
     * @return
     */
    public CerifClassEnum translate(String type) {
        try{
        WorkExternalIdentifierType t = WorkExternalIdentifierType.valueOf(type);
        switch (t) {
            case DOI:
                return CerifClassEnum.DOI;
            case HANDLE:
                return CerifClassEnum.HANDLE;
            case PMC:
                return CerifClassEnum.PMCID;
            case URI:
                return CerifClassEnum.URL;
            case URN:
                return CerifClassEnum.URI;
            case ISSN: {
                return CerifClassEnum.ISSN;
            }
            case ISBN: {
                return CerifClassEnum.ISBN;
            }
            default:
                return CerifClassEnum.OTHER;
            }
        }catch(IllegalArgumentException e){
            return CerifClassEnum.OTHER;
        }

    }

    /**
     * Author id translations
     * 
     * ORCID, ResearcherID, ScopusAuthorID, STAFFID, DNR, ISNI
     * 
     * Used select source_id, client_source_id, external_id_type, count(*) from
     * external_identifier group by source_id, client_source_id,
     * external_id_type; to discover names.
     * 
     * @param id
     * @return
     */
    public CerifClassEnum translate(PersonExternalIdentifier id) {
        if ("ISNI".equals(id.getValue())) {
            return CerifClassEnum.ISNI;
        } else if ("Scopus Author ID".equals(id.getValue())) {
            return CerifClassEnum.SCOPUSAUTHORID;
        } else if ("ResearcherID".equals(id.getValue())) {
            return CerifClassEnum.RESEARCHERID;
        }
        return CerifClassEnum.OTHER;
    }

    public boolean isPublication(WorkType type) {
        return (publicationTypefromWorkType(type) != null);
    }

    public boolean isProduct(WorkType type) {
        return (publicationTypefromWorkType(type) == null);
    }

    /**
     * Mapping is difficult here - little overlap of vocab We map dataset and
     * other.
     * 
     * @param type
     * @return
     */
    private CerifClassEnum productTypefromWorkType(WorkType type) {
        switch (type) {
        // products
        case DATA_SET:
            return CerifClassEnum.PRODUCT_DATASET;
        default:
            return CerifClassEnum.OTHER;
        }

    }

    private CerifClassEnum publicationTypefromWorkType(WorkType type) {
        switch (type) {
        // publications
        case BOOK:
            return CerifClassEnum.BOOK;
        case BOOK_CHAPTER:
            return CerifClassEnum.CHAPTER_IN_BOOK;
        case BOOK_REVIEW:
            return CerifClassEnum.BOOK_REVIEW;
        case DICTIONARY_ENTRY:
            return CerifClassEnum.DICTIONARY_ENTRY;
        case DISSERTATION:
            return CerifClassEnum.DOCTORAL_THESIS;
        case ENCYCLOPEDIA_ENTRY:
            return CerifClassEnum.ENCYCLOPEDIA_ENTRY;
        case EDITED_BOOK:
            return CerifClassEnum.EDITED_BOOK;
        case JOURNAL_ARTICLE:
            return CerifClassEnum.JOURNAL_ARTICLE;
        case JOURNAL_ISSUE:
            return CerifClassEnum.JOURNAL_ISSUE;
        case MAGAZINE_ARTICLE:
            return CerifClassEnum.MAGAZINE_ARTICLE;
        case MANUAL:
            return CerifClassEnum.MANUAL;
        case ONLINE_RESOURCE:
            return CerifClassEnum.ONLINE_RESOURCE;
        case NEWSLETTER_ARTICLE:
            return CerifClassEnum.NEWSCLIPPING;
        case REPORT:
            return CerifClassEnum.REPORT;
        case RESEARCH_TOOL:
            return CerifClassEnum.RESEARCH_TOOL;
        case SUPERVISED_STUDENT_PUBLICATION:
            return CerifClassEnum.SUPERVISED_STUDENT_PUBLICATIONS;
        case TEST:
            return CerifClassEnum.TEST;
        case TRANSLATION:
            return CerifClassEnum.TRANSLATION;
        case WEBSITE:
            return CerifClassEnum.ONLINE_RESOURCE;
        case WORKING_PAPER:
            return CerifClassEnum.WORKING_PAPER;
        // conferences
        case CONFERENCE_PAPER:
            return CerifClassEnum.CONFERENCE_PROCEEDINGS_ARTICLE;
        case CONFERENCE_ABSTRACT:
            return CerifClassEnum.CONFERENCE_ABSTRACT;
        case CONFERENCE_POSTER:
            return CerifClassEnum.CONFERENCE_POSTER;
        // other
        case STANDARDS_AND_POLICY:
            return CerifClassEnum.STANDARD_AND_POLICY;
        case TECHNICAL_STANDARD:
            return CerifClassEnum.STANDARD_AND_POLICY;
        // everything else is a product
        default:
            return null;
        }

    }

}
