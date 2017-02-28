/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
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

        List<IdentifierType> fetchMostPopularIdentifierTypes(Locale loc);
}
