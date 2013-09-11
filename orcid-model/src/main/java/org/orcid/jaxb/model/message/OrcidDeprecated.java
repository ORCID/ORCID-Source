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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "date", "primaryRecord"})
@XmlRootElement(name = "deprecated")
public class OrcidDeprecated implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "deprecated-date")
    @XmlSchemaType(name = "dateTime")
    protected DeprecatedDate date;
    @XmlElement(name = "primary-record")
    protected PrimaryRecord primaryRecord;
    
	public DeprecatedDate getDate() {
		return date;
	}
	public void setDate(DeprecatedDate date) {
		this.date = date;
	}
	public PrimaryRecord getPrimaryRecord() {
		return primaryRecord;
	}
	public void setPrimaryRecord(PrimaryRecord primaryRecord) {
		this.primaryRecord = primaryRecord;
	}
    
	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OrcidDeprecated other = (OrcidDeprecated) obj;
        
        if(date == null){
        	if(other.getDate() != null)
        		return false;
        }
        if(!date.equals(other.getDate()))
        	return false;        
        if(primaryRecord == null){
        	if(other.getPrimaryRecord() != null)
        		return false;
        }        
        if(!primaryRecord.equals(other.getPrimaryRecord()))
        	return false;
        return true;
	}
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((primaryRecord == null) ? 0 : primaryRecord.hashCode());        
        return result;
    }
}
