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
package org.orcid.core.reporting;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Clock;
import com.yammer.metrics.core.MetricPredicate;
import com.yammer.metrics.core.VirtualMachineMetrics;
import com.yammer.metrics.reporting.AbstractPollingReporter;
import com.yammer.metrics.reporting.ConsoleReporter;
import com.yammer.metrics.reporting.GraphiteReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 2011-2012 - ORCID
 *
 * @author Declan Newman (declan)
 *         Date: 17/07/2012
 */
public class ReporterInitializer implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReporterInitializer.class);

    private AbstractPollingReporter reporter;

    private final long period;
    private final TimeUnit unit;
    private final String host;
    private final int port;
    private final String prefix;

    public ReporterInitializer(long period, TimeUnit unit, String host, int port, String prefix, boolean useGraphite) throws IOException {
        this.period = period;
        this.unit = unit;
        this.host = host;
        this.port = port;
        this.prefix = prefix;
        if (useGraphite) {
            LOGGER.info("Starting Graphite reporter");
            reporter = new GraphiteReporter(Metrics.defaultRegistry(), prefix, MetricPredicate.ALL,
                    new GraphiteReporter.DefaultSocketProvider(host, port), Clock.defaultClock(), VirtualMachineMetrics.getInstance());
        } else {
            LOGGER.info("Starting Console reporter");
            reporter = new ConsoleReporter(Metrics.defaultRegistry(), System.out, MetricPredicate.ALL);
        }
    }

    public void shutdown() {
        if (reporter != null) {
            LOGGER.info("Shutting down the {} reporter", reporter);
            reporter.shutdown();
        }
    }

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied
     * (and satisfied BeanFactoryAware and ApplicationContextAware).
     * <p>This method allows the bean instance to perform initialization only
     * possible when all bean properties have been set and to throw an
     * exception in the event of misconfiguration.
     *
     * @throws Exception
     *         in the event of misconfiguration (such
     *         as failure to set an essential property) or if initialization fails.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        reporter.start(period, unit);
        LOGGER.info("{} reporter started", reporter);
    }
}
