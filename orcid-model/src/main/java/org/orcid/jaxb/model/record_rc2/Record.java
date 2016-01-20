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

import javax.xml.bind.annotation.XmlElement;

import org.orcid.jaxb.model.common_rc2.LastModifiedDate;
import org.orcid.jaxb.model.common_rc2.OrcidIdentifier;

public class Record {
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "last-modified-date")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "orcid-identifier")
    protected OrcidIdentifier orcidIdentifier;
    @XmlElement(namespace = "http://www.orcid.org/ns/deprecated", name = "deprecated")
    protected Deprecated deprecated;    
    @XmlElement(namespace = "http://www.orcid.org/ns/preferences", name = "preferences")
    protected Preferences preferences;
}
