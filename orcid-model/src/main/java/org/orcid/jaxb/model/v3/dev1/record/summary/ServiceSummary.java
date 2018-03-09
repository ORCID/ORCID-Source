package org.orcid.jaxb.model.v3.dev1.record.summary;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "service-summary", namespace = "http://www.orcid.org/ns/service")
public class ServiceSummary extends AffiliationSummary implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7314776433582416993L;

}
