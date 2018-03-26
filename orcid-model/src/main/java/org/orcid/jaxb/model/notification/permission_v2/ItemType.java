package org.orcid.jaxb.model.notification.permission_v2;

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
