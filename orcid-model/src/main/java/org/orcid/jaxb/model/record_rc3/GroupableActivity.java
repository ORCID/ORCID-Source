package org.orcid.jaxb.model.record_rc3;

import org.orcid.jaxb.model.common_rc3.Filterable;
import org.orcid.jaxb.model.common_rc3.LastModifiedDate;

public interface GroupableActivity extends Filterable {

    ExternalIdentifiersContainer getExternalIdentifiers();

    String getDisplayIndex();

    int compareTo(GroupableActivity activity);

    LastModifiedDate getLastModifiedDate();
}
