package org.orcid.jaxb.model.v3.rc1.common;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;

@XmlRootElement(name = "sourceOrcid")
@ApiModel(value = "SourceOrcidV3_0_rc1")
public class SourceOrcid extends OrcidIdBase implements Serializable {
    private static final long serialVersionUID = 1973161312082111053L;

    public SourceOrcid() {
        super();
    }

    public SourceOrcid(String path) {
        super(path);
    }

    public SourceOrcid(OrcidIdBase other) {
        super(other);
    }

}
