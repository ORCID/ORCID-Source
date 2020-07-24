package org.orcid.core.orgs.load.manager.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.orcid.core.orgs.load.manager.OrgLoadManager;
import org.orcid.core.orgs.load.source.OrgLoadSource;
import org.orcid.persistence.dao.OrgImportLogDao;
import org.orcid.persistence.jpa.entities.OrgImportLogEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrgLoadManagerImpl implements OrgLoadManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OrgLoadManagerImpl.class);
    
    @Resource
    private OrgImportLogDao orgImportLogDao;
    
    @Resource
    private List<OrgLoadSource> orgLoadSources;
    
    @Value("${org.orcid.core.orgs.loadBaseDir}")
    private String loadBaseDir;
    
    @Override
    public void loadOrgs() {
        checkBaseDir();
        OrgLoadSource loader = getNextOrgLoader();
        OrgImportLogEntity importLog = getOrgImportLogEntity(loader);
        boolean success = loader.loadLatestOrgs();
        logImport(importLog, success);
    }

    private void logImport(OrgImportLogEntity importLog, boolean success) {
        importLog.setEnd(new Date());
        importLog.setSuccessful(success);
        orgImportLogDao.persist(importLog);
    }

    private void checkBaseDir() {
        File baseDir = new File(loadBaseDir);
        if (!baseDir.exists()) {
            LOGGER.info("Creating org import base directory {}", loadBaseDir);
            baseDir.mkdirs();
        }
    }

    private OrgImportLogEntity getOrgImportLogEntity(OrgLoadSource loader) {
        OrgImportLogEntity log = new OrgImportLogEntity();
        log.setSource(loader.getSourceName());
        log.setStart(new Date());
        return log;
    }

    private OrgLoadSource getNextOrgLoader() {
        String nextImportSourceName = orgImportLogDao.getNextImportSourceName();
        Optional<OrgLoadSource> nextOrgLoader = orgLoadSources.stream().filter(l -> nextImportSourceName.equals(l.getSourceName())).findAny();
        if (!nextOrgLoader.isPresent()) {
            LOGGER.error("No org loader found for source name {}!", nextImportSourceName);
            throw new RuntimeException("No org loader found");
        }
        return nextOrgLoader.get();
    }

}
