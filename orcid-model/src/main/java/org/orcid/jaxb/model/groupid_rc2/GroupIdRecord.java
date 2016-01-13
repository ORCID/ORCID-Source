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
package org.orcid.jaxb.model.groupid_rc2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common.CreatedDate;
import org.orcid.jaxb.model.common.LastModifiedDate;
import org.orcid.jaxb.model.common.Source;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

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
 *     &lt;extension base="{http://www.orcid.org/ns/common}source-type">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.orcid.org/ns/common}string-1000"/>
 *         &lt;element name="group-id">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.orcid.org/ns/common}string-1000">
 *               &lt;pattern value="(ringgold:|issn:|orcid-generated:|fundref:).*"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="description" type="{http://www.orcid.org/ns/common}non-empty-string"/>
 *         &lt;element name="type">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="publisher"/>
 *               &lt;enumeration value="institution"/>
 *               &lt;enumeration value="journal"/>
 *               &lt;enumeration value="conference"/>
 *               &lt;enumeration value="newspaper"/>
 *               &lt;enumeration value="newsletter"/>
 *               &lt;enumeration value="magazine"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "name", "groupId", "description", "type", "source", "lastModifiedDate", "createdDate", "putCode" })
@XmlRootElement(name = "group-id-record", namespace = "http://www.orcid.org/ns/group-id")
public class GroupIdRecord {

    @XmlElement(namespace = "http://www.orcid.org/ns/group-id", required = true)
    protected String name;
    @XmlElement(name = "group-id", namespace = "http://www.orcid.org/ns/group-id", required = true)
    protected String groupId;
    @XmlElement(namespace = "http://www.orcid.org/ns/group-id", required = true)
    protected String description;
    @XmlElement(namespace = "http://www.orcid.org/ns/group-id", required = true)
    protected String type;
    @XmlElement(namespace = "http://www.orcid.org/ns/common")
    @JsonIgnore
    protected Source source;
    @XmlAttribute(name = "put-code")
    @ApiModelProperty(hidden = true) 
    @JsonIgnore
    protected Long putCode;
    @JsonIgnore
    @XmlElement(name = "last-modified-date", namespace = "http://www.orcid.org/ns/common")    
    protected LastModifiedDate lastModifiedDate;
    @JsonIgnore
    @XmlElement(name = "created-date", namespace = "http://www.orcid.org/ns/common")
    protected CreatedDate createdDate;

    /**
     * Gets the value of the name property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the groupId property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Sets the value of the groupId property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setGroupId(String value) {
        this.groupId = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setType(String value) {
        this.type = value;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Long getPutCode() {
        return putCode;
    }

    public void setPutCode(Long putCode) {
        this.putCode = putCode;
    }

    public LastModifiedDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LastModifiedDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public CreatedDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(CreatedDate createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isDuplicated(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GroupIdRecord other = (GroupIdRecord) obj;

        if (!this.groupId.equals(other.groupId)) {
            return false;
        }

        return true;
    }

    public String retrieveSourcePath() {
        if (source == null) {
            return null;
        }
        return source.retrieveSourcePath();
    }
}
