package org.orcid.jaxb.model.v3.rc1.notification.permission;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 * @author Will Simpson
 *
 */
@XmlEnum
public enum ItemType {
    //@formatter:off
    @XmlEnumValue("distinction") DISTINCTION,
    @XmlEnumValue("education") EDUCATION,
    @XmlEnumValue("employment") EMPLOYMENT,
    @XmlEnumValue("invited-position") INVITED_POSITION,
    @XmlEnumValue("funding") FUNDING,
    @XmlEnumValue("membership") MEMBERSHIP,
    @XmlEnumValue("peer-review") PEER_REVIEW,
    @XmlEnumValue("qualification") QUALIFICATION,
    @XmlEnumValue("service") SERVICE,
    @XmlEnumValue("work") WORK;
    //@formatter:on
    
}
