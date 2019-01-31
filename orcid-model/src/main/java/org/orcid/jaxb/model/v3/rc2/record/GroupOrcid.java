package org.orcid.jaxb.model.v3.rc2.record;

import javax.xml.bind.annotation.XmlRootElement;

import org.orcid.jaxb.model.v3.rc2.common.OrcidIdentifier;

import io.swagger.annotations.ApiModel;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlRootElement(name = "application-group-orcid")
@ApiModel(value = "GroupOrcidV3_0_rc2")
public class GroupOrcid extends OrcidIdentifier {
    private static final long serialVersionUID = -7831298842584309866L;

}
