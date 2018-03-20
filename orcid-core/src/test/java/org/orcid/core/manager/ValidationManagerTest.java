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
