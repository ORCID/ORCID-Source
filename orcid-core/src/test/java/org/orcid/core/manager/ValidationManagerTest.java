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
package org.orcid.core.manager;

import javax.annotation.Resource;

import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.jaxb.model.message.OrcidMessage;

public class ValidationManagerTest extends BaseTest {

    @Resource(name = "incomingValidationManagerLatest")
    private ValidationManager incomingValidationManager;

    @Resource(name = "outgoingValidationManagerLatest")
    private ValidationManager outgoingValidationManager;

    @Test(expected = OrcidValidationException.class)
    public void testEmptyMessage() {
        OrcidMessage message = new OrcidMessage();
        incomingValidationManager.validateMessage(message);
    }

    @Test
    public void testEmptyMessageWhenLoggingOnly() {
        OrcidMessage message = new OrcidMessage();
        outgoingValidationManager.validateMessage(message);
    }

}
