package org.orcid.scheduler.loader.manager.impl;

import javax.annotation.Resource;

import org.orcid.scheduler.loader.manager.IssnLoadManager;
import org.orcid.scheduler.loader.source.issn.IssnLoadSource;
import org.orcid.utils.alerting.SlackManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("issnLoadManager")
public class IssnLoadManagerImpl implements IssnLoadManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssnLoadManagerImpl.class);
    @Resource
    private IssnLoadSource  issnLoadSource;

    @Resource
    private SlackManager slackManager;

    @Value("${org.orcid.core.orgs.load.slackChannel}")
    private String slackChannel;

    @Value("${org.orcid.core.orgs.load.slackUser}")
    private String slackUser;
    
    @Value("${org.orcid.core.issn.source}")
    private String issnSource;

    
    @Override
    public void loadIssn() {
        try {
            LOGGER.info("Load ISSN  for client : " + issnSource);
            issnLoadSource.loadIssn(issnSource);
            slackManager.sendAlert("Issn  succesfully updated for client " + issnSource, slackChannel, slackUser);
        } catch (Exception ex) {
            LOGGER.error("Error when running ISSN for client" + issnSource, ex);
            slackManager.sendAlert("Error when running ISSN for client " + issnSource, slackChannel, slackUser);

        }
    }
}
