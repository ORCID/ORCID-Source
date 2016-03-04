package org.orcid.jaxb.model.record_rc2;

import org.orcid.jaxb.model.common_rc2.LastModifiedDate;

public interface LastModifiedAware {
    boolean shouldSetLastModified();
    void setLastModifiedDate(LastModifiedDate lastModifiedDate);
}
