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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.Pair;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedSolrDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.solr.entities.OrgDisambiguatedSolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrgDisambiguatedManagerImpl implements OrgDisambiguatedManager {

    private static final int INDEXING_CHUNK_SIZE = 1000;
    private static final int INCORRECT_POPULARITY_CHUNK_SIZE = 1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(OrgDisambiguatedManagerImpl.class);

    @Resource
    private OrgDisambiguatedDao orgDisambiguatedDao;

    @Resource
    private OrgDisambiguatedSolrDao orgDisambiguatedSolrDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public void processOrgsForIndexing() {
        LOGGER.info("About to process disambiguated orgs for indexing");
        List<OrgDisambiguatedEntity> entities = null;
        do {
            entities = orgDisambiguatedDao.findOrgsByIndexingStatus(IndexingStatus.PENDING, 0, INDEXING_CHUNK_SIZE);
            LOGGER.info("Found chunk of {} disambiguated orgs for indexing", entities.size());
            for (OrgDisambiguatedEntity entity : entities) {
                processDisambiguatedOrgInTransaction(entity);
            }
        } while (!entities.isEmpty());

    }

    private void processDisambiguatedOrgInTransaction(final OrgDisambiguatedEntity entity) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                processDisambiguatedOrg(orgDisambiguatedDao.find(entity.getId()));
            }
        });
    }

    private void processDisambiguatedOrg(OrgDisambiguatedEntity entity) {
        LOGGER.info("About to index disambiguated org, id={}", entity.getId());
        OrgDisambiguatedSolrDocument document = convertEntityToDocument(entity);
        orgDisambiguatedSolrDao.persist(document);
        orgDisambiguatedDao.updateIndexingStatus(entity.getId(), IndexingStatus.DONE);
    }

    private OrgDisambiguatedSolrDocument convertEntityToDocument(OrgDisambiguatedEntity entity) {
        OrgDisambiguatedSolrDocument document = new OrgDisambiguatedSolrDocument();
        document.setOrgDisambiguatedId(entity.getId());
        document.setOrgDisambiguatedName(entity.getName());
        document.setOrgDisambiguatedCity(entity.getCity());
        document.setOrgDisambiguatedRegion(entity.getRegion());
        document.setOrgDisambiguatedCountry(entity.getCountry().value());
        document.setOrgDisambiguatedType(entity.getOrgType());
        document.setOrgDisambiguatedPopularity(entity.getPopularity());
        Set<String> orgNames = new HashSet<>();
        orgNames.add(entity.getName());
        Set<OrgEntity> orgs = entity.getOrgs();
        if (orgs != null) {
            for (OrgEntity org : orgs) {
                orgNames.add(org.getName());
            }
        }
        document.setOrgNames(new ArrayList<>(orgNames));
        return document;
    }

    @Override
    public void processOrgsWithIncorrectPopularity() {
        LOGGER.info("About to process disambiguated orgs with incorrect popularity");
        List<Pair<Long, Integer>> pairs = null;
        do {
            pairs = orgDisambiguatedDao.findDisambuguatedOrgsWithIncorrectPopularity(INCORRECT_POPULARITY_CHUNK_SIZE);
            LOGGER.info("Found chunk of {} disambiguated orgs with incorrect popularity", pairs.size());
            for (Pair<Long, Integer> pair : pairs) {
                LOGGER.info("About to update popularity of disambiguated org: {}", pair);
                orgDisambiguatedDao.updatePopularity(pair.getLeft(), pair.getRight());
            }
        } while (!pairs.isEmpty());
    }

}
