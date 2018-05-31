package org.orcid.jaxb.model.v3.rc1.client;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "name", "description" })
@XmlRootElement(name = "client", namespace = "http://www.orcid.org/ns/client")
public class ClientSummary implements Serializable {
    private static final long serialVersionUID = -3011951615514804083L;

    @XmlElement(namespace = "http://www.orcid.org/ns/client")
    protected String name;

    @XmlElement(namespace = "http://www.orcid.org/ns/client")
    protected String description;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        ClientSummary other = (ClientSummary) obj;
        if (name == null && other.name != null) {
            return false;
        } else if (!name.equals(other.name)) {
            return false;
        }

        if (description == null && other.description != null) {
            return false;
        } else if (!description.equals(other.description)) {
            return false;
        }

        return true;
    }
}
