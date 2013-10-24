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
            WorkSubtype.RESEARCH_TECHNIQUE, WorkSubtype.SPIN_OFF_COMPANY, WorkSubtype.STANDARDS_AND_POLICY, WorkSubtype.TECHNICAL_STANDARD, WorkSubtype.UNDEFINED);  
    
    private List<WorkSubtype> subTypes = new ArrayList<WorkSubtype>();
    private String value;

    private NewWorkType(String value, WorkSubtype... subTypes) {
        this.value = value;
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
    
    public static NewWorkType fromValue(String v) {
        for (NewWorkType c : NewWorkType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
