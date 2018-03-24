package org.orcid.jaxb.model.record_rc3;

import org.orcid.jaxb.model.common_rc3.Source;

public interface SourceAware {
    void setSource(Source source);
    Source getSource();
}
