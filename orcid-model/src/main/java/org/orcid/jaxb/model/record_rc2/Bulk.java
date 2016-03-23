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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "bulk" })
@XmlRootElement(name = "bulk", namespace = "http://www.orcid.org/ns/work")
public class Bulk implements Serializable {
    private static final long serialVersionUID = 5371451020817591926L;
    @XmlElements({
        @XmlElement(name = "work", type = Work.class),
        @XmlElement(name = "error", type = Error.class)
    })
    private List<WorkBulkElement> bulk;

    public List<WorkBulkElement> getBulk() {
        return bulk;
    }

    public void setBulk(List<WorkBulkElement> bulk) {
        this.bulk = bulk;
    }
}
