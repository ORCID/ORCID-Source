package org.orcid.core.manager;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.orcid.pojo.IdentifierType;

public interface IdentifierTypeManager {

	public IdentifierType createIdentifierType(IdentifierType id);

	public IdentifierType updateIdentifierType(IdentifierType id);

	IdentifierType fetchIdentifierTypeByDatabaseName(String name, Locale loc);

	Map<String, IdentifierType> fetchIdentifierTypesByAPITypeName(Locale loc);
	
        List<IdentifierType> queryByPrefix(String query, Locale loc);

        List<IdentifierType> fetchDefaultIdentifierTypes(Locale loc);
}
