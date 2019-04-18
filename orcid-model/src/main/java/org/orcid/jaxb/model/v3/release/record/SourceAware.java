package org.orcid.jaxb.model.v3.release.record;

import org.orcid.jaxb.model.v3.release.common.Source;

public interface SourceAware {
    void setSource(Source source);
    Source getSource();
}
