package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.persistence.constants.OrganizationStatus;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedSolrDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.MemberChosenOrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.pojo.OrgDisambiguated;
import org.orcid.pojo.OrgDisambiguatedExternalIdentifiers;
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
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
    private OrgDisambiguatedDao orgDisambiguatedDaoReadOnly;
    
    @Resource
    private OrgDisambiguatedSolrDao orgDisambiguatedSolrDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    synchronized public void processOrgsForIndexing() {
        LOGGER.info("About to process disambiguated orgs for indexing");
        List<OrgDisambiguatedEntity> entities = null;
        do {
            entities = orgDisambiguatedDaoReadOnly.findOrgsPendingIndexing(0, INDEXING_CHUNK_SIZE);
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
                processDisambiguatedOrg(orgDisambiguatedDaoReadOnly.find(entity.getId()));
            }
        });
    }

    private void processDisambiguatedOrg(OrgDisambiguatedEntity entity) {
        LOGGER.info("About to index disambiguated org, id={}", entity.getId());
        OrgDisambiguatedSolrDocument document = convertEntityToDocument(entity);
        if(OrganizationStatus.DEPRECATED.name().equals(entity.getStatus()) || OrganizationStatus.OBSOLETE.name().equals(entity.getStatus())) {
            orgDisambiguatedSolrDao.remove(document.getOrgDisambiguatedId());            
        } else {
            orgDisambiguatedSolrDao.persist(document);
        }
        
        orgDisambiguatedDao.updateIndexingStatus(entity.getId(), IndexingStatus.DONE);
    }

    private OrgDisambiguatedSolrDocument convertEntityToDocument(OrgDisambiguatedEntity entity) {
        OrgDisambiguatedSolrDocument document = new OrgDisambiguatedSolrDocument();
        document.setOrgDisambiguatedId(entity.getId());
        document.setOrgDisambiguatedName(entity.getName());
        document.setOrgDisambiguatedCity(entity.getCity());
        document.setOrgDisambiguatedRegion(entity.getRegion());
        if (entity.getCountry() != null)
            document.setOrgDisambiguatedCountry(entity.getCountry());
        document.setOrgDisambiguatedIdFromSource(entity.getSourceId());
        document.setOrgDisambiguatedIdSourceType(entity.getSourceType());
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

        if (OrgDisambiguatedSourceType.FUNDREF.name().equals(entity.getSourceType())) {
            document.setFundingOrg(true);
        } else {
            document.setFundingOrg(false);
        }

        return document;
    }

    @Override
    synchronized public void processOrgsWithIncorrectPopularity() {
        LOGGER.info("About to process disambiguated orgs with incorrect popularity");
        List<Pair<Long, Integer>> pairs = null;
        do {
            pairs = orgDisambiguatedDaoReadOnly.findDisambuguatedOrgsWithIncorrectPopularity(INCORRECT_POPULARITY_CHUNK_SIZE);
            LOGGER.info("Found chunk of {} disambiguated orgs with incorrect popularity", pairs.size());
            for (Pair<Long, Integer> pair : pairs) {
                LOGGER.info("About to update popularity of disambiguated org: {}", pair);
                orgDisambiguatedDao.updatePopularity(pair.getLeft(), pair.getRight());
            }
        } while (!pairs.isEmpty());
    }

    @Override
    public List<OrgDisambiguated> searchOrgsFromSolr(String searchTerm, int firstResult, int maxResult, boolean fundersOnly) {
        List<OrgDisambiguatedSolrDocument> docs = orgDisambiguatedSolrDao.getOrgs(searchTerm, firstResult, maxResult, fundersOnly);
        List<OrgDisambiguated> ret = new ArrayList<OrgDisambiguated>();
        for (OrgDisambiguatedSolrDocument doc: docs){
            OrgDisambiguated org = convertSolrDocument(doc);
            ret.add(org);            
        }
        return ret;
    }
    
    @Override
    public List<OrgDisambiguated> searchOrgsFromSolrForSelfService(String searchTerm, int firstResult, int maxResult) {
        List<OrgDisambiguatedSolrDocument> docs = orgDisambiguatedSolrDao.getOrgsForSelfService(searchTerm, firstResult, maxResult);
        List<OrgDisambiguated> ret = new ArrayList<OrgDisambiguated>();
        for (OrgDisambiguatedSolrDocument doc: docs){
            OrgDisambiguated org = convertSolrDocument(doc);
            ret.add(org);            
        }
        return ret;
    }

    private OrgDisambiguated convertSolrDocument(OrgDisambiguatedSolrDocument doc) {
        OrgDisambiguated org = new OrgDisambiguated();
        org.setValue(doc.getOrgDisambiguatedName());
        org.setCity(doc.getOrgDisambiguatedCity());
        org.setRegion(doc.getOrgDisambiguatedRegion());
        org.setCountry(doc.getOrgDisambiguatedCountry());
        org.setOrgType(doc.getOrgDisambiguatedType());
        org.setDisambiguatedAffiliationIdentifier(Long.toString(doc.getOrgDisambiguatedId()));
        org.setSourceType(doc.getOrgDisambiguatedIdSourceType());
        org.setSourceId(doc.getOrgDisambiguatedIdFromSource());
        return org;
    }

    @Override
    @Transactional
    public OrgDisambiguated findInDB(Long id) {
        OrgDisambiguatedEntity orgDisambiguatedEntity = orgDisambiguatedDaoReadOnly.find(id);
        OrgDisambiguated org = convertEntity(orgDisambiguatedEntity);
        return org;
    }

    @Override
    @Transactional
    public OrgDisambiguated findInDB(String idValue, String idType) {
        OrgDisambiguatedEntity orgDisambiguatedEntity = orgDisambiguatedDaoReadOnly.findBySourceIdAndSourceType(idValue, idType);
        if (orgDisambiguatedEntity != null)
            return convertEntity(orgDisambiguatedEntity);
        return null;
    }

    private OrgDisambiguated convertEntity(OrgDisambiguatedEntity orgDisambiguatedEntity) {
        OrgDisambiguated org = new OrgDisambiguated();
        org.setValue(orgDisambiguatedEntity.getName());
        org.setCity(orgDisambiguatedEntity.getCity());
        org.setRegion(orgDisambiguatedEntity.getRegion());        
        org.setCountry(orgDisambiguatedEntity.getCountry() != null ? orgDisambiguatedEntity.getCountry() : null);
        org.setOrgType(orgDisambiguatedEntity.getOrgType());
        org.setSourceId(orgDisambiguatedEntity.getSourceId());
        org.setSourceType(orgDisambiguatedEntity.getSourceType()); 
        org.setUrl(orgDisambiguatedEntity.getUrl());
        Map<String, OrgDisambiguatedExternalIdentifiers> externalIdsMap = new HashMap<String, OrgDisambiguatedExternalIdentifiers>();
        if (orgDisambiguatedEntity.getExternalIdentifiers() != null && !orgDisambiguatedEntity.getExternalIdentifiers().isEmpty()) {
            for (OrgDisambiguatedExternalIdentifierEntity extIdEntity : orgDisambiguatedEntity.getExternalIdentifiers()) {
                String type = extIdEntity.getIdentifierType();
                String identifier = extIdEntity.getIdentifier();
                Boolean preferred = extIdEntity.getPreferred();

                OrgDisambiguatedExternalIdentifiers extId = null;

                if (externalIdsMap.containsKey(type)) {
                    extId = externalIdsMap.get(type);
                } else {
                    extId = new OrgDisambiguatedExternalIdentifiers();
                    extId.setIdentifierType(type);
                    externalIdsMap.put(type, extId);
                }

                if (preferred) {
                    extId.setPreferred(identifier);
                }

                extId.getAll().add(identifier);
            }

            if (!externalIdsMap.isEmpty()) {
                List<OrgDisambiguatedExternalIdentifiers> extIds = new ArrayList<OrgDisambiguatedExternalIdentifiers>();
                externalIdsMap.keySet().stream().sorted().collect(Collectors.toList()).forEach(k -> {extIds.add(externalIdsMap.get(k));});                
                org.setOrgDisambiguatedExternalIdentifiers(extIds);
            }

        }
        return org;
    }

    @Override
    public void refreshMemberChosenOrgs(List<Long> chosenIds) {
        List<MemberChosenOrgDisambiguatedEntity> entities = new ArrayList<>();
        chosenIds.stream().forEach(id -> entities.add(new MemberChosenOrgDisambiguatedEntity(id)));
        orgDisambiguatedDao.clearMemberChosenOrgs();
        entities.stream().forEach(e -> orgDisambiguatedDao.persistChosenOrg(e));
    }
}
