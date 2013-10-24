package org.orcid.jaxb.model.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlEnumValue;

public enum NewWorkType {
    @XmlEnumValue("publication")
    PUBLICATION("publication", WorkSubtype.BOOK_CHAPTER, WorkSubtype.BOOK_REVIEW, WorkSubtype.BOOK, WorkSubtype.DICTIONARY_ENTRY, WorkSubtype.DISSERTATION, WorkSubtype.EDITED_BOOK,
            WorkSubtype.ENCYCLOPEDIA_ENTRY, WorkSubtype.JOURNAL_ARTICLE, WorkSubtype.JOURNAL_ISSUE, WorkSubtype.MAGAZINE_ARTICLE, WorkSubtype.MANUAL,
            WorkSubtype.NEWSLETTER_ARTICLE, WorkSubtype.NEWSPAPER_ARTICLE, WorkSubtype.ONLINE_RESOURCE, WorkSubtype.REPORT, WorkSubtype.RESEARCH_TOOL,
            WorkSubtype.SUPERVISED_STUDENT_PUBLICATION, WorkSubtype.TEST, WorkSubtype.TRANSLATION, WorkSubtype.WEBSITE, WorkSubtype.WORKING_PAPER),             
    @XmlEnumValue("conference")
    CONFERENCE("conference", WorkSubtype.CONFERENCE_ABSTRACT, WorkSubtype.CONFERENCE_PAPER, WorkSubtype.CONFERENCE_POSTER), 
    @XmlEnumValue("intellectual_property")
    INTELLECTUAL_PROPERTY("intellectual_property", WorkSubtype.DISCLOSURE, WorkSubtype.LICENSE, WorkSubtype.PATENT, WorkSubtype.REGISTERED_COPYRIGHT), 
    @XmlEnumValue("other_output")
    OTHER_OUTPUT("other_output", WorkSubtype.ARTISTIC_PERFORMANCE, WorkSubtype.DATA_SET, WorkSubtype.INVENTION, WorkSubtype.LECTURE_SPEECH, WorkSubtype.OTHER,
            WorkSubtype.RESEARCH_TECHNIQUE, WorkSubtype.SPIN_OFF_COMPANY, WorkSubtype.STANDARDS_AND_POLICY, WorkSubtype.TECHNICAL_STANDARD, WorkSubtype.UNDEFINED),  
    
    /**
     * Deprecated work types, used just for backwards compatibility
     * */
    @XmlEnumValue("advertisement")
    ADVERTISEMENT("advertisement"), @XmlEnumValue("audiovisual")
    AUDIOVISUAL("audiovisual"), @XmlEnumValue("bible")
    BIBLE("bible"), @XmlEnumValue("book")
    BOOK("book"), @XmlEnumValue("brochure")
    BROCHURE("brochure"), @XmlEnumValue("cartoon-comic")
    CARTOON_COMIC("cartoon-comic"), @XmlEnumValue("chapter-anthology")
    CHAPTER_ANTHOLOGY("chapter-anthology"), @XmlEnumValue("components")
    COMPONENTS("components"), @XmlEnumValue("conference-proceedings")
    CONFERENCE_PROCEEDINGS("conference-proceedings"), @XmlEnumValue("congressional-publication")
    CONGRESSIONAL_PUBLICATION("congressional-publication"), @XmlEnumValue("court-case")
    COURT_CASE("court-case"), @XmlEnumValue("database")
    DATABASE("database"), @XmlEnumValue("dictionary-entry")
    DICTIONARY_ENTRY("dictionary-entry"), @XmlEnumValue("digital-image")
    DIGITAL_IMAGE("digital-image"), @XmlEnumValue("dissertation-abstract")
    DISSERTATON_ABSTRACT("dissertation-abstract"), @XmlEnumValue("dissertation")
    DISSERTATION("dissertation"), @XmlEnumValue("e-mail")
    EMAIL("e-mail"), @XmlEnumValue("editorial")
    EDITORIAL("editorial"), @XmlEnumValue("electronic-only")
    ELECTRONIC_ONLY("electronic-only"), @XmlEnumValue("encyclopedia-article")
    ENCYCLOPEDIA_ARTICLE("encyclopedia-article"), @XmlEnumValue("executive-order")
    EXECUTIVE_ORDER("executive-order"), @XmlEnumValue("federal-bill")
    FEDERAL_BILL("federal-bill"), @XmlEnumValue("federal-report")
    FEDERAL_REPORT("federal-report"), @XmlEnumValue("federal-rule")
    FEDERAL_RULE("federal-rule"), @XmlEnumValue("federal-statute")
    FEDERAL_STATUTE("federal-statute"), @XmlEnumValue("federal-testimony")
    FEDERAL_TESTIMONY("federal-testimony"), @XmlEnumValue("film-movie")
    FILM_MOVIE("film-movie"), @XmlEnumValue("government-publication")
    GOVERNMENT_PUBLICATION("government-publication"), @XmlEnumValue("interview")
    INTERVIEW("interview"), @XmlEnumValue("journal-article")
    JOURNAL_ARTICLE("journal-article"), @XmlEnumValue("lecture-speech")
    LECTURE_SPEECH("lecture-speech"), @XmlEnumValue("legal")
    LEGAL("legal"), @XmlEnumValue("letter")
    LETTER("letter"), @XmlEnumValue("live-performance")
    LIVE_PERFORMANCE("live-performance"), @XmlEnumValue("magazine-article")
    MAGAZINE_ARTICLE("magazine-article"), @XmlEnumValue("mailing-list")
    MAILING_LIST("mailing-list"), @XmlEnumValue("manuscript")
    MANUSCRIPT("manuscript"), @XmlEnumValue("map-chart")
    MAP_CHART("map-chart"), @XmlEnumValue("musical-recording")
    MUSICAL_RECORDING("musical-recording"), @XmlEnumValue("newsgroup")
    NEWSGROUP("newsgroup"), @XmlEnumValue("newsletter")
    NEWSLETTER("newsletter"), @XmlEnumValue("newspaper-article")
    NEWSPAPER_ARTICLE("newspaper-article"), @XmlEnumValue("non-periodicals")
    NON_PERIODICALS("non-periodicals"), @XmlEnumValue("other")
    OTHER("other"), @XmlEnumValue("pamphlet")
    PAMPHLET("pamphlet"), @XmlEnumValue("painting")
    PAINTING("painting"), @XmlEnumValue("patent")
    PATENT("patent"), @XmlEnumValue("periodicals")
    PERIODICALS("periodicals"), @XmlEnumValue("photograph")
    PHOTOGRAPH("photograph"), @XmlEnumValue("press-release")
    PRESSRELEASE("press-release"), @XmlEnumValue("raw-data")
    RAW_DATA("raw-data"), @XmlEnumValue("religious-text")
    RELIGIOUS_TEXT("religious-text"), @XmlEnumValue("report")
    REPORT("report"), @XmlEnumValue("reports-working-papers")
    REPORTS_WORKING_PAPERS("reports-working-papers"), @XmlEnumValue("review")
    REVIEW("review"), @XmlEnumValue("scholarly-project")
    SCHOLARLY_PROJECT("scholarly-project"), @XmlEnumValue("software")
    SOFTWARE("software"), @XmlEnumValue("standards")
    STANDARDS("standards"), @XmlEnumValue("television-radio")
    TELEVISION_RADIO("television-radio"), @XmlEnumValue("thesis")
    THESIS("thesis"), @XmlEnumValue("web-site")
    WEBSITE("web-site"), @XmlEnumValue("undefined")
    UNDEFINED("undefined");
    
    
    private List<WorkSubtype> subTypes = new ArrayList<WorkSubtype>();
    private String value;
    private boolean deprecated = true;

    private NewWorkType(String value){
        this.value = value;
    }
    
    private NewWorkType(String value, WorkSubtype... subTypes) {
        this.value = value;
        this.deprecated = false;
        for (WorkSubtype subType : subTypes) {
            this.subTypes.add(subType);
        }
    }        

    public String value() {
        return value;
    }

    public List<WorkSubtype> getSubTypes() {
        return Collections.unmodifiableList(subTypes);
    }
    
    public boolean isDeprecated(){
        return this.deprecated;
    }
    
    public static NewWorkType fromValue(String v) {
        for (NewWorkType c : NewWorkType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
