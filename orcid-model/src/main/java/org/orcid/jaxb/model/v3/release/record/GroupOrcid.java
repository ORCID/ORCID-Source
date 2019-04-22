package org.orcid.jaxb.model.v3.release.record;

import javax.xml.bind.annotation.XmlRootElement;

import org.orcid.jaxb.model.v3.release.common.OrcidIdentifier;

import io.swagger.annotations.ApiModel;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlRootElement(name = "application-group-orcid")
@ApiModel(value = "GroupOrcidV3_0")
public class GroupOrcid extends OrcidIdentifier {
    private static final long serialVersionUID = -7831298842584309866L;

}
