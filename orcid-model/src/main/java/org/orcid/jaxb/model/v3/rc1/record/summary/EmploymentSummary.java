package org.orcid.jaxb.model.v3.rc1.record.summary;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;

@XmlRootElement(name = "employment-summary", namespace = "http://www.orcid.org/ns/employment")
@ApiModel(value = "EmploymentSummaryV3_0_rc1")
public class EmploymentSummary extends AffiliationSummary implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4147903643760430166L;
}
