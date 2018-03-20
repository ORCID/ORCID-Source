package org.orcid.jaxb.model.record_rc2;

import org.orcid.jaxb.model.common_rc2.Source;

public interface SourceAware {
    void setSource(Source source);
    Source getSource();
}
