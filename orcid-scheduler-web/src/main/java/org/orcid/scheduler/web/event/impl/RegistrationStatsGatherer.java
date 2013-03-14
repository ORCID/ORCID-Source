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
package org.orcid.scheduler.web.event.impl;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.reporting.ConsoleReporter;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.scheduler.web.event.EventStatsGatherer;

import javax.annotation.Resource;

/**
 * 2011-2012 - ORCID
 *
 * @author Declan Newman (declan)
 *         Date: 17/07/2012
 */
public class RegistrationStatsGatherer implements EventStatsGatherer {

    private final Histogram registrationCompletedCount = Metrics.newHistogram(RegistrationStatsGatherer.class, "registration-completed-count");
    private final Histogram registrationUnclaimedCount = Metrics.newHistogram(RegistrationStatsGatherer.class, "registration-unclaimed-count");

    @Resource
    private RegistrationManager registrationManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Override
    public void gather() {
        Long registrationCount = registrationManager.getCount();
        Long confirmedProfileCount = profileEntityManager.getConfirmedProfileCount();
        if (registrationCount != null && confirmedProfileCount != null) {
            registrationCompletedCount.update(confirmedProfileCount);
            registrationUnclaimedCount.update(registrationCount - confirmedProfileCount);
        }
    }
}
