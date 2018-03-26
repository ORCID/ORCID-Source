package org.orcid.core.manager;

import java.util.Map;

public interface ReferenceDataManager {

    Map<String, String> retrieveReferenceDataMap();

    String findReferenceDataValueByKey(String key);
}
