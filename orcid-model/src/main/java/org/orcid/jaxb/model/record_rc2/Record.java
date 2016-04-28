/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.jaxb.model.record_rc2;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common_rc2.LastModifiedDate;
import org.orcid.jaxb.model.common_rc2.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "lastModifiedDate", "orcidIdentifier", "deprecated", "preferences", "history", "person", "activitiesSummary" })
@XmlRootElement(name = "record", namespace = "http://www.orcid.org/ns/record")
public class Record implements Serializable {
    private static final long serialVersionUID = 1086932594400451295L;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "last-modified-date")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "orcid-identifier")
    protected OrcidIdentifier orcidIdentifier;
    @XmlElement(namespace = "http://www.orcid.org/ns/deprecated", name = "deprecated")
    protected Deprecated deprecated;
    @XmlElement(namespace = "http://www.orcid.org/ns/preferences", name = "preferences")
    protected Preferences preferences;
    @XmlElement(namespace = "http://www.orcid.org/ns/history", name = "history")
    protected History history;
    @XmlElement(namespace = "http://www.orcid.org/ns/person", name = "person")
    protected Person person;
    @XmlElement(namespace = "http://www.orcid.org/ns/activities", name = "activities-summary")
    protected ActivitiesSummary activitiesSummary;
    @XmlTransient
    protected OrcidType orcidType;

    public LastModifiedDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LastModifiedDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public OrcidIdentifier getOrcidIdentifier() {
        return orcidIdentifier;
    }

    public void setOrcidIdentifier(OrcidIdentifier orcidIdentifier) {
        this.orcidIdentifier = orcidIdentifier;
    }

    public Deprecated getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Deprecated deprecated) {
        this.deprecated = deprecated;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public ActivitiesSummary getActivitiesSummary() {
        return activitiesSummary;
    }

    public void setActivitiesSummary(ActivitiesSummary activitiesSummary) {
        this.activitiesSummary = activitiesSummary;
    }

    public OrcidType getOrcidType() {
        return orcidType;
    }

    public void setOrcidType(OrcidType orcidType) {
        this.orcidType = orcidType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((activitiesSummary == null) ? 0 : activitiesSummary.hashCode());
        result = prime * result + ((deprecated == null) ? 0 : deprecated.hashCode());
        result = prime * result + ((history == null) ? 0 : history.hashCode());
        result = prime * result + ((lastModifiedDate == null) ? 0 : lastModifiedDate.hashCode());
        result = prime * result + ((orcidIdentifier == null) ? 0 : orcidIdentifier.hashCode());
        result = prime * result + ((orcidType == null) ? 0 : orcidType.hashCode());
        result = prime * result + ((person == null) ? 0 : person.hashCode());
        result = prime * result + ((preferences == null) ? 0 : preferences.hashCode());
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
        Record other = (Record) obj;
        if (activitiesSummary == null) {
            if (other.activitiesSummary != null)
                return false;
        } else if (!activitiesSummary.equals(other.activitiesSummary))
            return false;
        if (deprecated == null) {
            if (other.deprecated != null)
                return false;
        } else if (!deprecated.equals(other.deprecated))
            return false;
        if (history == null) {
            if (other.history != null)
                return false;
        } else if (!history.equals(other.history))
            return false;
        if (lastModifiedDate == null) {
            if (other.lastModifiedDate != null)
                return false;
        } else if (!lastModifiedDate.equals(other.lastModifiedDate))
            return false;
        if (orcidIdentifier == null) {
            if (other.orcidIdentifier != null)
                return false;
        } else if (!orcidIdentifier.equals(other.orcidIdentifier))
            return false;
        if (orcidType != other.orcidType)
            return false;
        if (person == null) {
            if (other.person != null)
                return false;
        } else if (!person.equals(other.person))
            return false;
        if (preferences == null) {
            if (other.preferences != null)
                return false;
        } else if (!preferences.equals(other.preferences))
            return false;
        return true;
    }
}
