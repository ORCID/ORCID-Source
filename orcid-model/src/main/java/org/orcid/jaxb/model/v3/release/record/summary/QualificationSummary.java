package org.orcid.jaxb.model.v3.rc2.record.summary;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;

@XmlRootElement(name = "qualification-summary", namespace = "http://www.orcid.org/ns/qualification")
@ApiModel(value = "QualificationSummaryV3_0_rc2")
public class QualificationSummary extends AffiliationSummary implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1058178985146686275L;

}
