package org.orcid.jaxb.model.common_v2;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;

@XmlRootElement(name = "sourceClientId")
@ApiModel(value = "SourceClientIdV2_0")
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
