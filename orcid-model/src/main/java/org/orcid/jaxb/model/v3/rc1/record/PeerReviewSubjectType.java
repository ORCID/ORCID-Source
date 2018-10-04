package org.orcid.jaxb.model.v3.rc1.record;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlEnumValue;

public enum PeerReviewSubjectType implements Serializable {
    @XmlEnumValue("artistic-performance")
    ARTISTIC_PERFORMANCE("artistic-performance"),
    @XmlEnumValue("book-chapter")
    BOOK_CHAPTER("book-chapter"),
    @XmlEnumValue("book-review")
    BOOK_REVIEW("book-review"),
    @XmlEnumValue("book")
    BOOK("book"),
    @XmlEnumValue("conference-abstract")
    CONFERENCE_ABSTRACT("conference-abstract"),
    @XmlEnumValue("conference-paper")
    CONFERENCE_PAPER("conference-paper"),
    @XmlEnumValue("conference-poster")
    CONFERENCE_POSTER("conference-poster"),
    @XmlEnumValue("data-set")
    DATA_SET("data-set"),
    @XmlEnumValue("dictionary-entry")
    DICTIONARY_ENTRY("dictionary-entry"),
    @XmlEnumValue("disclosure")
    DISCLOSURE("disclosure"),
    @XmlEnumValue("dissertation")
    DISSERTATION("dissertation"),
    @XmlEnumValue("edited-book")
    EDITED_BOOK("edited-book"),
    @XmlEnumValue("encyclopedia-entry")
    ENCYCLOPEDIA_ENTRY("encyclopedia-entry"),
    @XmlEnumValue("invention")
    INVENTION("invention"),
    @XmlEnumValue("journal-article")
    JOURNAL_ARTICLE("journal-article"),
    @XmlEnumValue("journal-issue")
    JOURNAL_ISSUE("journal-issue"),
    @XmlEnumValue("lecture-speech")
    LECTURE_SPEECH("lecture-speech"),
    @XmlEnumValue("license")
    LICENSE("license"),
    @XmlEnumValue("magazine-article")
    MAGAZINE_ARTICLE("magazine-article"),
    @XmlEnumValue("manual")
    MANUAL("manual"),
    @XmlEnumValue("newsletter-article")
    NEWSLETTER_ARTICLE("newsletter-article"),
    @XmlEnumValue("newspaper-article")
    NEWSPAPER_ARTICLE("newspaper-article"),
    @XmlEnumValue("online-resource")
    ONLINE_RESOURCE("online-resource"),
    @XmlEnumValue("other")
    OTHER("other"),
    @XmlEnumValue("patent")
    PATENT("patent"),
    @XmlEnumValue("registered-copyright")
    REGISTERED_COPYRIGHT("registered-copyright"),
    @XmlEnumValue("report")
    REPORT("report"),
    @XmlEnumValue("research-technique")
    RESEARCH_TECHNIQUE("research-technique"),
    @XmlEnumValue("research-tool")
    RESEARCH_TOOL("research-tool"),
    @XmlEnumValue("software")
    SOFTWARE("software"),
    @XmlEnumValue("spin-off-company")
    SPIN_OFF_COMPANY("spin-off-company"),
    @XmlEnumValue("standards-and-policy")
    STANDARDS_AND_POLICY("standards-and-policy"),
    @XmlEnumValue("supervised-student-publication")
    SUPERVISED_STUDENT_PUBLICATION("supervised-student-publication"),
    @XmlEnumValue("technical-standard")
    TECHNICAL_STANDARD("technical-standard"),
    @XmlEnumValue("test")
    TEST("test"),
    @XmlEnumValue("trademark")
    TRADEMARK("trademark"),
    @XmlEnumValue("translation")
    TRANSLATION("translation"),    
    @XmlEnumValue("website")
    WEBSITE("website"),
    @XmlEnumValue("working-paper")
    WORKING_PAPER("working-paper"),
    @XmlEnumValue("grant")
    GRANT("grant"), 
    @XmlEnumValue("contract")
    CONTRACT("contract"), 
    @XmlEnumValue("award")
    AWARD("award"),
    @XmlEnumValue("salary-award")
    SALARY_AWARD("salary-award"),
    @XmlEnumValue("research-resource-proposal")
    RESEARCH_RESOURCE_PROPOSAL("research-resource-proposal"),
    @XmlEnumValue("undefined")
    UNDEFINED("undefined");
    
    private final String value;
    
    PeerReviewSubjectType(String v) {
        value = v;
    }
    
    public String value() {
        return value;
    }
    
    public static PeerReviewSubjectType fromValue(String v) {
        for (PeerReviewSubjectType c : PeerReviewSubjectType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        
        // Known maps
        switch (v) {
        case "article-journal":
            return PeerReviewSubjectType.JOURNAL_ARTICLE;
        case "chapter":
            return PeerReviewSubjectType.BOOK_CHAPTER;
        case "dataset":
            return PeerReviewSubjectType.DATA_SET;
        case "standard":
            return PeerReviewSubjectType.STANDARDS_AND_POLICY;
        }        
        
        throw new IllegalArgumentException(v);
    }
}
