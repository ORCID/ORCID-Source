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
