package org.orcid.jaxb.model.v3.dev1.record;

import org.orcid.jaxb.model.v3.dev1.common.Source;

public interface SourceAware {
    void setSource(Source source);
    Source getSource();
}
