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
package org.orcid.solr.filter;

import java.io.File;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Will Simpson
 *
 */
public class SolrMigrationFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolrMigrationFilter.class);

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) throws ServletException {
        LOGGER.info("Initializing SOLR migration filter");
        String solrDataDirProperty = System.getProperty("solr.data.dir");
        LOGGER.info("SOLR data dir is {}", solrDataDirProperty);
        if (StringUtils.isNotBlank(solrDataDirProperty)) {
            File solrDataDir = new File(solrDataDirProperty);
            File profileSubDirectory = new File(solrDataDir, "profile");
            if (profileSubDirectory.exists()) {
                LOGGER.info("Profile subdirectory already exists, so no migration needed: {}", profileSubDirectory);
            } else {
                mkdir(profileSubDirectory);
                File indexDir = new File(solrDataDir, "index");
                if (indexDir.exists()) {
                    LOGGER.info("Found index directory to migrate: {}", indexDir);
                    File newIndexDir = new File(profileSubDirectory, "index");
                    move(indexDir, newIndexDir);
                }
                File spellcheckerDir = new File(solrDataDir, "spellchecker");
                if (spellcheckerDir.exists()) {
                    LOGGER.info("Found spellchecker directory to migrate: {}", spellcheckerDir);
                    File newSpellcheckerDir = new File(profileSubDirectory, "spellchecker");
                    move(spellcheckerDir, newSpellcheckerDir);
                }
            }
        }
    }

    private void move(File oldFile, File newFile) {
        boolean result = oldFile.renameTo(newFile);
        if (!result) {
            throw new RuntimeException("Error moving " + oldFile + " to " + newFile);
        }
    }

    private void mkdir(File directory) {
        boolean result = directory.mkdir();
        if (!result) {
            throw new RuntimeException("Error making directory: " + directory);
        }
    }

}
