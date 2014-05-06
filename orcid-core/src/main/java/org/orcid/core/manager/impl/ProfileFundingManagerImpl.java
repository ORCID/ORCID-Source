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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.FundingSubTypeSolrDao;
import org.orcid.persistence.dao.FundingSubTypeToIndexDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.solr.entities.OrgDefinedFundingTypeSolrDocument;

public class ProfileFundingManagerImpl implements ProfileFundingManager {
    
    @Resource
    private ProfileFundingDao profileFundingDao;
    
    @Resource 
    private FundingSubTypeToIndexDao fundingSubTypeToIndexDao;
    
    @Resource
    private FundingSubTypeSolrDao fundingSubTypeSolrDao;
    
    /**
     * Removes the relationship that exists between a funding and a profile.
     * 
     * @param profileFundingId
     *            The id of the profileFunding that will be removed from the
     *            client profile
     * @param clientOrcid
     *            The client orcid
     * @return true if the relationship was deleted
     * */
    public boolean removeProfileFunding(String clientOrcid, String profileFundingId) {
        return profileFundingDao.removeProfileFunding(clientOrcid, profileFundingId);
    }

    /**
     * Updates the visibility of an existing profile funding relationship
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param profileFundingId
     *            The id of the profile funding that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile profileFunding object
     * 
     * @return true if the relationship was updated
     * */
    public boolean updateProfileFunding(String clientOrcid, String profileFundingId, Visibility visibility) {
        return profileFundingDao.updateProfileFunding(clientOrcid, profileFundingId, visibility);
    }

    /**
     * Creates a new profile funding relationship between an organization and a
     * profile.
     * 
     * @param newProfileFundingEntity
     *            The object to be persisted
     * @return the created newProfileFundingEntity with the id assigned on
     *         database
     * */
    public ProfileFundingEntity addProfileFunding(ProfileFundingEntity newProfileFundingEntity) {
        return profileFundingDao.addProfileFunding(newProfileFundingEntity);
    }
    
    /**
     * Add a new funding subtype to the list of pending for indexing subtypes
     * */
    public void addFundingSubType(String subtype, String orcid) {
        fundingSubTypeToIndexDao.addSubTypes(subtype, orcid);
    }
    
    /**
     * Looks for the org defined funding subtypes that matches a given pattern
     * @param subtype pattern to look for
     * @param limit the max number of results to look for
     * @return a list of all org defined funding subtypes that matches the given pattern
     * */
    public List<String> getIndexedFundingSubTypes(String subtype, int limit) {
        List<OrgDefinedFundingTypeSolrDocument> types = fundingSubTypeSolrDao.getFundingTypes(subtype, 0, 100); 
        List<String> result = new ArrayList<String>();
        for (OrgDefinedFundingTypeSolrDocument type : types) {
            result.add(type.getOrgDefinedFundingType());
        }
        return result; 
    }
    
    /**
     * A process that will process all funding subtypes, filter and index them. 
     * */
    public void indexFundingSubTypes() {
        List<String> subtypes = fundingSubTypeToIndexDao.getSubTypes();
        List<String> wordsToFilter = new ArrayList<String>();
        try {
            wordsToFilter = IOUtils.readLines(getClass().getResourceAsStream("words_to_filter.txt"));
        } catch (IOException e) {
            throw new RuntimeException("Problem reading words_to_filter.txt from classpath", e);
        }
        for(String subtype : subtypes) {       
            boolean isInappropriate = false;
            //All filter words are in lower case, so, lowercase the subtype before comparing
            for(String wordToFilter : wordsToFilter) {
                if(wordToFilter.matches(".*\\b" + subtype + "\\b.*")) {
                    isInappropriate = true;
                    break;
                }
            }
            if(!isInappropriate){
                OrgDefinedFundingTypeSolrDocument document = new OrgDefinedFundingTypeSolrDocument();
                document.setOrgDefinedFundingType(subtype);
                fundingSubTypeSolrDao.persist(document);              
            }           
            fundingSubTypeToIndexDao.removeSubTypes(subtype);
        }        
    }
}
