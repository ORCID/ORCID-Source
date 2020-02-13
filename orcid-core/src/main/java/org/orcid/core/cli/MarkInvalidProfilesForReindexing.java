package org.orcid.core.cli;

import java.util.ArrayList;
import java.util.List;

import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MarkInvalidProfilesForReindexing {
    
    private static final Logger LOG = LoggerFactory.getLogger(MarkInvalidProfilesForReindexing.class);
   
    private static final int BATCH_SIZE = 300;
    
    private ProfileDao profileDao;

    public static void main(String[] args) {
        MarkInvalidProfilesForReindexing m = new MarkInvalidProfilesForReindexing();
        m.init();
        m.markProfilesForReindexing();
    }

    private void markProfilesForReindexing() {
        List<String> allIds = profileDao.getAllOrcidIdsForInvalidRecords();
        LOG.info("Found {} profiles for reindexing", allIds.size());
        
        List<String> toCorrect = getNextIdSubset(allIds);
        while (toCorrect != null && !toCorrect.isEmpty()) {
            profileDao.updateIndexingStatus(toCorrect, IndexingStatus.REINDEX);
            LOG.info("Updated {} profiles, {} remaining to update", new Object[] { toCorrect.size(), allIds.size() });
            toCorrect = getNextIdSubset(allIds);
        }
        LOG.info("Profiles updated");
        System.exit(0);
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        profileDao  = (ProfileDao) context.getBean("profileDao");
    }

    private List<String> getNextIdSubset(List<String> ids) {
        List<String> subset = new ArrayList<>();
        for (int i = 0; i < BATCH_SIZE && !ids.isEmpty(); i++) {
            subset.add(ids.remove(0));
        }
        return subset;
    }
    
}
