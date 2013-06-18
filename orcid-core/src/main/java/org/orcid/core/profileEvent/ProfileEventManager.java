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
package org.orcid.core.profileEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author rcpeters
 * 
 */
public class ProfileEventManager {

    static ApplicationContext context;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private GenericDao<ProfileEventEntity, Long> profileEventDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    private static Logger LOG = LoggerFactory.getLogger(ProfileEventManager.class);

    private static final int CHUNK_SIZE = 1000;

    @Option(name = "-testSendToOrcids", usage = "Call only on passed ORCID Ids")
    private String orcs;

    @Option(name = "-callOnAll", usage = "Calls on all orcids")
    private String callOnAll;

    @Option(name = "-bean", usage = "ProfileEvent class to instantiate", required = true)
    private String bean;

    public static void main(String... args) {
        context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        ProfileEventManager pem = (ProfileEventManager) context.getBean("profileEventManager");

        CmdLineParser parser = new CmdLineParser(pem);
        if (args == null) {
            parser.printUsage(System.err);
        }
        try {
            parser.parseArgument(args);
            pem.execute(pem);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }

    }

    private void execute(ProfileEventManager pem) {
        if (callOnAll != null) {
            callOnceOnAll(bean);
        } else if (orcs != null) {
            for (String orc : orcs.split(" ")) {
                OrcidProfile orcidProfile = getOrcidProfileManager().retrieveOrcidProfile(orc);
                ProfileEvent pe = (ProfileEvent)context.getBean(bean,orcidProfile);
                try {
                    pe.call();
                } catch (Exception e) {
                    LOG.error("Error calling ", e);
                }
            }
        }
    }

    private void callOnceOnAll(final String classStr) {
        ProfileEvent dummyPe = (ProfileEvent)context.getBean(classStr, (ProfileEvent)null);
        long startTime = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        List<String> orcids = Collections.EMPTY_LIST;
        int doneCount = 0;
        do {
            orcids = getProfileDao().findByEventTypes(CHUNK_SIZE, dummyPe.outcomes(), null, true);
            for (final String orcid : orcids) {
                LOG.info("Calling bean "+ classStr + " for "+ orcid);
                call(orcid, classStr);
                doneCount++;
            }
            LOG.info("Sending crossref verify emails on number: {}", doneCount);
        } while (!orcids.isEmpty());
        long endTime = System.currentTimeMillis();
        String timeTaken = DurationFormatUtils.formatDurationHMS(endTime - startTime);
        LOG.info("Sending crossref verify emails: doneCount={}, timeTaken={} (H:m:s.S)", doneCount, timeTaken);
    }
    
    public void call(final String orcid, final String classStr) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                OrcidProfile orcidProfile = getOrcidProfileManager().retrieveOrcidProfile(orcid);
                try {
                    ProfileEvent pe = (ProfileEvent)context.getBean(classStr,orcidProfile);
                    getProfileEventDao().persist(new ProfileEventEntity(orcidProfile.getOrcidId(), pe.call()));
                } catch (Exception e) {
                    LOG.error("Error calling ", e);
                }
            }
        });
    }

    public ProfileDao getProfileDao() {
        return profileDao;
    }

    public void setProfileDao(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    public OrcidProfileManager getOrcidProfileManager() {
        return orcidProfileManager;
    }

    public void setOrcidProfileManager(OrcidProfileManager orcidProfileManager) {
        this.orcidProfileManager = orcidProfileManager;
    }

    public GenericDao<ProfileEventEntity, Long> getProfileEventDao() {
        return profileEventDao;
    }

    public void setProfileEventDao(GenericDao<ProfileEventEntity, Long> profileEventDao) {
        this.profileEventDao = profileEventDao;
    }

}
