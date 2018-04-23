package org.orcid.jaxb.model.v3.rc1.record;

import org.orcid.jaxb.model.v3.rc1.common.Source;

public interface SourceAware {
    void setSource(Source source);
    Source getSource();
}
