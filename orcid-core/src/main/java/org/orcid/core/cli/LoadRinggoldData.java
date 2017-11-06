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
package org.orcid.core.cli;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.utils.JsonUtils;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedSolrDao;
import org.orcid.utils.NullUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;

public class LoadRinggoldData {
    private static final String RINGGOLD_CHARACTER_ENCODING = "UTF-8";
    private static final String RINGGOLD_SOURCE_TYPE = "RINGGOLD";    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadRinggoldData.class);
    @Option(name = "-f", usage = "Path to json file containing Ringgold parents to load into DB")
    private File fileToLoad;
    @Option(name = "-d", usage = "Path to json file containing Ringgold deleted IDs to process")
    private File deletedIdsFile;
    @Option(name = "-z", usage = "Path to zip file containing Ringgold data to process")
    private File zipFile;
    
    private OrgDisambiguatedDao orgDisambiguatedDao;
    private OrgDisambiguatedSolrDao orgDisambiguatedSolrDao;
        
    private int numAdded;
    private int numUpdated;
    private int numUnchanged;
    private int numSkipped;
    private int numDeleted;
    private int numDeletionsSkipped;
    
    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orgDisambiguatedDao = (OrgDisambiguatedDao) context.getBean("orgDisambiguatedDao");
        orgDisambiguatedSolrDao = (OrgDisambiguatedSolrDao) context.getBean("orgDisambiguatedSolrDao");        
    }
    
    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (NullUtils.allNull(fileToLoad, deletedIdsFile, zipFile)) {
            throw new CmdLineException(parser, "At least one of -f | -d | -z must be specificed");
        }
    }
    
    private void processZip() {
        try (ZipFile zip = new ZipFile(zipFile)) {
            JsonNode institutions = getJsonNode(zip, zip.getEntry("Ringgold_Identify_json_institutions.json"));
            JsonNode altNames = getJsonNode(zip, zip.getEntry("Ringgold_Identify_json_alt_names.json"));
            JsonNode identifiers = getJsonNode(zip, zip.getEntry("Ringgold_Identify_json_identifiers.json"));
            JsonNode deletedIds = getJsonNode(zip, zip.getEntry("Ringgold_Identify_json_deleted_ids.json"));
            
            if(institutions != null) {
                 
            }
            
            if(deletedIds != null) {
                
            }            
        } catch (IOException e) {
            throw new RuntimeException("Error reading zip file", e);
        }
    }
    
    private JsonNode getJsonNode(ZipFile zip, ZipEntry entry) throws IOException, UnsupportedEncodingException {
        InputStream is = zip.getInputStream(entry);
        Reader reader = new InputStreamReader(is, RINGGOLD_CHARACTER_ENCODING);
        if(reader == null) {
            return null;
        }
        
        return JsonUtils.read(reader);
    }
    
    private void processInstitutions(JsonNode institutions, JsonNode altNames, JsonNode identifiers) {
        /**
         * 1. Read every line from institutions
         * 2. Use name in altNames if exists????
         * 3. Look for any ISNI external identifier in identifiers
         * */
    }
    
    private void processDeletedIds(JsonNode deletedIds) {
        
    }
}
