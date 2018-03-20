package org.orcid.jaxb.model.common_rc4;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "sourceClientId")
public class SourceClientId extends OrcidIdBase implements Serializable {

    private static final long serialVersionUID = 1L;

    public SourceClientId() {
        super();
    }

    public SourceClientId(String path) {
        super(path);
    }

    public SourceClientId(OrcidIdBase other) {
        super(other);
    }

}
