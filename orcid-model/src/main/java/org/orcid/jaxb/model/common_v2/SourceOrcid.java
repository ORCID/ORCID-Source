package org.orcid.jaxb.model.common_v2;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "sourceOrcid")
public class SourceOrcid extends OrcidIdBase implements Serializable {

    private static final long serialVersionUID = 1L;

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
