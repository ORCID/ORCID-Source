package org.orcid.jaxb.model.record_rc1;

import org.orcid.jaxb.model.common_rc1.Filterable;
import org.orcid.jaxb.model.common_rc1.LastModifiedDate;

public interface GroupableActivity extends Filterable {

    ExternalIdentifiersContainer getExternalIdentifiers();

    String getDisplayIndex();

    int compareTo(GroupableActivity activity);

	LastModifiedDate getLastModifiedDate();
}
