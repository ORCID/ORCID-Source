/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.web.util;

import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.persistence.jpa.entities.FuzzyDateEntity;


/**
 * Util class to provide conversions from Entities to Pojos and vice versa
 * */
public class Conversions {
	
	/**
	 * Converts from FuzzyDateEntity to FuzzyDate
	 * @param fde
	 * 	The FuzzyDateEntity
	 * @return a FuzzyDate object 
	 * */
	public static FuzzyDate toFuzzyDate(FuzzyDateEntity fde){
		return new FuzzyDate(fde.getYear(), fde.getMonth(), fde.getDay());		
	}
}
