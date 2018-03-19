package org.orcid.jaxb.model.record_v2;

import javax.xml.bind.annotation.XmlRootElement;

import org.orcid.jaxb.model.common_v2.OrcidIdentifier;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlRootElement(name = "application-group-orcid")
public class GroupOrcid extends OrcidIdentifier {
    private static final long serialVersionUID = -7831298842584309866L;

}
