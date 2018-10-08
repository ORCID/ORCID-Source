package org.orcid.jaxb.model.v3.rc2.record.summary;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "invited-position-summary", namespace = "http://www.orcid.org/ns/invited-position")
public class InvitedPositionSummary extends AffiliationSummary implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5195707602485199190L;

}
