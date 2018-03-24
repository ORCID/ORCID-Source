package org.orcid.jaxb.model.v3.dev1.record.summary;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "membership-summary", namespace = "http://www.orcid.org/ns/membership")
public class MembershipSummary extends AffiliationSummary implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2967263626161925359L;
}
