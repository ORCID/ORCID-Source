package org.orcid.jaxb.model.v3.release.record;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.release.common.MultipleOrganizationHolder;
import org.orcid.jaxb.model.v3.release.common.Organization;

import io.swagger.annotations.ApiModel;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "organization" })
@XmlRootElement(name = "hosts", namespace = "http://www.orcid.org/ns/research-resource")
@ApiModel(value = "ResearchResourceHostsV3_0")
public class ResearchResourceHosts implements Serializable, MultipleOrganizationHolder{
    private static final long serialVersionUID = -488401801848466689L;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "organization")
    protected List<Organization> organization;

    public List<Organization> getOrganization() {
        if (organization == null)
            organization = new ArrayList<Organization>();
        return organization;
    }

    public void setOrganization(List<Organization> organization) {
        this.organization = organization;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((organization == null) ? 0 : organization.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ResearchResourceHosts other = (ResearchResourceHosts) obj;
        if (organization == null) {
            if (other.organization != null)
                return false;
        } else if (!organization.equals(other.organization))
            return false;
        return true;
    }

}
