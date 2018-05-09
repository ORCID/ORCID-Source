package org.orcid.jaxb.model.v3.rc1.record;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.rc1.common.Organization;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "organization" })
@XmlRootElement(name = "hosts", namespace = "http://www.orcid.org/ns/research-resource")
public class ResearchResourceHosts {

    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "organization")
    protected List<Organization> organization;

}
