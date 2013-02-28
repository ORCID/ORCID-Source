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
package org.orcid.audit.concurrency;

import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;

/**
 * 2011-2012 - ORCID
 *
 * @author Declan Newman (declan)
 *         Date: 24/07/2012
 */
public class AuditEventConsumerInitialiser implements InitializingBean {

    @Resource
    private AuditEventConsumer auditEventConsumer;

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
        new Thread(auditEventConsumer).start();
    }

    public void shutdown() {
        auditEventConsumer.shutdownNow();
    }
}
