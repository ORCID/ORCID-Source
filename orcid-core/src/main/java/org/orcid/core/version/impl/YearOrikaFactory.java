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
