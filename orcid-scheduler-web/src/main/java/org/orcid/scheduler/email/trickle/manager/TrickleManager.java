package org.orcid.scheduler.email.trickle.manager;

import org.orcid.core.email.trickle.producer.EmailTrickleItem;
import org.orcid.core.manager.v3.EmailMessage;

public interface TrickleManager {
    
    void attemptSend(EmailTrickleItem item);

}
