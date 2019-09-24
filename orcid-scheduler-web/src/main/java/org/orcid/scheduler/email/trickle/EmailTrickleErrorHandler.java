package org.orcid.scheduler.email.trickle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

public class EmailTrickleErrorHandler implements ErrorHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailTrickleErrorHandler.class);

    @Override
    public void handleError(Throwable t) {
        if (t.getCause() instanceof TrickleTooHeavyException) {
            LOGGER.info("No email schedule allowing sending of message");
        } else {
            LOGGER.warn("Unexpected email trickle error", t);
        }
    }

}
