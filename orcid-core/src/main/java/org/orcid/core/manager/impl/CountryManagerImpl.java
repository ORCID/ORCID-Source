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
package org.orcid.core.manager.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.orcid.core.manager.CountryManager;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.springframework.cache.annotation.Cacheable;

/**
 * 
 * @author Will Simpson
 * 
 */
public class CountryManagerImpl implements CountryManager {

    @Resource(name = "isoCountryReferenceDataDao")
    private GenericDao<CountryIsoEntity, String> isoCountryReferenceDataDao;

    /**
     * Use {@link #retrieveCountriesAndIsoCodes()} instead.
     */
    @Override
    @Cacheable("country-list")
    @Deprecated
    public List<String> retrieveCountries() {
        try {
            return IOUtils.readLines(getClass().getResourceAsStream("countries.txt"));
        } catch (IOException e) {
            throw new RuntimeException("Problem reading countries.txt from classpath", e);
        }
    }

    @Override
    @Cacheable("iso-countries")
    public Map<String, String> retrieveCountriesAndIsoCodes() {
        List<CountryIsoEntity> countries = isoCountryReferenceDataDao.getAll();
        Collections.sort(countries, new Comparator<CountryIsoEntity>() {
            public int compare(CountryIsoEntity country1, CountryIsoEntity country2) {
                return ((String) country1.getCountryName()).compareToIgnoreCase((String) country2.getCountryName());
            }
        });

        Map<String, String> countriesMap = new LinkedHashMap<String, String>();

        for (CountryIsoEntity country : countries) {
            countriesMap.put(country.getCountryIsoCode(), country.getCountryName());
        }
        return countriesMap;
    }

}
