package org.orcid.jaxb.model.v3.rc2.record;

import org.orcid.jaxb.model.v3.rc2.common.Filterable;
import org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate;

public interface GroupableActivity extends Filterable {

    ExternalIdentifiersContainer getExternalIdentifiers();

    String getDisplayIndex();

    int compareTo(GroupableActivity activity);

    LastModifiedDate getLastModifiedDate();        
}
