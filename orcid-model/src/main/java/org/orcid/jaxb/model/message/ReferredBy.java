package org.orcid.jaxb.model.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "referred-by")
public class ReferredBy extends OrcidIdBase implements Serializable {

    private static final long serialVersionUID = 1L;

    public ReferredBy() {
        super();
    }

    public ReferredBy(String path) {
        super(path);
    }

    public ReferredBy(OrcidIdBase other) {
        super(other);
    }

}
