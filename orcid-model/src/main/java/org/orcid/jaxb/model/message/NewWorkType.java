package org.orcid.jaxb.model.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlEnumValue;

public enum NewWorkType {
    @XmlEnumValue("publication")
    PUBLICATION("publication", WorkSubType.BOOK_CHAPTER, WorkSubType.BOOK, WorkSubType.DICTIONARY_ENTRY, WorkSubType.DISSERTATION, WorkSubType.EDITED_BOOK,
            WorkSubType.ENCYCLOPEDIA_ENTRY, WorkSubType.JOURNAL_ARTICLE, WorkSubType.JOURNAL_ISSUE, WorkSubType.MAGAZINE_ARTICLE, WorkSubType.MANUAL,
            WorkSubType.NEWSLETTER_ARTICLE, WorkSubType.NEWSPAPER_ARTICLE, WorkSubType.ONLINE_RESOURCE, WorkSubType.REPORT, WorkSubType.RESEARCH_TOOL,
            WorkSubType.SUPERVISED_STUDENT_PUBLICATION, WorkSubType.TRANSLATION, WorkSubType.WEBSITE, WorkSubType.WORKING_PAPER),             
    @XmlEnumValue("conference")
    CONFERENCE("conference", WorkSubType.CONFERENCE_ABSTRACT, WorkSubType.CONFERENCE_PAPER, WorkSubType.CONFERENCE_POSTER), 
    @XmlEnumValue("intellectual_property")
    INTELLECTUAL_PROPERTY("intellectual_property", WorkSubType.DISCLOSURE, WorkSubType.LICENSE, WorkSubType.PATENT, WorkSubType.REGISTERED_COPYRIGHT), 
    @XmlEnumValue("other_output")
    OTHER_OUTPUT("other_output", WorkSubType.ARTISTIC_PERFORMANCE, WorkSubType.DATA_SET, WorkSubType.INVENTION, WorkSubType.LECTURE_SPEECH, WorkSubType.OTHER,
            WorkSubType.RESEARCH_TECHNIQUE, WorkSubType.SPIN_OFF_COMPANY, WorkSubType.STANDARDS_AND_POLICY, WorkSubType.TECHNICAL_STANDARD, WorkSubType.UNDEFINED);

    private List<WorkSubType> subTypes = new ArrayList<WorkSubType>();
    private String value;

    private NewWorkType(String value, WorkSubType... subTypes) {
        this.value = value;
        for (WorkSubType subType : subTypes) {
            this.subTypes.add(subType);
        }
    }

    public String value() {
        return value;
    }

    public List<WorkSubType> getSubTypes() {
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
