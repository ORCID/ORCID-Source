package org.orcid.jaxb.model.v3.rc2.notification.permission;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import io.swagger.annotations.ApiModel;

/**
 * 
 * @author Will Simpson
 *
 */
@XmlEnum
@ApiModel(value = "ItemTypeV3_0_rc2")
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
    @XmlEnumValue("work") WORK, 
    @XmlEnumValue("research-resource") RESEARCH_RESOURCE;
    //@formatter:on
    
}
