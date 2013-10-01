/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.jaxb.model.message;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.GroupType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}orcid" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}orcid-deprecated" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}orcid-history" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}orcid-bio" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}orcid-activities" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}orcid-internal" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" type="{http://www.orcid.org/ns/orcid}orcid-type" default="user" />
 *       &lt;attribute name="groupType" type="{http://www.orcid.org/ns/orcid}client-type" default="user" />
 *       &lt;attribute name="clientType" type="{http://www.orcid.org/ns/orcid}group-type" default="user" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "orcid", "orcidId", "orcidDeprecated", "orcidPreferences", "orcidHistory", "orcidBio", "orcidActivities", "orcidInternal" })
@XmlRootElement(name = "orcid-profile")
public class OrcidProfile implements Serializable {

    private static final long serialVersionUID = 1L;
    protected Orcid orcid;

    @XmlElement(name = "orcid-id")
    protected String orcidId;

    @XmlElement(name = "orcid-deprecated")
    private OrcidDeprecated orcidDeprecated;

    @XmlElement(name = "orcid-preferences")
    private OrcidPreferences orcidPreferences;

    @XmlElement(name = "orcid-history")
    protected OrcidHistory orcidHistory;

    @XmlElement(name = "orcid-bio")
    protected OrcidBio orcidBio;

    @XmlElement(name = "orcid-activities")
    protected OrcidActivities orcidActivities;

    @XmlElement(name = "orcid-internal")
    protected OrcidInternal orcidInternal;

    @XmlAttribute
    protected OrcidType type;

    @XmlAttribute(name = "group-type")
    protected GroupType groupType;

    @XmlAttribute(name = "client-type")
    protected ClientType clientType;

    // TODO: Look into where this should be
    @XmlTransient
    private String password;

    @XmlTransient
    private String verificationCode;

    @XmlTransient
    private String securityQuestionAnswer;

    /**
     * Gets the value of the orcid property.
     * 
     * @return possible object is {@link Orcid }
     * 
     */
    public Orcid getOrcid() {
        return orcid;
    }

    /**
     * Sets the value of the orcid property.
     * 
     * @param value
     *            allowed object is {@link Orcid }
     * 
     */
    public void setOrcid(Orcid value) {
        this.orcid = value;
    }

    public String getOrcidId() {
        return this.orcidId; // orcidId;
    }

    public void setOrcidId(String value) {
        this.orcidId = value;
    }

    /**
     * Sets the value of the orcid property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setOrcid(String value) {
        this.orcid = new Orcid(value);
    }

    /**
     * Gets the value of the orcidHistory property.
     * 
     * @return possible object is {@link OrcidHistory }
     * 
     */
    public OrcidHistory getOrcidHistory() {
        return orcidHistory;
    }

    public boolean isDeactivated() {
        return orcidHistory != null && orcidHistory.getDeactivationDate() != null && orcidHistory.getDeactivationDate().getValue() != null;
    }

    /**
     * Sets the value of the orcidHistory property.
     * 
     * @param value
     *            allowed object is {@link OrcidHistory }
     * 
     */
    public void setOrcidHistory(OrcidHistory value) {
        this.orcidHistory = value;
    }

    /**
     * Gets the value of the orcidBio property.
     * 
     * @return possible object is {@link OrcidBio }
     * 
     */
    public OrcidBio getOrcidBio() {
        return orcidBio;
    }

    /**
     * Sets the value of the orcidBio property.
     * 
     * @param value
     *            allowed object is {@link OrcidBio }
     * 
     */
    public void setOrcidBio(OrcidBio value) {
        this.orcidBio = value;
    }

    /**
     * 
     * @return the activites contained in this record
     */
    public OrcidActivities getOrcidActivities() {
        return orcidActivities;
    }

    /**
     * @param orcidActivities
     *            set the activities for this record
     */
    public void setOrcidActivities(OrcidActivities orcidActivities) {
        this.orcidActivities = orcidActivities;
    }

    /**
     * 
     * @return
     */
    public OrcidPatents retrieveOrcidPatents() {
        return orcidActivities != null ? orcidActivities.getOrcidPatents() : null;
    }

    /**
     * 
     * @param orcidGrants
     */
    public void setOrcidGrants(OrcidGrants orcidGrants) {
        if (orcidActivities == null) {
            orcidActivities = new OrcidActivities();
        }
        this.orcidActivities.setOrcidGrants(orcidGrants);
    }

    /**
     * 
     * @return
     */
    public OrcidGrants retrieveOrcidGrants() {
        return orcidActivities != null ? orcidActivities.getOrcidGrants() : null;
    }

    /**
     * 
     * @param orcidPatents
     */
    public void setOrcidPatents(OrcidPatents orcidPatents) {
        if (orcidActivities == null) {
            orcidActivities = new OrcidActivities();
        }
        this.orcidActivities.setOrcidPatents(orcidPatents);
    }

    /**
     * 
     * @return affiliations
     */
    public Affiliations retrieveAffiliations() {
        return orcidActivities != null ? orcidActivities.getAffiliations() : null;
    }

    /**
     * 
     * @param affiliations
     */
    public void setAffiliations(Affiliations affiliations) {
        if (orcidActivities == null) {
            orcidActivities = new OrcidActivities();
        }
        this.orcidActivities.setAffiliations(affiliations);
    }

    /**
     * 
     * @return
     */
    public OrcidWorks retrieveOrcidWorks() {
        return orcidActivities != null ? orcidActivities.getOrcidWorks() : null;
    }

    /**
     * 
     * @param orcidWorks
     */
    public void setOrcidWorks(OrcidWorks orcidWorks) {
        if (orcidActivities == null) {
            orcidActivities = new OrcidActivities();
        }
        this.orcidActivities.setOrcidWorks(orcidWorks);
    }

    /**
     * 
     * @param orcidWorks
     */
    public void setOrcidWork(List<OrcidWork> orcidWorks) {
        if (orcidActivities == null) {
            orcidActivities = new OrcidActivities();
        }
        this.orcidActivities.getOrcidWorks().setOrcidWork(orcidWorks);
    }

    /**
     * Gets the value of the orcidInternal property.
     * 
     * @return possible object is {@link OrcidInternal }
     * 
     */
    public OrcidInternal getOrcidInternal() {
        return orcidInternal;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getSecurityQuestionAnswer() {
        return securityQuestionAnswer;
    }

    public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
        this.securityQuestionAnswer = securityQuestionAnswer;
    }

    /**
     * Sets the value of the orcidInternal property.
     * 
     * @param value
     *            allowed object is {@link OrcidInternal }
     * 
     */
    public void setOrcidInternal(OrcidInternal value) {
        this.orcidInternal = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return possible object is {@link OrcidType }
     * 
     */
    public OrcidType getType() {
        if (type == null) {
            return OrcidType.USER;
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *            allowed object is {@link OrcidType }
     * 
     */
    public void setType(OrcidType value) {
        this.type = value;
    }

    /**
     * Gets the value of the groupType property.
     * 
     * @return possible object is {@link GroupType }
     * 
     */
    public GroupType getGroupType() {
        return groupType;
    }

    /**
     * Sets the value of the groupType property.
     * 
     * @param value
     *            allowed object is {@link GroupType }
     * 
     */
    public void setGroupType(GroupType value) {
        this.groupType = value;
    }

    /**
     * Gets the value of the clientType property.
     * 
     * @return possible object is {@link ClientType }
     * 
     */
    public ClientType getClientType() {
        return clientType;
    }

    /**
     * Sets the value of the clientType property.
     * 
     * @param value
     *            allowed object is {@link ClientType }
     * 
     */
    public void setClientType(ClientType value) {
        this.clientType = value;
    }

    /*
     * Gets the value of the orcidDeprecated property.
     * 
     * @return possible object is {@link OrcidDeprecated }
     */
    public OrcidDeprecated getOrcidDeprecated() {
        return orcidDeprecated;
    }

    /**
     * Sets the value of the orcidDeprecated property.
     * 
     * @param orcidDeprecated
     *            allowed object is {@link orcidDeprecated }
     * */
    public void setOrcidDeprecated(OrcidDeprecated orcidDeprecated) {
        this.orcidDeprecated = orcidDeprecated;
    }

    public void downgradeToBioOnly() {
        setOrcidActivities(null);
    }

    public void downgradeToExternalIdentifiersOnly() {
        downgradeToBioOnly();
        if (orcidBio != null) {
            orcidBio.downGradeToExternalIdentifiersOnly();
        }
    }

    public void downgradeToWorksOnly() {
        setOrcidBio(null);
        if (orcidActivities != null) {
            orcidActivities.downgradeToWorksOnly();
        }
    }

    public void downgradeToAffiliationsOnly() {
        setOrcidBio(null);
        if (orcidActivities != null) {
            orcidActivities.downgradeToAffiliationsOnly();
        }
    }
    
    @Override
    public String toString() {
        return OrcidMessage.convertToString(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orcid == null) ? 0 : orcid.hashCode());
        result = prime * result + ((orcidBio == null) ? 0 : orcidBio.hashCode());
        result = prime * result + ((orcidHistory == null) ? 0 : orcidHistory.hashCode());
        result = prime * result + ((orcidInternal == null) ? 0 : orcidInternal.hashCode());
        result = prime * result + ((orcidActivities == null) ? 0 : orcidActivities.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((securityQuestionAnswer == null) ? 0 : securityQuestionAnswer.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((groupType == null) ? 0 : groupType.hashCode());
        result = prime * result + ((clientType == null) ? 0 : clientType.hashCode());
        result = prime * result + ((verificationCode == null) ? 0 : verificationCode.hashCode());
        result = prime * result + ((orcidDeprecated == null) ? 0 : orcidDeprecated.hashCode());
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
        OrcidProfile other = (OrcidProfile) obj;
        if (orcid == null) {
            if (other.orcid != null)
                return false;
        } else if (!orcid.equals(other.orcid))
            return false;
        if (orcidBio == null) {
            if (other.orcidBio != null)
                return false;
        } else if (!orcidBio.equals(other.orcidBio))
            return false;
        if (orcidHistory == null) {
            if (other.orcidHistory != null)
                return false;
        } else if (!orcidHistory.equals(other.orcidHistory))
            return false;
        if (orcidInternal == null) {
            if (other.orcidInternal != null)
                return false;
        } else if (!orcidInternal.equals(other.orcidInternal))
            return false;
        if (orcidActivities == null) {
            if (other.orcidActivities != null)
                return false;
        } else if (!orcidActivities.equals(other.orcidActivities))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (securityQuestionAnswer == null) {
            if (other.securityQuestionAnswer != null)
                return false;
        } else if (!securityQuestionAnswer.equals(other.securityQuestionAnswer))
            return false;
        if (type != other.type)
            return false;
        if (groupType != other.groupType)
            return false;
        if (clientType != other.clientType)
            return false;
        if (verificationCode == null) {
            if (other.verificationCode != null)
                return false;
        } else if (!verificationCode.equals(other.verificationCode))
            return false;
        if (orcidDeprecated == null) {
            if (other.getOrcidDeprecated() != null)
                return false;
        } else if (!orcidDeprecated.equals(other.getOrcidDeprecated()))
            return false;

        return true;
    }

    public OrcidPreferences getOrcidPreferences() {
        return orcidPreferences;
    }

    public void setOrcidPreferences(OrcidPreferences orcidPreferences) {
        this.orcidPreferences = orcidPreferences;
    }

}
