package org.orcid.scheduler.email.trickle.manager;

import org.orcid.core.email.trickle.producer.EmailTrickleItem;

public interface TrickleManager {
    
    void attemptSend(EmailTrickleItem item);

}
