package org.orcid.jaxb.model.v3.rc1.record.summary;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;

@XmlRootElement(name = "education-summary", namespace = "http://www.orcid.org/ns/education")
@ApiModel(value = "EducationSummaryV3_0_rc1")
public class EducationSummary extends AffiliationSummary implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5825435929626191645L;
}
