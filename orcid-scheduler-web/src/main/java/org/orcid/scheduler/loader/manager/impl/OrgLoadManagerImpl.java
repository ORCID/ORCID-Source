package org.orcid.scheduler.loader.manager.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.orcid.persistence.dao.OrgImportLogDao;
import org.orcid.persistence.jpa.entities.OrgImportLogEntity;
import org.orcid.scheduler.loader.manager.OrgLoadManager;
import org.orcid.scheduler.loader.source.OrgLoadSource;
import org.orcid.utils.alerting.SlackManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("orgLoadManager")
public class OrgLoadManagerImpl implements OrgLoadManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrgLoadManagerImpl.class);

    @Resource
    private OrgImportLogDao orgImportLogDao;

    @Resource
    private List<OrgLoadSource> orgLoadSources;

    @Resource
    private SlackManager slackManager;

    @Value("${org.orcid.core.orgs.load.slackChannel}")
    private String slackChannel;

    @Value("${org.orcid.core.orgs.load.slackUser}")
    private String slackUser;

    @Override
    public void loadOrgs() {
        LOGGER.info("About to start the orgs loader process");
        OrgLoadSource loader = getNextOrgLoader();
        if (loader != null) {
            loadOrg(loader);
        }
    }
    
    @Override
    public void loadOrg(OrgLoadSource loader) {
        LOGGER.info("About to start loading orgs from source {}", loader.getSourceName());
        if (loader != null) {
            OrgImportLogEntity importLog = getOrgImportLogEntity(loader);
            boolean success = loader.downloadOrgData();
            if (success) {
                success = loader.loadOrgData();
            }
            logImport(importLog, success);

            if (success) {
                LOGGER.info("Orgs successfully imported from {}", loader.getSourceName());
                slackManager.sendAlert(String.format("Orgs successfully imported from %s", loader.getSourceName()), slackChannel, slackUser);
            } else {
                LOGGER.warn("Org import FAILURE from {}", loader.getSourceName());
                slackManager.sendAlert(String.format("Org import FAILURE from %s", loader.getSourceName()), slackChannel, slackUser);
            }
        } else {
            slackManager.sendAlert(String.format("No org loader enabled, orgs will not be imported"), slackChannel, slackUser);
        }
    }

    private void logImport(OrgImportLogEntity importLog, boolean success) {
        importLog.setEnd(new Date());
        importLog.setSuccessful(success);
        orgImportLogDao.persist(importLog);
    }

    private OrgImportLogEntity getOrgImportLogEntity(OrgLoadSource loader) {
        OrgImportLogEntity log = new OrgImportLogEntity();
        log.setSource(loader.getSourceName());
        log.setStart(new Date());
        return log;
    }

    private OrgLoadSource getNextOrgLoader() {
        List<String> nextImportSourceNames = orgImportLogDao.getImportSourceOrder();
        addOrgLoadSourcesNeverRun(nextImportSourceNames);
        
        for (String name : nextImportSourceNames) {
            Optional<OrgLoadSource> nextOrgLoader = orgLoadSources.stream().filter(l -> name.equals(l.getSourceName()) && l.isEnabled()).findAny();
            if (!nextOrgLoader.isPresent()) {
                LOGGER.warn("No enabled org loader found for source name {}", name);
            } else {
                return nextOrgLoader.get();
            }
        }

        LOGGER.info("No enabled org import source found");
        return null;
    }

    private void addOrgLoadSourcesNeverRun(List<String> nextImportSourceNames) {
        orgLoadSources.forEach(s -> {
            if (!nextImportSourceNames.contains(s.getSourceName())) {
                nextImportSourceNames.add(0, s.getSourceName());
            }
        });
    }
}
