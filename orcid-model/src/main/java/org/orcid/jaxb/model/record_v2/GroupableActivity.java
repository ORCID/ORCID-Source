package org.orcid.jaxb.model.record_v2;

import org.orcid.jaxb.model.common_v2.Filterable;
import org.orcid.jaxb.model.common_v2.LastModifiedDate;

public interface GroupableActivity extends Filterable {

    ExternalIdentifiersContainer getExternalIdentifiers();

    String getDisplayIndex();

    int compareTo(GroupableActivity activity);

    LastModifiedDate getLastModifiedDate();        
}
