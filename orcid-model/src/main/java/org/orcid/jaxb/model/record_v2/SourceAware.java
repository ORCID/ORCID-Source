package org.orcid.jaxb.model.record_v2;

import org.orcid.jaxb.model.common_v2.Source;

public interface SourceAware {
    void setSource(Source source);
    Source getSource();
}
