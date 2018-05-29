package org.orcid.core.cli;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.orcid.core.manager.EncryptionManager;
import org.orcid.persistence.dao.ProfileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HashOrcidIds {

    private static Logger LOG = LoggerFactory.getLogger(HashOrcidIds.class);

    private static final int BUFFER_SIZE = 500;

    private ProfileDao profileDao;

    private EncryptionManager encryptionManager;

    private int doneCount = 0;

    public static void main(String[] args) throws IOException {
        HashOrcidIds hashOrcidIds = new HashOrcidIds();
        hashOrcidIds.init();
        hashOrcidIds.hashOrcidIds();
        System.exit(0);
    }

    private void hashOrcidIds() {
        List<String> orcids = profileDao.getProfilesWithNoHashedOrcid(BUFFER_SIZE);
        while (!orcids.isEmpty()) {
            for (String orcid : orcids) {
                try {
                    String hash = encryptionManager.sha256Hash(orcid);
                    profileDao.hashOrcidIds(orcid, hash);
                    doneCount++;
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
            LOG.info("Current processed count: " + doneCount);
            orcids = profileDao.getProfilesWithNoHashedOrcid(BUFFER_SIZE);
        }
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        profileDao = (ProfileDao) context.getBean("profileDao");
        encryptionManager = (EncryptionManager) context.getBean("encryptionManager");
    }

}
