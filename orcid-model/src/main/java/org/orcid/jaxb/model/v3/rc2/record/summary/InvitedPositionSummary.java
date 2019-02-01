package org.orcid.jaxb.model.v3.rc2.record.summary;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;

@XmlRootElement(name = "invited-position-summary", namespace = "http://www.orcid.org/ns/invited-position")
@ApiModel(value = "InvitedPositionSummaryV3_0_rc2")
public class InvitedPositionSummary extends AffiliationSummary implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5195707602485199190L;

}
