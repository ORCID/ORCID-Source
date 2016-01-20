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
package org.orcid.jaxb.model.notification.permission_rc1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * 
 * @author Will Simpson
 *
 */
@XmlEnum
public enum ItemType {
    //@formatter:off
    @XmlEnumValue("education") EDUCATION,
    @XmlEnumValue("employment") EMPLOYMENT,
    @XmlEnumValue("funding") FUNDING,
    @XmlEnumValue("peer-review") PEER_REVIEW,
    @XmlEnumValue("work") WORK;
    //@formatter:on
}
