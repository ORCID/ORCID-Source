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
    private SlackManager slackManager;

    @Value("${org.orcid.core.orgs.load.slackChannel}")
    private String slackChannel;

    @Value("${org.orcid.core.orgs.load.slackUser}")
    private String slackUser;

    
    @Override
    public void loadIssn() {
        try {
            IssnLoadSource loadIssn = new IssnLoadSource();
            loadIssn.loadIssn();
            slackManager.sendAlert("Issn date succesfully updated for client XXXX", slackChannel, slackUser);
    
        } catch (Exception ex) {
            slackManager.sendAlert("Error when running ISSN for client XXX ", slackChannel, slackUser);

        }
    }
}
