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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.OrgManager;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.utils.NullUtils;
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

    private static final String RINGGOLD_CHARACTER_ENCODING = "UTF-8";
    private static final String RINGGOLD_SOURCE_TYPE = "RINGGOLD";
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadRinggoldData.class);
    @Option(name = "-f", usage = "Path to CSV file containing Ringgold parents to load into DB")
    private File fileToLoad;
    @Option(name = "-d", usage = "Path to CSV file containing Ringgold deleted IDs to process")
    private File deletedIdsFile;
    @Option(name = "-z", usage = "Path to zip file containing Ringgold data to process")
    private File zipFile;
    private OrgDisambiguatedDao orgDisambiguatedDao;
    private OrgManager orgManager;
    private int numAdded;
    private int numUpdated;
    private int numUnchanged;
    private int numSkipped;
    private int numDeleted;

    public static void main(String[] args) {
        LoadRinggoldData loadRinggoldData = new LoadRinggoldData();
        CmdLineParser parser = new CmdLineParser(loadRinggoldData);
        try {
            parser.parseArgument(args);
            loadRinggoldData.validateArgs(parser);
            loadRinggoldData.init();
            loadRinggoldData.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (NullUtils.allNull(fileToLoad, deletedIdsFile, zipFile)) {
            throw new CmdLineException(parser, "At least one of -f | -d | -z must be specificed");
        }
    }

    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orgDisambiguatedDao = (OrgDisambiguatedDao) context.getBean("orgDisambiguatedDao");
        orgManager = (OrgManager) context.getBean("orgManager");
    }

    public void execute() {
        if (fileToLoad != null) {
            processParentsCsv();
        }
        if (deletedIdsFile != null) {
            processDeletedIds();
        }
        if (zipFile != null) {
            processZip();
        }
        LOGGER.info("Finished loading Ringgold data");
    }

    private void processDeletedIds() {
        try (Reader reader = openFile(deletedIdsFile)) {
            processDeletedIdsReader(reader);
        } catch (IOException e) {
            throw new RuntimeException("Error reading csv file", e);
        } finally {
            LOGGER.info("Number deleted={}", new Object[] { numDeleted });
        }

    }

    private void processZip() {
        try (ZipFile zip = new ZipFile(zipFile)) {
            for (ZipEntry entry : Collections.list(zip.entries())) {
                String entryName = entry.getName();
                if (entryName.endsWith("_parents.csv")) {
                    LOGGER.info("Found parents file: " + entryName);
                    Reader reader = getReader(zip, entry);
                    processReader(reader);
                }
                if (entryName.endsWith("deleted_ids.csv")) {
                    LOGGER.info("Found deleted ids file: " + entryName);
                    Reader reader = getReader(zip, entry);
                    processDeletedIdsReader(reader);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading zip file", e);
        }
    }

    private Reader getReader(ZipFile zip, ZipEntry entry) throws IOException, UnsupportedEncodingException {
        InputStream is = zip.getInputStream(entry);
        Reader reader = new InputStreamReader(is, RINGGOLD_CHARACTER_ENCODING);
        return reader;
    }

    private void processParentsCsv() {
        try (Reader reader = openFile(fileToLoad)) {
            processReader(reader);
        } catch (IOException e) {
            throw new RuntimeException("Error reading csv file", e);
        }
    }

    private Reader openFile(File fileToLoad) {
        Reader reader = null;
        try {
            FileInputStream fis = new FileInputStream(fileToLoad);
            reader = new InputStreamReader(fis, RINGGOLD_CHARACTER_ENCODING);
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
        try (CSVReader csvReader = createCSVReader(reader)) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                processLine(line);
            }
        } finally {
            LOGGER.info("Number added={}, number updated={}, number unchanged={}, num skipped={}, total={}", new Object[] { numAdded, numUpdated, numUnchanged,
                    numSkipped, getTotal() });
        }
    }

    private int getTotal() {
        return numAdded + numUpdated + numUnchanged + numSkipped;
    }

    private CSVReader createCSVReader(Reader reader) {
        return new CSVReader(reader, ',', '"', 1);
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

        processOrg(pCode, name, city, state, country, type);
    }

    private void processOrg(String pCode, String name, String city, String state, Iso3166Country country, String type) {
        OrgDisambiguatedEntity existingEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(pCode, RINGGOLD_SOURCE_TYPE);
        if (existingEntity == null) {
            LOGGER.info("No existing disambiguated org with sourceId={} and sourceType={}", pCode, RINGGOLD_SOURCE_TYPE);
            processNew(pCode, name, city, state, country, type);
        } else {
            LOGGER.info("Found existing disambiguated org with sourceId={} and sourceType={}", pCode, RINGGOLD_SOURCE_TYPE);
            processExisting(existingEntity, pCode, name, city, country, state, type);
        }
    }

    private void processNew(String pCode, String name, String city, String state, Iso3166Country country, String type) {
        if (isDuplicate(pCode, name, city, state, country)) {
            return;
        }
        OrgDisambiguatedEntity orgDisambiguatedEntity = new OrgDisambiguatedEntity();
        setFields(orgDisambiguatedEntity, pCode, name, city, country, state, type);
        orgDisambiguatedDao.persist(orgDisambiguatedEntity);
        createOrUpdateOrg(name, city, country, state, orgDisambiguatedEntity.getId());
        numAdded++;
    }

    private void processExisting(OrgDisambiguatedEntity existingEntity, String pCode, String name, String city, Iso3166Country country, String state, String type) {
        if (!hasChanged(existingEntity, name, city, country, state, type)) {
            numUnchanged++;
            return;
        }
        existingEntity.setIndexingStatus(IndexingStatus.PENDING);
        setFields(existingEntity, pCode, name, city, country, state, type);
        orgDisambiguatedDao.merge(existingEntity);
        createOrUpdateOrg(name, city, country, state, existingEntity.getId());
        numUpdated++;
    }

    private boolean isDuplicate(String pCode, String name, String city, String state, Iso3166Country country) {
        OrgDisambiguatedEntity duplicate = orgDisambiguatedDao.findByNameCityRegionCountryAndSourceType(name, city, state, country, RINGGOLD_SOURCE_TYPE);
        if (duplicate != null) {
            LOGGER.info("Skipping disambiguated org with sourceId={} because it appears to be a duplicate of sourceId={}, sourceType={}",
                    new Object[] { pCode, duplicate.getSourceId(), RINGGOLD_SOURCE_TYPE });
            numSkipped++;
            return true;
        }
        return false;
    }

    private void createOrUpdateOrg(String name, String city, Iso3166Country country, String state, Long orgDisambiguatedId) {
        // Ensure there is a corresponding org and that the org is linked to the
        // disambiguated org
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setName(name);
        orgEntity.setRegion(state);
        orgEntity.setCity(city);
        orgEntity.setCountry(country);
        orgManager.createUpdate(orgEntity, orgDisambiguatedId);
    }

    private boolean hasChanged(OrgDisambiguatedEntity existingEntity, String name, String city, Iso3166Country country, String state, String type) {
        if (!name.equals(existingEntity.getName())) {
            return true;
        }
        if (!city.equals(existingEntity.getCity())) {
            return true;
        }
        if (!country.equals(existingEntity.getCountry())) {
            return true;
        }
        String existingRegion = existingEntity.getRegion();
        if (state == null) {
            if (existingRegion != null) {
                return true;
            }
        } else if (!state.equals(existingRegion)) {
            return true;
        }
        if (!type.equals(existingEntity.getOrgType())) {
            return true;
        }
        return false;
    }

    private void setFields(OrgDisambiguatedEntity orgDisambiguatedEntity, String pCode, String name, String city, Iso3166Country country, String state, String type) {
        orgDisambiguatedEntity.setName(name);
        orgDisambiguatedEntity.setCity(city);
        orgDisambiguatedEntity.setRegion(state);
        orgDisambiguatedEntity.setCountry(country);
        orgDisambiguatedEntity.setOrgType(type);
        orgDisambiguatedEntity.setSourceId(pCode);
        orgDisambiguatedEntity.setSourceType(RINGGOLD_SOURCE_TYPE);
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

    private void processDeletedIdsReader(Reader reader) throws IOException {
        try (CSVReader csvReader = createCSVReader(reader)) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                processDeletedIdsLine(line);
            }
        } finally {
            LOGGER.info("Number deleted={}", numDeleted);
        }
    }

    private void processDeletedIdsLine(String[] line) {
        String deletedId = line[0];
        OrgDisambiguatedEntity entity = orgDisambiguatedDao.findBySourceIdAndSourceType(deletedId, RINGGOLD_SOURCE_TYPE);
        if (entity != null) {
            LOGGER.info("Deleted ID exists in DB, id={}", deletedId);
            // Should actually delete, or flag status?
            numDeleted++;
        }
    }

}