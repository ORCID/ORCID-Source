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
package org.orcid.jaxb.model.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlEnumValue;

public enum WorkType implements Serializable {
    @XmlEnumValue("artistic-performance")
    ARTISTIC_PERFORMANCE("artistic-performance", false),
    @XmlEnumValue("book-chapter")
    BOOK_CHAPTER("book-chapter", false),
    @XmlEnumValue("book-review")
    BOOK_REVIEW("book-review", false),
    @XmlEnumValue("book")
    BOOK("book", false),
    @XmlEnumValue("conference-abstract")
    CONFERENCE_ABSTRACT("conference-abstract", false),
    @XmlEnumValue("conference-paper")
    CONFERENCE_PAPER("conference-paper", false),
    @XmlEnumValue("conference-poster")
    CONFERENCE_POSTER("conference-poster", false),
    @XmlEnumValue("data-set")
    DATA_SET("data-set", false),
    @XmlEnumValue("dictionary-entry")
    DICTIONARY_ENTRY("dictionary-entry", false),
    @XmlEnumValue("disclosure")
    DISCLOSURE("disclosure", false),
    @XmlEnumValue("dissertation")
    DISSERTATION("dissertation", false),
    @XmlEnumValue("edited-book")
    EDITED_BOOK("edited-book", false),
    @XmlEnumValue("encyclopedia-entry")
    ENCYCLOPEDIA_ENTRY("encyclopedia-entry", false),
    @XmlEnumValue("invention")
    INVENTION("invention", false),
    @XmlEnumValue("journal-article")
    JOURNAL_ARTICLE("journal-article", false),
    @XmlEnumValue("journal-issue")
    JOURNAL_ISSUE("journal-issue", false),
    @XmlEnumValue("lecture-speech")
    LECTURE_SPEECH("lecture-speech", false),
    @XmlEnumValue("license")
    LICENSE("license", false),
    @XmlEnumValue("magazine-article")
    MAGAZINE_ARTICLE("magazine-article", false),
    @XmlEnumValue("manual")
    MANUAL("manual", false),
    @XmlEnumValue("newsletter-article")
    NEWSLETTER_ARTICLE("newsletter-article", false),
    @XmlEnumValue("newspaper-article")
    NEWSPAPER_ARTICLE("newspaper-article", false),
    @XmlEnumValue("online-resource")
    ONLINE_RESOURCE("online-resource", false),
    @XmlEnumValue("other")
    OTHER("other", false),
    @XmlEnumValue("patent")
    PATENT("patent", false),
    @XmlEnumValue("registered-copyright")
    REGISTERED_COPYRIGHT("registered-copyright", false),
    @XmlEnumValue("report")
    REPORT("report", false),
    @XmlEnumValue("research-technique")
    RESEARCH_TECHNIQUE("research-technique", false),
    @XmlEnumValue("research-tool")
    RESEARCH_TOOL("research-tool", false),
    @XmlEnumValue("spin-off-company")
    SPIN_OFF_COMPANY("spin-off-company", false),
    @XmlEnumValue("standards-and-policy")
    STANDARDS_AND_POLICY("standards-and-policy", false),
    @XmlEnumValue("supervised-student-publication")
    SUPERVISED_STUDENT_PUBLICATION("supervised-student-publication", false),
    @XmlEnumValue("technical-standard")
    TECHNICAL_STANDARD("technical-standard", false),
    @XmlEnumValue("test")
    TEST("test", false),
    @XmlEnumValue("trademark")
    TRADEMARK("trademark", false),
    @XmlEnumValue("translation")
    TRANSLATION("translation", false),    
    @XmlEnumValue("website")
    WEBSITE("website", false),
    @XmlEnumValue("working-paper")
    WORKING_PAPER("working-paper", false),
    @XmlEnumValue("undefined")
    UNDEFINED("undefined", false),
    
    /**
     * Deprecated work types, used just for backwards compatibility
     * */
    @XmlEnumValue("advertisement")
    ADVERTISEMENT("advertisement"), 
    @XmlEnumValue("audiovisual")
    AUDIOVISUAL("audiovisual"), 
    @XmlEnumValue("bible")
    BIBLE("bible"), 
    @XmlEnumValue("brochure")
    BROCHURE("brochure"), 
    @XmlEnumValue("cartoon-comic")
    CARTOON_COMIC("cartoon-comic"), 
    @XmlEnumValue("chapter-anthology")
    CHAPTER_ANTHOLOGY("chapter-anthology"), 
    @XmlEnumValue("components")
    COMPONENTS("components"), 
    @XmlEnumValue("conference-proceedings")
    CONFERENCE_PROCEEDINGS("conference-proceedings"), 
    @XmlEnumValue("congressional-publication")
    CONGRESSIONAL_PUBLICATION("congressional-publication"), 
    @XmlEnumValue("court-case")
    COURT_CASE("court-case"), 
    @XmlEnumValue("database")
    DATABASE("database"),
    @XmlEnumValue("digital-image")
    DIGITAL_IMAGE("digital-image"),
    @XmlEnumValue("dissertation-abstract")
    DISSERTATON_ABSTRACT("dissertation-abstract"),
    @XmlEnumValue("e-mail")
    EMAIL("e-mail"), 
    @XmlEnumValue("editorial")
    EDITORIAL("editorial"), 
    @XmlEnumValue("electronic-only")
    ELECTRONIC_ONLY("electronic-only"), 
    @XmlEnumValue("encyclopedia-article")
    ENCYCLOPEDIA_ARTICLE("encyclopedia-article"), 
    @XmlEnumValue("executive-order")
    EXECUTIVE_ORDER("executive-order"), 
    @XmlEnumValue("federal-bill")
    FEDERAL_BILL("federal-bill"), 
    @XmlEnumValue("federal-report")
    FEDERAL_REPORT("federal-report"), 
    @XmlEnumValue("federal-rule")
    FEDERAL_RULE("federal-rule"), 
    @XmlEnumValue("federal-statute")
    FEDERAL_STATUTE("federal-statute"), 
    @XmlEnumValue("federal-testimony")
    FEDERAL_TESTIMONY("federal-testimony"), 
    @XmlEnumValue("film-movie")
    FILM_MOVIE("film-movie"), 
    @XmlEnumValue("government-publication")
    GOVERNMENT_PUBLICATION("government-publication"), 
    @XmlEnumValue("interview")
    INTERVIEW("interview"), 
    @XmlEnumValue("legal")
    LEGAL("legal"), 
    @XmlEnumValue("letter")
    LETTER("letter"), 
    @XmlEnumValue("live-performance")
    LIVE_PERFORMANCE("live-performance"), 
    @XmlEnumValue("mailing-list")
    MAILING_LIST("mailing-list"), 
    @XmlEnumValue("manuscript")
    MANUSCRIPT("manuscript"), 
    @XmlEnumValue("map-chart")
    MAP_CHART("map-chart"), 
    @XmlEnumValue("musical-recording")
    MUSICAL_RECORDING("musical-recording"), 
    @XmlEnumValue("newsgroup")
    NEWSGROUP("newsgroup"), 
    @XmlEnumValue("newsletter")
    NEWSLETTER("newsletter"), 
    @XmlEnumValue("non-periodicals")
    NON_PERIODICALS("non-periodicals"),
    @XmlEnumValue("pamphlet")
    PAMPHLET("pamphlet"), 
    @XmlEnumValue("painting")
    PAINTING("painting"), 
    @XmlEnumValue("periodicals")
    PERIODICALS("periodicals"), 
    @XmlEnumValue("photograph")
    PHOTOGRAPH("photograph"), 
    @XmlEnumValue("press-release")
    PRESSRELEASE("press-release"), 
    @XmlEnumValue("raw-data")
    RAW_DATA("raw-data"), 
    @XmlEnumValue("religious-text")
    RELIGIOUS_TEXT("religious-text"), 
    @XmlEnumValue("reports-working-papers")
    REPORTS_WORKING_PAPERS("reports-working-papers"), 
    @XmlEnumValue("review")
    REVIEW("review"), 
    @XmlEnumValue("scholarly-project")
    SCHOLARLY_PROJECT("scholarly-project"), 
    @XmlEnumValue("software")
    SOFTWARE("software"), 
    @XmlEnumValue("standards")
    STANDARDS("standards"), 
    @XmlEnumValue("television-radio")
    TELEVISION_RADIO("television-radio"), 
    @XmlEnumValue("thesis")
    THESIS("thesis"),
    @XmlEnumValue("web-site")
    WEB_SITE("web-site");
    
    private final String value;
    private boolean deprecated = true;

    WorkType(String v) {
        value = v;
    }
    
    WorkType(String v, boolean d){
    	value = v;
    	deprecated = d;
    }
    
    public String value() {
        return value;
    }
    
    public boolean isDeprecated(){
        return this.deprecated;
    }
    
    public static WorkType fromValue(String v) {
        for (WorkType c : WorkType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
