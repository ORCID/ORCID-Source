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
package org.orcid.jaxb.model.groupid_v2;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common_v2.LastModifiedDate;

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
 *         &lt;element name="total" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="page" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="page-size" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element ref="{http://www.orcid.org/ns/group-id}group-id-record" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "lastModifiedDate", "total", "page", "pageSize", "groupIdRecord" })
@XmlRootElement(name = "group-id", namespace = "http://www.orcid.org/ns/group-id")
public class GroupIdRecords {

    @XmlElement(name = "last-modified-date", namespace = "http://www.orcid.org/ns/common")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(required = true, namespace = "http://www.orcid.org/ns/group-id")
    protected int total;
    @XmlElement(required = true, namespace = "http://www.orcid.org/ns/group-id")
    protected int page;
    @XmlElement(name = "page-size", required = true, namespace = "http://www.orcid.org/ns/group-id")
    protected int pageSize;
    @XmlElement(name = "group-id-record", namespace = "http://www.orcid.org/ns/group-id")
    protected List<GroupIdRecord> groupIdRecord;

    /**
     * Gets the value of the total property.
     * 
     * @return possible object is {@link int }
     * 
     */
    public int getTotal() {
        return total;
    }

    /**
     * Sets the value of the total property.
     * 
     * @param value
     *            allowed object is {@link int }
     * 
     */
    public void setTotal(int value) {
        this.total = value;
    }

    /**
     * Gets the value of the page property.
     * 
     * @return possible object is {@link int }
     * 
     */
    public int getPage() {
        return page;
    }

    /**
     * Sets the value of the page property.
     * 
     * @param value
     *            allowed object is {@link int }
     * 
     */
    public void setPage(int value) {
        this.page = value;
    }

    /**
     * Gets the value of the pageSize property.
     * 
     * @return possible object is {@link int }
     * 
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Sets the value of the pageSize property.
     * 
     * @param value
     *            allowed object is {@link int }
     * 
     */
    public void setPageSize(int value) {
        this.pageSize = value;
    }

    /**
     * Gets the value of the groupIdRecord property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the groupIdRecord property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getGroupIdRecord().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GroupIdRecord }
     * 
     * 
     */
    public List<GroupIdRecord> getGroupIdRecord() {
        if (groupIdRecord == null) {
            groupIdRecord = new ArrayList<GroupIdRecord>();
        }
        return this.groupIdRecord;
    }

    public LastModifiedDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LastModifiedDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((groupIdRecord == null) ? 0 : groupIdRecord.hashCode());
        result = prime * result + ((lastModifiedDate == null) ? 0 : lastModifiedDate.hashCode());
        result = prime * result + page;
        result = prime * result + pageSize;
        result = prime * result + total;
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
        GroupIdRecords other = (GroupIdRecords) obj;
        if (groupIdRecord == null) {
            if (other.groupIdRecord != null)
                return false;
        } else if (!groupIdRecord.equals(other.groupIdRecord))
            return false;
        if (lastModifiedDate == null) {
            if (other.lastModifiedDate != null)
                return false;
        } else if (!lastModifiedDate.equals(other.lastModifiedDate))
            return false;
        if (page != other.page)
            return false;
        if (pageSize != other.pageSize)
            return false;
        if (total != other.total)
            return false;
        return true;
    }

}
