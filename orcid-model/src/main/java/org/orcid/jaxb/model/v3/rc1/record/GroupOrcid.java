package org.orcid.jaxb.model.v3.rc1.record;

import javax.xml.bind.annotation.XmlRootElement;

import org.orcid.jaxb.model.v3.rc1.common.OrcidIdentifier;

import io.swagger.annotations.ApiModel;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlRootElement(name = "application-group-orcid")
@ApiModel(value = "GroupOrcdV3_0_rc1")
public class GroupOrcid extends OrcidIdentifier {
    private static final long serialVersionUID = -7831298842584309866L;

}
