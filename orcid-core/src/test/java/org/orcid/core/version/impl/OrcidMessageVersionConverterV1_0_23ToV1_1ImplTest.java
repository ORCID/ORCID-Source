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
package org.orcid.core.version.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Test;
import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.jaxb.model.message.OrcidMessage;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidMessageVersionConverterV1_0_23ToV1_1ImplTest {

    @Test
    public void testUpgradeMessage() {
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("/org/orcid/core/version/orcid-public-full-message-v1.0.23.xml"));
        OrcidMessage oldMessage = OrcidMessage.unmarshall(reader);
        OrcidMessageVersionConverter converter = new OrcidMessageVersionConverterImplV1_0_23ToV1_1();
        OrcidMessage newMessage = converter.upgradeMessage(oldMessage);
        assertNotNull(newMessage);
        assertEquals("1.1", newMessage.getMessageVersion());
        assertEquals("4444-4444-4444-4446", newMessage.getOrcidProfile().getOrcidIdentifier().getPath());
        assertEquals("http://orcid.org/4444-4444-4444-4446", newMessage.getOrcidProfile().retrieveOrcidUriAsString());
    }

}
