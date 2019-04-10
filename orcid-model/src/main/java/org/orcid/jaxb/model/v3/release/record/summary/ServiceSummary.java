package org.orcid.jaxb.model.v3.release.record.summary;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;

@XmlRootElement(name = "service-summary", namespace = "http://www.orcid.org/ns/service")
@ApiModel(value = "ServiceSummaryV3_0")
public class ServiceSummary extends AffiliationSummary implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7314776433582416993L;

}
