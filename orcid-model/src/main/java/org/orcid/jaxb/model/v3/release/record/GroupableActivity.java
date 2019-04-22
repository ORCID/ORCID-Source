package org.orcid.jaxb.model.v3.release.record;

import org.orcid.jaxb.model.v3.release.common.Filterable;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;

public interface GroupableActivity extends Filterable {

    ExternalIdentifiersContainer getExternalIdentifiers();

    String getDisplayIndex();

    int compareTo(GroupableActivity activity);

    LastModifiedDate getLastModifiedDate();        
}
