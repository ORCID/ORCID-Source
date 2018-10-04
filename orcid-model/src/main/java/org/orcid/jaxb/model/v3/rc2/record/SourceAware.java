package org.orcid.jaxb.model.v3.rc2.record;

import org.orcid.jaxb.model.v3.rc2.common.Source;

public interface SourceAware {
    void setSource(Source source);
    Source getSource();
}
