package org.orcid.jaxb.model.v3.rc2.record;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.message.WorkExternalIdentifier;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "externalIdentifiers" })
@XmlRootElement(name = "external-ids", namespace = "http://www.orcid.org/ns/common")

public class ExternalIDs implements Serializable, ExternalIdentifiersContainer{
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "external-id", namespace = "http://www.orcid.org/ns/common")
    protected List<ExternalID> externalIdentifiers;
    
    public List<ExternalID> getExternalIdentifier() {
        if (externalIdentifiers == null) {
            externalIdentifiers = new ArrayList<ExternalID>();
        }
        return this.externalIdentifiers;
    }  
    
    public static ExternalIDs valueOf(org.orcid.jaxb.model.message.WorkExternalIdentifiers messageWorkExternalIdentifiers) {
        ExternalIDs ids = new ExternalIDs();
        for (WorkExternalIdentifier id : messageWorkExternalIdentifiers.getWorkExternalIdentifier()){
            ids.getExternalIdentifier().add(ExternalID.fromMessageExtId(id));
        }
        return ids;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExternalIDs)) {
            return false;
        }

        ExternalIDs that = (ExternalIDs) o;

        if (externalIdentifiers == null) {
            if (that.externalIdentifiers != null)
                return false;
        } else {
            if (that.externalIdentifiers == null)
                return false;
            else if (!(externalIdentifiers.containsAll(that.externalIdentifiers) && that.externalIdentifiers.containsAll(externalIdentifiers) && 
                    that.externalIdentifiers.size() == externalIdentifiers.size())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = externalIdentifiers != null ? externalIdentifiers.hashCode() : 0;
        return result;
    }


}
