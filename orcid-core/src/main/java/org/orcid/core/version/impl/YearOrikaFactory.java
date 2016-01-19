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
package org.orcid.core.version.impl;

import org.orcid.jaxb.model.common_rc2.Year;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.ObjectFactory;

public class YearOrikaFactory implements ObjectFactory<Year> {

	@Override
	public Year create(Object source, MappingContext mappingContext) {
		Year year = new Year();
		year.setValue(((org.orcid.jaxb.model.common_rc1.Year) source).getValue());
		return year;
	}

}
