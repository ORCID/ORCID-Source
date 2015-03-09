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
@XmlRootElement(name = "employments", namespace = "http://www.orcid.org/ns/activities")
public class Employments implements Serializable {

    private static final long serialVersionUID = 3293976926416154039L;
    @XmlElement(name = "employment-summary", namespace = "http://www.orcid.org/ns/employment")
    private List<EmploymentSummary> summaries;

    public List<EmploymentSummary> getSummaries() {
        if(summaries == null)
            summaries = new ArrayList<>();
        return summaries;
    }


}
