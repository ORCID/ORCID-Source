package org.orcid.core.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.release.record.AffiliationType;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "putCode", "endDate", "organizationName", "validated" })
@XmlRootElement(name = "employment", namespace = "http://www.orcid.org/ns/summary")
@Schema(description = "Employment")
public class Employment extends ProfessionalActivity {
    @XmlTransient
    private String type = AffiliationType.EMPLOYMENT.name();
    
    
}
