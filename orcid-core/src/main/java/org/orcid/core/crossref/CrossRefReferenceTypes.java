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
package org.orcid.core.crossref;

import java.util.HashMap;
import java.util.Map;

/**
 * reference types.
 */
public class CrossRefReferenceTypes {

    // Journal-like Reference types
    /** constant value for Generic. */
    public static final String GENERIC = "GEN"; // Generic
    /** constant value for Aggregated Database. */
    public static final String AGGREGATED_DATABASE = "Aggregated Database";
    /** constant value for Ancient Text. */
    public static final String ANCIENT_TEXT = "Ancient Text";
    /** constant value for Audiovisual Material. */
    // Audio visual material.
    public static final String AUDIOVISUAL_MATERIAL = "ADVS";
    /** constant value for Bill. */
    public static final String BILL = "BILL"; // Bill or Resolution
    /** constant value for Book. */
    public static final String BOOK = "BOOK"; // Book, Whole
    /** constant value for Book Section. */
    public static final String BOOK_SECTION = "CHAP"; // Book chapter or section
    /** constant value for Case. */
    public static final String CASE = "CASE"; // Case
    /** constant value for Catalog. */
    public static final String CATALOG = "CTLG";

    // Journal-like Reference types
    /** constant value for CLASSICAL_WORK. */
    public static final String CLASSICAL_WORK = "Classical Work";
    /** constant value for DICTIONARY. */
    public static final String DICTIONARY = "Dictionary";
    /** constant value for ELECTRONIC_BOOK. */
    public static final String ELECTRONIC_BOOK = "Electronic Book";
    /** constant value for ELECTRONIC_ARTICLE. */
    public static final String ELECTRONIC_ARTICLE = "Electronic Article";
    /** constant value for ENCYCLOPEDIA. */
    public static final String ENCYCLOPEDIA = "Encyclopedia";
    /** constant value for GOVERNMENT_DOCUMENT. */
    public static final String GOVERNMENT_DOCUMENT = "Government Document";
    /** constant value for HEARING. */
    public static final String HEARING = "HEAR"; // Hearing
    /** constant value for JOURNAL_ARTICLE. */
    public static final String JOURNAL_ARTICLE = "JOUR"; // Journal Article

    // Journal-like Reference types
    /** constant value for LEGAL_RULE_OR_REGULATION. */
    public static final String LEGAL_RULE_OR_REGULATION = "Legal Rule or Regulation";
    /** constant value for MAGAZINE_ARTICLE. */
    public static final String MAGAZINE_ARTICLE = "MGZN"; // Magazine Article
    /** constant value for MANUSCRIPT. */
    public static final String MANUSCRIPT = "Manuscript";
    /** constant value for NEWSPAPER_ARTICLE. */
    public static final String NEWSPAPER_ARTICLE = "NEWS"; // Newspaper Article
    /** constant value for ONLINE_DATABASE. */
    public static final String ONLINE_DATABASE = "Online Database";
    /** constant value for ONLINE_MULTIMEDIA. */
    public static final String ONLINE_MULTIMEDIA = "Online Multimedia";
    /** constant value for PAMPHLET. */
    public static final String PAMPHLET = "PAMP"; // Pamphlet

    // Journal-like Reference types
    /** constant value for PERSONAL_COMMUNICATION. */
    public static final String PERSONAL_COMMUNICATION = "PCOMM"; // Personal
    // Communication
    /** constant value for REPORT. */
    public static final String REPORT = "RPRT"; // Report
    /** constant value for SERIAL. */
    public static final String SERIAL = "SER"; // Serial
    /** constant value for STANDARD. */
    public static final String STANDARD = "STND"; // Standard
    /** constant value for STATUTE. */
    public static final String STATUTE = "STAT"; // Statute
    /** constant value for UNPUBLISHED_WORK. */
    public static final String UNPUBLISHED_WORK = "UNPB"; // Unpublished Work

    // CREATION REFERENCE TYPES
    /** constant value for ARTWORK. */
    public static final String ARTWORK = "ART"; // Art Work
    /** constant value for BLOG. */
    public static final String BLOG = "Blog";
    /** constant value for CHART_OR_TABLE. */
    public static final String CHART_OR_TABLE = "Chart or Table";
    /** constant value for COMPUTER_PROGRAM. */
    public static final String COMPUTER_PROGRAM = "COMP"; // Computer Program
    /** constant value for EQUATION. */
    public static final String EQUATION = "Equation";
    /** constant value for FIGURE. */
    public static final String FIGURE = "Figure";
    /** constant value for FILM_OR_BROADCAST. */
    public static final String FILM_OR_BROADCAST = "Film or Broadcast";
    /** constant value for MAP. */
    public static final String MAP = "MAP"; // Map
    /** constant value for WEB_PAGE. */
    public static final String WEB_PAGE = "Web Page";

    // Conference References.
    /** constant value for CONFERENCE_PAPER. */
    public static final String CONFERENCE_PAPER = "CONP"; // Conference Paper
    /** constant value for CONFERENCE_PROCEEDING. */
    // Conference Proceeding.
    public static final String CONFERENCE_PROCEEDING = "CONF";

    /** Thesis References. */
    public static final String THESIS = "THES"; // Thesis

    /** Patent Reference type. */
    public static final String PATENT = "PAT"; // Patent

    /** Grant References. */
    public static final String GRANT = "GRNT"; // Grant

    /** Edited Book References. */
    public static final String EDITED_BOOK = "EDBK"; // Edited Book

    // Reference Type Ids for Journal like types
    /** constant value for GENERIC_ID. */
    public static final Integer GENERIC_ID = 13;
    /** constant value for AGGREGATED_DATABASE_ID. */
    public static final Integer AGGREGATED_DATABASE_ID = 55;
    /** constant value for ANCIENT_TEXT_ID. */
    public static final Integer ANCIENT_TEXT_ID = 51;
    /** constant value for AUDIOVISUAL_MATERIAL_ID. */
    public static final Integer AUDIOVISUAL_MATERIAL_ID = 3;
    /** constant value for BILL_ID. */
    public static final Integer BILL_ID = 4;
    /** constant value for BOOK_ID. */
    public static final Integer BOOK_ID = 6;
    /** constant value for BOOK_SECTION_ID. */
    public static final Integer BOOK_SECTION_ID = 5;

    // Reference Type Ids for Journal like types
    /** constant value for CASE_ID. */
    public static final Integer CASE_ID = 7;
    /** constant value for CATALOG_ID. */
    public static final Integer CATALOG_ID = 8;
    /** constant value for CLASSICAL_WORK_ID. */
    public static final Integer CLASSICAL_WORK_ID = 49;
    /** constant value for DICTIONARY_ID. */
    public static final Integer DICTIONARY_ID = 52;
    /** constant value for ELECTRONIC_BOOK_ID. */
    public static final Integer ELECTRONIC_BOOK_ID = 44;
    /** constant value for ELECTRONIC_ARTICLE_ID. */
    public static final Integer ELECTRONIC_ARTICLE_ID = 43;
    /** constant value for ENCYCLOPEDIA_ID. */
    public static final Integer ENCYCLOPEDIA_ID = 53;
    /** constant value for GOVERNMENT_DOCUMENT_ID. */
    public static final Integer GOVERNMENT_DOCUMENT_ID = 46;

    // Reference Type Ids for Journal like types
    /** constant value for HEARING_ID. */
    public static final Integer HEARING_ID = 14;
    /** constant value for JOURNAL_ARTICLE_ID. */
    public static final Integer JOURNAL_ARTICLE_ID = 17;
    /** constant value for LEGAL_RULE_OR_REGULATION_ID. */
    public static final Integer LEGAL_RULE_OR_REGULATION_ID = 50;
    /** constant value for MAGAZINE_ARTICLE_ID. */
    public static final Integer MAGAZINE_ARTICLE_ID = 19;
    /** constant value for MANUSCRIPT_ID. */
    public static final Integer MANUSCRIPT_ID = 36;
    /** constant value for NEWSPAPER_ARTICLE_ID. */
    public static final Integer NEWSPAPER_ARTICLE_ID = 23;
    /** constant value for ONLINE_DATABASE_ID . */
    public static final Integer ONLINE_DATABASE_ID = 45;
    /** constant value for ONLINE_MULTIMEDIA_ID. */
    public static final Integer ONLINE_MULTIMEDIA_ID = 48;

    // Reference Type Ids for Journal like types
    /** constant value for PAMPHLET_ID. */
    public static final Integer PAMPHLET_ID = 24;
    /** constant value for PERSONAL_COMMUNICATION_ID. */
    public static final Integer PERSONAL_COMMUNICATION_ID = 26;
    /** constant value for REPORT_ID. */
    public static final Integer REPORT_ID = 27;
    /** constant value for SERIAL_ID. */
    public static final Integer SERIAL_ID = 57;
    /** constant value for STANDARD_ID. */
    public static final Integer STANDARD_ID = 58;
    /** constant value for STATUTE_ID. */
    public static final Integer STATUTE_ID = 31;
    /** constant value for UNPUBLISHED_WORK_ID. */
    public static final Integer UNPUBLISHED_WORK_ID = 34;

    // Reference Type Ids for Creational Reference types
    /** constant value for ARTWORK_ID. */
    public static final Integer ARTWORK_ID = 2;
    /** constant value for BLOG_ID. */
    public static final Integer BLOG_ID = 56;
    /** constant value for CHART_OR_TABLE_ID. */
    public static final Integer CHART_OR_TABLE_ID = 38;
    /** constant value for COMPUTER_PROGRAM_ID. */
    public static final Integer COMPUTER_PROGRAM_ID = 9;
    /** constant value for EQUATION_ID. */
    public static final Integer EQUATION_ID = 39;
    /** constant value for FIGURE_ID. */
    public static final Integer FIGURE_ID = 37;
    /** constant value for FILM_OR_BROADCAST_ID. */
    public static final Integer FILM_OR_BROADCAST_ID = 21;
    /** constant value for MAP_ID. */
    public static final Integer MAP_ID = 20;
    /** constant value for WEB_PAGE_ID. */
    public static final Integer WEB_PAGE_ID = 12;

    /** Reference type Ids for Conference Types. */
    public static final Integer CONFERENCE_PAPER_ID = 47;
    /** ref type for processing id. */
    public static final Integer CONFERENCE_PROCEEDING_ID = 10;

    /** Thesis reference type Id. */
    public static final Integer THESIS_ID = 32;

    /** Patent Reference Type Id. */
    public static final Integer PATENT_ID = 25;

    /** Grant reference type id. */
    public static final Integer GRANT_ID = 54;

    /** Edited Book Reference Type Id. */
    public static final Integer EDITED_BOOK_ID = 28;

    /** static map containing reference type name and id. */
    private static Map<String, Integer> reftypeIDMap = new HashMap<String, Integer>();

    /**
     * Static map which consists of reference type name as key and reference
     * type id as value.
     */
    private static Map<Integer, String> refIDtypeMapper = new HashMap<Integer, String>();
    /** puts in id map. */

    static {

        // set type name and id as key and value pairs for reftypeIDMap
        reftypeIDMap.put(GENERIC, GENERIC_ID);
        reftypeIDMap.put(AGGREGATED_DATABASE, AGGREGATED_DATABASE_ID);
        reftypeIDMap.put(ANCIENT_TEXT, ANCIENT_TEXT_ID);
        reftypeIDMap.put(AUDIOVISUAL_MATERIAL, AUDIOVISUAL_MATERIAL_ID);
        reftypeIDMap.put(BILL, BILL_ID);
        reftypeIDMap.put(BOOK, BOOK_ID);
        // set type name and id as key and value pairs for reftypeIDMap
        reftypeIDMap.put(BOOK_SECTION, BOOK_SECTION_ID);
        reftypeIDMap.put(CASE, CASE_ID);
        reftypeIDMap.put(CATALOG, CATALOG_ID);
        reftypeIDMap.put(CLASSICAL_WORK, CLASSICAL_WORK_ID);
        reftypeIDMap.put(DICTIONARY, DICTIONARY_ID);
        // set type name and id as key and value pairs for reftypeIDMap
        reftypeIDMap.put(ELECTRONIC_BOOK, ELECTRONIC_BOOK_ID);
        reftypeIDMap.put(ELECTRONIC_ARTICLE, ELECTRONIC_ARTICLE_ID);
        reftypeIDMap.put(ENCYCLOPEDIA, ENCYCLOPEDIA_ID);
        reftypeIDMap.put(GOVERNMENT_DOCUMENT, GOVERNMENT_DOCUMENT_ID);
        reftypeIDMap.put(HEARING, HEARING_ID);
        // set type name and id as key and value pairs for reftypeIDMap
        reftypeIDMap.put(JOURNAL_ARTICLE, JOURNAL_ARTICLE_ID);
        reftypeIDMap.put(LEGAL_RULE_OR_REGULATION, LEGAL_RULE_OR_REGULATION_ID);
        reftypeIDMap.put(MAGAZINE_ARTICLE, MAGAZINE_ARTICLE_ID);
        reftypeIDMap.put(MANUSCRIPT, MANUSCRIPT_ID);
        reftypeIDMap.put(NEWSPAPER_ARTICLE, NEWSPAPER_ARTICLE_ID);
        reftypeIDMap.put(ONLINE_DATABASE, ONLINE_DATABASE_ID);
        // set type name and id as key and value pairs for reftypeIDMap
        reftypeIDMap.put(ONLINE_MULTIMEDIA, ONLINE_MULTIMEDIA_ID);
        reftypeIDMap.put(PAMPHLET, PAMPHLET_ID);
        reftypeIDMap.put(PERSONAL_COMMUNICATION, PERSONAL_COMMUNICATION_ID);
        reftypeIDMap.put(REPORT, REPORT_ID);
        // set type name and id as key and value pairs for reftypeIDMap
        reftypeIDMap.put(SERIAL, SERIAL_ID);
        reftypeIDMap.put(STANDARD, STANDARD_ID);
        reftypeIDMap.put(STATUTE, STATUTE_ID);
        reftypeIDMap.put(UNPUBLISHED_WORK, UNPUBLISHED_WORK_ID);
        reftypeIDMap.put(ARTWORK, ARTWORK_ID);
        reftypeIDMap.put(BLOG, BLOG_ID);
        // set type name and id as key and value pairs for reftypeIDMap
        reftypeIDMap.put(CHART_OR_TABLE, CHART_OR_TABLE_ID);
        reftypeIDMap.put(COMPUTER_PROGRAM, COMPUTER_PROGRAM_ID);
        reftypeIDMap.put(EQUATION, EQUATION_ID);
        reftypeIDMap.put(FIGURE, FIGURE_ID);
        reftypeIDMap.put(FILM_OR_BROADCAST, FILM_OR_BROADCAST_ID);
        reftypeIDMap.put(MAP, MAP_ID);
        // set type name and id as key and value pairs for reftypeIDMap
        reftypeIDMap.put(WEB_PAGE, WEB_PAGE_ID);
        reftypeIDMap.put(CONFERENCE_PAPER, CONFERENCE_PAPER_ID);
        reftypeIDMap.put(CONFERENCE_PROCEEDING, CONFERENCE_PROCEEDING_ID);
        reftypeIDMap.put(THESIS, THESIS_ID);
        reftypeIDMap.put(PATENT, PATENT_ID);
        reftypeIDMap.put(GRANT, GRANT_ID);
        reftypeIDMap.put(EDITED_BOOK, EDITED_BOOK_ID);

        // set type id and name as key and value pairs for refIDtypeMapper
        refIDtypeMapper.put(GENERIC_ID, GENERIC);
        refIDtypeMapper.put(AGGREGATED_DATABASE_ID, AGGREGATED_DATABASE);
        refIDtypeMapper.put(ANCIENT_TEXT_ID, ANCIENT_TEXT);
        refIDtypeMapper.put(AUDIOVISUAL_MATERIAL_ID, AUDIOVISUAL_MATERIAL);
        refIDtypeMapper.put(BILL_ID, BILL);
        refIDtypeMapper.put(BOOK_ID, BOOK);
        // set type id and name as key and value pairs for refIDtypeMapper
        refIDtypeMapper.put(BOOK_SECTION_ID, BOOK_SECTION);
        refIDtypeMapper.put(CASE_ID, CASE);
        refIDtypeMapper.put(CATALOG_ID, CATALOG);
        refIDtypeMapper.put(CLASSICAL_WORK_ID, CLASSICAL_WORK);
        refIDtypeMapper.put(DICTIONARY_ID, DICTIONARY);
        // set type id and name as key and value pairs for refIDtypeMapper
        refIDtypeMapper.put(ELECTRONIC_BOOK_ID, ELECTRONIC_BOOK);
        refIDtypeMapper.put(ELECTRONIC_ARTICLE_ID, ELECTRONIC_ARTICLE);
        refIDtypeMapper.put(ENCYCLOPEDIA_ID, ENCYCLOPEDIA);
        refIDtypeMapper.put(GOVERNMENT_DOCUMENT_ID, GOVERNMENT_DOCUMENT);
        refIDtypeMapper.put(HEARING_ID, HEARING);
        // set type id and name as key and value pairs for refIDtypeMapper
        refIDtypeMapper.put(JOURNAL_ARTICLE_ID, JOURNAL_ARTICLE);
        refIDtypeMapper.put(LEGAL_RULE_OR_REGULATION_ID, LEGAL_RULE_OR_REGULATION);
        refIDtypeMapper.put(MAGAZINE_ARTICLE_ID, MAGAZINE_ARTICLE);
        refIDtypeMapper.put(MANUSCRIPT_ID, MANUSCRIPT);
        refIDtypeMapper.put(NEWSPAPER_ARTICLE_ID, NEWSPAPER_ARTICLE);
        refIDtypeMapper.put(ONLINE_DATABASE_ID, ONLINE_DATABASE);
        // set type id and name as key and value pairs for refIDtypeMapper
        refIDtypeMapper.put(ONLINE_MULTIMEDIA_ID, ONLINE_MULTIMEDIA);
        refIDtypeMapper.put(PAMPHLET_ID, PAMPHLET);
        refIDtypeMapper.put(PERSONAL_COMMUNICATION_ID, PERSONAL_COMMUNICATION);
        refIDtypeMapper.put(REPORT_ID, REPORT);
        refIDtypeMapper.put(SERIAL_ID, SERIAL);
        // set type id and name as key and value pairs for refIDtypeMapper
        refIDtypeMapper.put(STANDARD_ID, STANDARD);
        refIDtypeMapper.put(STATUTE_ID, STATUTE);
        refIDtypeMapper.put(UNPUBLISHED_WORK_ID, UNPUBLISHED_WORK);
        refIDtypeMapper.put(ARTWORK_ID, ARTWORK);
        refIDtypeMapper.put(BLOG_ID, BLOG);
        refIDtypeMapper.put(CHART_OR_TABLE_ID, CHART_OR_TABLE);
        // set type id and name as key and value pairs for refIDtypeMapper
        refIDtypeMapper.put(COMPUTER_PROGRAM_ID, COMPUTER_PROGRAM);
        refIDtypeMapper.put(EQUATION_ID, EQUATION);
        refIDtypeMapper.put(FIGURE_ID, FIGURE);
        refIDtypeMapper.put(FILM_OR_BROADCAST_ID, FILM_OR_BROADCAST);
        refIDtypeMapper.put(MAP_ID, MAP);
        refIDtypeMapper.put(WEB_PAGE_ID, WEB_PAGE);
        // set type id and name as key and value pairs for refIDtypeMapper
        refIDtypeMapper.put(CONFERENCE_PAPER_ID, CONFERENCE_PAPER);
        refIDtypeMapper.put(CONFERENCE_PROCEEDING_ID, CONFERENCE_PROCEEDING);
        refIDtypeMapper.put(THESIS_ID, THESIS);
        refIDtypeMapper.put(PATENT_ID, PATENT);
        refIDtypeMapper.put(GRANT_ID, GRANT);
        refIDtypeMapper.put(EDITED_BOOK_ID, EDITED_BOOK);

    }

    /**
     * gets reference type name by id.
     * 
     * @param refTypeId
     *            String
     * @return String reference type id
     */
    public static String getReferenceTypeById(final int refTypeId) {
        String refType = refIDtypeMapper.get(refTypeId);

        if (refType == null) {
            refType = JOURNAL_ARTICLE;
        }
        return refType;
    }

    /**
     * Gets reference id from name.
     * 
     * @param refTypeName
     *            name
     * @return int
     */
    public static int getReferenceTypeIdByName(final String refTypeName) {
        Integer refTypeId = reftypeIDMap.get(refTypeName);
        if (refTypeId == null) {
            return JOURNAL_ARTICLE_ID;
        }
        return refTypeId;
    }
}
