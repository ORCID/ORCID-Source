package org.orcid.core.manager;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.orcid.pojo.IdentifierType;

public interface IdentifierTypeManager {

	public IdentifierType createIdentifierType(IdentifierType id);

	public IdentifierType updateIdentifierType(IdentifierType id);

	IdentifierType fetchIdentifierTypeByDatabaseName(String name, Locale loc);

	default IdentifierType fetchIdentifierTypeByDatabaseName(String name) {
		return fetchIdentifierTypeByDatabaseName(name, Locale.ENGLISH);
	}

	Map<String, IdentifierType> fetchIdentifierTypesByAPITypeName(Locale loc);

	default Map<String, IdentifierType> fetchIdentifierTypesByAPITypeName() {
		return fetchIdentifierTypesByAPITypeName(Locale.ENGLISH);
	}
	
	List<IdentifierType> queryByPrefix(String query, Locale loc);

	default List<IdentifierType> queryByPrefix(String query) {
		return queryByPrefix(query, Locale.ENGLISH);
	}

	List<IdentifierType> fetchDefaultIdentifierTypes(Locale loc);

	default List<IdentifierType> fetchDefaultIdentifierTypes() {
		return fetchDefaultIdentifierTypes(Locale.ENGLISH);
	}
}
