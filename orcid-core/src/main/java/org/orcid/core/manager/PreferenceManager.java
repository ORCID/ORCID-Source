package org.orcid.core.manager;

import org.orcid.jaxb.model.common_v2.Visibility;

public interface PreferenceManager {
    boolean updateDefaultVisibility(String orcid, Visibility newValue);
}
