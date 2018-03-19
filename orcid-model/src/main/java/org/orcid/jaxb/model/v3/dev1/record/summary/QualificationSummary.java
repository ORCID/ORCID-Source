package org.orcid.jaxb.model.v3.dev1.record.summary;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "qualification-summary", namespace = "http://www.orcid.org/ns/qualification")
public class QualificationSummary extends AffiliationSummary implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1058178985146686275L;

}
