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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.MailGunManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.sun.xml.bind.CycleRecoverable.Context;

/**
 * 
 * @author rcpeters
 * 
 */
public class ProfileEventManager {

    ApplicationContext context;

    private ProfileDao profileDao;

    private OrcidProfileManager orcidProfileManager;

    private GenericDao<ProfileEventEntity, Long> profileEventDao;

    private TransactionTemplate transactionTemplate;

    private static Logger LOG = LoggerFactory.getLogger(ProfileEventManager.class);

    private static final int CHUNK_SIZE = 1000;

    @Option(name = "-testSendToOrcids", usage = "Call only on passed ORCID Ids")
    private String orcs;

    @Option(name = "-callOnAll", usage = "Calls on all orcids")
    private String callOnAll;

    @Option(name = "-class", usage = "ProfileEvent class to instantiate", required = true)
    private String peClassStr;

    public static void main(String... args) {
        ProfileEventManager se = new ProfileEventManager();
        CmdLineParser parser = new CmdLineParser(se);
        if (args == null) {
            parser.printUsage(System.err);
        }
        try {
            parser.parseArgument(args);
            se.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }

    }

    private void execute() {
        init();
        if (callOnAll != null) {
            callOnAll();
        } else if (orcs != null) {
            for (String orc : orcs.split(" ")) {
                OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile(orc);
                CrossRefEmail cre = new CrossRefEmail(orcidProfile, context);
                try {
                    cre.call();
                } catch (Exception e) {
                    LOG.error("Error calling ", e);
                }
            }
        }
    }

    private void callOnAll() {
        ProfileEvent dummyPe = eventNewInstance(null);
        long startTime = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        List<String> orcids = Collections.EMPTY_LIST;
        int doneCount = 0;
        do {
            orcids = profileDao.findByEventTypes(CHUNK_SIZE, dummyPe.outcomes(), null, true);
            for (final String orcid : orcids) {
                LOG.info("Migrating emails for profile: {}", orcid);
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile(orcid);
                        try {
                            profileEventDao.persist(new ProfileEventEntity(orcid, eventNewInstance(orcidProfile).call()));
                        } catch (Exception e) {
                            LOG.error("Error calling ", e);
                        }
                    }
                });
                doneCount++;
            }
            LOG.info("Sending crossref verify emails on number: {}", doneCount);
        } while (!orcids.isEmpty());
        long endTime = System.currentTimeMillis();
        String timeTaken = DurationFormatUtils.formatDurationHMS(endTime - startTime);
        LOG.info("Sending crossref verify emails: doneCount={}, timeTaken={} (H:m:s.S)", doneCount, timeTaken);
    }

    private ProfileEvent eventNewInstance(OrcidProfile orcidProfile) {
        ProfileEvent pe = null;
        try {
            Class<?> peClass = Class.forName(peClassStr);
            Constructor<?> peConstructor = peClass.getDeclaredConstructor(OrcidProfile.class, ApplicationContext.class);
            pe = (ProfileEvent) peConstructor.newInstance(orcidProfile, context);
        } catch (InstantiationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pe;
    }

    private void init() {
        context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        profileDao = (ProfileDao) context.getBean("profileDao");
        profileEventDao = (GenericDao<ProfileEventEntity, Long>) context.getBean("profileEventDao");
        orcidProfileManager = (OrcidProfileManager) context.getBean("orcidProfileManager");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }

}
