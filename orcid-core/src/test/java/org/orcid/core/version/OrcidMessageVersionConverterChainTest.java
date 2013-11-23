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
package org.orcid.core.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStreamReader;
import java.io.Reader;

import javax.annotation.Resource;

import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.message.OrcidMessage;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidMessageVersionConverterChainTest extends BaseTest {

    @Resource
    private OrcidMessageVersionConverterChain orcidMessageVersionConverterChain;

    @Test
    public void testUpdgrade() {
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("orcid-public-full-message-v1.0.16.xml"));
        OrcidMessage oldMessage = OrcidMessage.unmarshall(reader);
        OrcidMessage newMessage = orcidMessageVersionConverterChain.upgradeMessage(oldMessage, "1.0.17");
        assertNotNull(newMessage);
        assertEquals("1.0.17", newMessage.getMessageVersion());
        assertEquals("4444-4444-4444-4446", newMessage.getOrcidProfile().getOrcid().getValue());
        assertEquals("http://orcid.org/4444-4444-4444-4446", newMessage.getOrcidProfile().getOrcidId());
    }

}
