package org.orcid.jaxb.model.v3.rc2.record;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.rc2.common.Url;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "resourceName", "resourceType", "hosts", "externalIdentifiers", "url" })
@XmlRootElement(name = "resource-item", namespace = "http://www.orcid.org/ns/research-resource")
public class ResearchResourceItem {

    @XmlElement(namespace = "http://www.orcid.org/ns/research-resource", name = "resource-name")
    protected String resourceName;
    @XmlElement(namespace = "http://www.orcid.org/ns/research-resource", name = "resource-type")
    protected String resourceType;
    @XmlElement(namespace = "http://www.orcid.org/ns/research-resource", name = "hosts")
    protected ResearchResourceHosts hosts;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "external-ids")
    protected ExternalIDs externalIdentifiers;
    @XmlElement(namespace = "http://www.orcid.org/ns/common")
    protected Url url;

    public String getName() {
        return resourceName;
    }

    public void setName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public ResearchResourceHosts getHosts() {
        if (hosts == null)
            hosts = new ResearchResourceHosts();
        return hosts;
    }

    public void setHosts(ResearchResourceHosts hosts) {
        this.hosts = hosts;
    }

    public ExternalIDs getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(ExternalIDs externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((externalIdentifiers == null) ? 0 : externalIdentifiers.hashCode());
        result = prime * result + ((hosts == null) ? 0 : hosts.hashCode());
        result = prime * result + ((resourceName == null) ? 0 : resourceName.hashCode());
        result = prime * result + ((resourceType == null) ? 0 : resourceType.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
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
        ResearchResourceItem other = (ResearchResourceItem) obj;
        if (externalIdentifiers == null) {
            if (other.externalIdentifiers != null)
                return false;
        } else if (!externalIdentifiers.equals(other.externalIdentifiers))
            return false;
        if (hosts == null) {
            if (other.hosts != null)
                return false;
        } else if (!hosts.equals(other.hosts))
            return false;
        if (resourceName == null) {
            if (other.resourceName != null)
                return false;
        } else if (!resourceName.equals(other.resourceName))
            return false;
        if (resourceType == null) {
            if (other.resourceType != null)
                return false;
        } else if (!resourceType.equals(other.resourceType))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

}
