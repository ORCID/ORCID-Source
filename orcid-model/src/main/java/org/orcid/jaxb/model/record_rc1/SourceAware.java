package org.orcid.jaxb.model.record_rc1;

import org.orcid.jaxb.model.common_rc1.Source;

public interface SourceAware {
    void setSource(Source source);
    Source getSource();
}
