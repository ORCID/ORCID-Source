package org.orcid.jaxb.model.record_rc4;

import org.orcid.jaxb.model.common_rc4.Source;

public interface SourceAware {
    void setSource(Source source);
    Source getSource();
}
