package org.orcid.core.manager;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface CountryManager {

    List<String> retrieveCountries();

    Map<String, String> retrieveCountriesAndIsoCodes();

}
