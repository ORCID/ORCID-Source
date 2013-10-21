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
package org.orcid.core.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.OrgManager;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import au.com.bytecode.opencsv.CSVReader;

/**
 * 
 * @author Will Simpson
 * 
 */
public class LoadRinggoldData {

    private static final String RINGGOLD_SOURCE_TYPE = "RINGGOLD";
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadRinggoldData.class);
    @Option(name = "-f", usage = "Path to file to load into DB", required = true)
    private File fileToLoad;
    private OrgDisambiguatedDao orgDisambiguatedDao;
    private OrgManager orgManager;
    private int numAdded;
    private int numUpdated;

    public static void main(String[] args) {
        LoadRinggoldData loadRinggoldData = new LoadRinggoldData();
        CmdLineParser parser = new CmdLineParser(loadRinggoldData);
        try {
            parser.parseArgument(args);
            loadRinggoldData.init();
            loadRinggoldData.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orgDisambiguatedDao = (OrgDisambiguatedDao) context.getBean("orgDisambiguatedDao");
        orgManager = (OrgManager) context.getBean("orgManager");
    }

    public void execute() {
        try (Reader reader = openFile(fileToLoad)) {
            processReader(reader);
        } catch (IOException e) {
            throw new RuntimeException("Error reading csv file", e);
        } finally {
            LOGGER.info("Number added={}, number updated={}, total={}", new Object[] { numAdded, numUpdated, numAdded + numUpdated });
        }
        LOGGER.info("Finished loading Ringgold data");
    }

    private Reader openFile(File fileToLoad) {
        Reader reader = null;
        try {
            FileInputStream fis = new FileInputStream(fileToLoad);
            reader = new InputStreamReader(fis, "UTF-8");
        } catch (FileNotFoundException e) {
            if (!fileToLoad.exists()) {
                throw new IllegalArgumentException("Input file does not exist: " + fileToLoad);
            }
            if (!fileToLoad.canRead()) {
                throw new IllegalArgumentException("Input exists, but can't read: " + fileToLoad);
            }
            throw new IllegalArgumentException("Unable to read input file: " + fileToLoad + "\n" + e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return reader;
    }

    private void processReader(Reader reader) throws IOException {
        try (CSVReader csvReader = new CSVReader(reader, ',', '"', 1)) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                processLine(line);
            }
        }
    }

    private void processLine(String[] line) {
        String pCode = line[1];
        String name = line[2];
        String extName = line[3];
        if (StringUtils.isNotBlank(extName)) {
            name = extName;
        }
        String city = line[4];
        String extCity = line[5];
        if (StringUtils.isNotBlank(extCity)) {
            city = extCity;
        }
        Iso3166Country country = parseCountry(line[7]);
        String state = line[8];
        if (StringUtils.isBlank(state)) {
            state = null;
        }
        String type = line[9];

        OrgDisambiguatedEntity existingEntity = orgDisambiguatedDao.findByNameCityRegionCountryAndSourceType(name, city, state, country, RINGGOLD_SOURCE_TYPE);
        OrgDisambiguatedEntity orgDisambiguatedEntity = null;
        boolean createNew = true;
        if (existingEntity == null) {
            LOGGER.info("No existing disambiguated org with sourceId={} and sourceType={}", pCode, RINGGOLD_SOURCE_TYPE);
            orgDisambiguatedEntity = new OrgDisambiguatedEntity();
        } else {
            if (!pCode.equals(existingEntity.getSourceId())) {
                LOGGER.info("Skipping disambiguated org with sourceId={} because it appears to be a duplicate of sourceId={}, sourceType={}", new Object[] { pCode,
                        existingEntity.getSourceId(), RINGGOLD_SOURCE_TYPE });
                return;
            }
            LOGGER.info("Found existing disambiguated org with sourceId={} and sourceType={}", pCode, RINGGOLD_SOURCE_TYPE);
            orgDisambiguatedEntity = existingEntity;
            createNew = false;
        }

        orgDisambiguatedEntity.setName(name);
        orgDisambiguatedEntity.setCity(city);
        orgDisambiguatedEntity.setRegion(state);
        orgDisambiguatedEntity.setCountry(country);
        orgDisambiguatedEntity.setOrgType(type);
        orgDisambiguatedEntity.setSourceId(pCode);
        orgDisambiguatedEntity.setSourceType(RINGGOLD_SOURCE_TYPE);

        if (createNew) {
            orgDisambiguatedDao.persist(orgDisambiguatedEntity);
            numAdded++;
        } else {
            orgDisambiguatedDao.merge(orgDisambiguatedEntity);
            numUpdated++;
        }

        // Ensure there is a corresponding org and that the org is linked to the
        // disambiguated org
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setName(name);
        orgEntity.setRegion(state);
        orgEntity.setCity(city);
        orgEntity.setCountry(country);
        orgManager.createUpdate(orgEntity, orgDisambiguatedEntity.getId());
    }

    private Iso3166Country parseCountry(String countryString) {
        countryString = countryString.toUpperCase();
        if ("USA".equals(countryString)) {
            countryString = "US";
        } else if ("CAN".equals(countryString)) {
            countryString = "CA";
        }
        return Iso3166Country.valueOf(countryString);
    }

}