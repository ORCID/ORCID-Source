package org.orcid.core.manager.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.FundingSubTypeToIndexDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;

public class ProfileFundingManagerImpl implements ProfileFundingManager {
    
    @Resource
    private ProfileFundingDao profileFundingDao;
    
    @Resource 
    private FundingSubTypeToIndexDao fundingSubTypeToIndexDao;
    
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
     * A process that will process all funding subtypes, filter and index them. 
     * */
    public void indexFundingSubTypes() {
        List<String> subtypes = fundingSubTypeToIndexDao.getSubTypes();
        List<String> wordsToFilter = new ArrayList<String>();
        try {
            wordsToFilter = IOUtils.readLines(getClass().getResourceAsStream("words_to_filter.txt"));
        } catch (IOException e) {
            throw new RuntimeException("Problem reading countries.txt from classpath", e);
        }
        for(String subtype : subtypes) {            
            subtype = subtype.toLowerCase();
            boolean isInappropriate = false;
            //All filter words are in lower case, so, lowercase the subtype before comparing
            for(String wordToFilter : wordsToFilter) {
                if(wordToFilter.matches(".*\\b" + subtype + "\\b.*")) {
                    isInappropriate = true;
                    break;
                }
                    
            }
            if(!isInappropriate){
                System.out.println(subtype + " will be sent to solr");                
            } else {
                System.out.println(subtype + " is filtered");
            }           
            fundingSubTypeToIndexDao.removeSubTypes(subtype);
        }        
    }
}
