/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.integration.listener;

import java.util.Map;

import org.orcid.utils.listener.LastModifiedMessage;
import org.orcid.utils.listener.MessageConstants;
import org.springframework.jms.annotation.JmsListener;

// use @Component or add as a bean in the XML config.
public class EchoTestMessageListener {

    public static LastModifiedMessage message = null;
    
    @JmsListener(destination=MessageConstants.Queues.TEST)
    public void processMessage(final Map<String, String> map) {
      message = new LastModifiedMessage(map);
    }
}
