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

import org.junit.Before;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.message.OrcidMessage;

public class ValidationManagerTest extends BaseTest {

    @Resource
    ValidationManager validationManager;

    @Before
    public void before() {
        validationManager.setValidationBehaviour(ValidationBehaviour.THROW_RUNTIME_EXCEPTION);
    }

    @Test(expected = RuntimeException.class)
    public void testEmptyMessage() {
        OrcidMessage message = new OrcidMessage();
        validationManager.validateMessage(message);
    }

    @Test
    public void testEmptyMessageWhenLoggingOnly() {
        validationManager.setValidationBehaviour(ValidationBehaviour.LOG_WARNING);
        OrcidMessage message = new OrcidMessage();
        validationManager.validateMessage(message);
    }

}
