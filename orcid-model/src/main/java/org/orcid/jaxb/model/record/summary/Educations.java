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
package org.orcid.jaxb.model.record.summary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "summaries" })
@XmlRootElement(name = "educations", namespace = "http://www.orcid.org/ns/activities")
public class Educations implements Serializable {

    private static final long serialVersionUID = 3293976926416154039L;
    @XmlElement(name = "education-summary", namespace = "http://www.orcid.org/ns/education")
    private List<EducationSummary> summaries;

    public List<EducationSummary> getSummaries() {
        if(summaries == null)
            summaries = new ArrayList<>();
        return summaries;
    }


}
