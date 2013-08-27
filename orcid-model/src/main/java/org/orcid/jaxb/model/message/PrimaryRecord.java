package org.orcid.jaxb.model.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}orcid-id" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Angel Montenegro
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "orcid", "orcidId"})
@XmlRootElement(name = "primary-record")
public class PrimaryRecord implements Serializable {
	/**
	 * TODO
	 */
	private static final long serialVersionUID = 8691353462048542411L;
	
	@XmlElement(name = "orcid")
    protected String orcid;
	@XmlElement(name = "orcid-id")
    protected String orcidId;
	public String getOrcid() {
		return orcid;
	}
	public void setOrcid(String orcid) {
		this.orcid = orcid;
	}
	public String getOrcidId() {
		return orcidId;
	}
	public void setOrcidId(String orcidId) {
		this.orcidId = orcidId;
	}	
	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PrimaryRecord other = (PrimaryRecord) obj;
        if(orcid == null){
        	if(other.getOrcid() != null)
        		return false;
        }
        
        if(!orcid.equals(other.getOrcid()))
        	return false;
        
        if(orcidId == null){
        	if(other.getOrcidId() != null)
        		return false;
        }
        if(!orcidId.equals(other.getOrcidId()))
        	return false;
        
        return true;
	}
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orcid == null) ? 0 : orcid.hashCode());
        result = prime * result + ((orcidId == null) ? 0 : orcidId.hashCode());        
        return result;
    }
}
