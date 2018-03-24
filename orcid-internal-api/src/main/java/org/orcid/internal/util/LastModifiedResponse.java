package org.orcid.internal.util;

import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class LastModifiedResponse {

    @XmlElement(name = "orcid")
    private String orcid;
    @XmlElement(name = "last-modified")
    private String lastModified;

    public LastModifiedResponse(String orcid, String lastModified) {
        this.orcid = orcid;
        this.lastModified = lastModified;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
}
