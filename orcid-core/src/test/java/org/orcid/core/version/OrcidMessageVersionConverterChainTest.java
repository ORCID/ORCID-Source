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
        OrcidMessage newMessage = orcidMessageVersionConverterChain.upgradeMessage(oldMessage, "1.2_rc7");
        assertNotNull(newMessage);
        assertEquals("1.2_rc7", newMessage.getMessageVersion());
        oldMessage = newMessage;
        newMessage = orcidMessageVersionConverterChain.upgradeMessage(oldMessage, "1.2");
        assertNotNull(newMessage);
        assertEquals("1.2", newMessage.getMessageVersion());
        assertEquals("4444-4444-4444-4446", newMessage.getOrcidProfile().getOrcid().getValue());
        assertEquals("http://orcid.org/4444-4444-4444-4446", newMessage.getOrcidProfile().getOrcidId());
    }

    @Test
    public void testDowngrade() {
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("orcid-public-full-message-v1.0.16.xml"));
        OrcidMessage oldMessage = OrcidMessage.unmarshall(reader);
        OrcidMessage newMessage = orcidMessageVersionConverterChain.upgradeMessage(oldMessage, "1.2");
        assertNotNull(newMessage);
        assertEquals("1.2", newMessage.getMessageVersion());
        oldMessage = newMessage;
        newMessage = orcidMessageVersionConverterChain.downgradeMessage(oldMessage, "1.2_rc7");
        assertNotNull(newMessage);
        assertEquals("1.2_rc7", newMessage.getMessageVersion());
        oldMessage = newMessage;
        newMessage = orcidMessageVersionConverterChain.downgradeMessage(oldMessage, "1.2_rc6");
        assertNotNull(newMessage);
        assertEquals("1.2_rc6", newMessage.getMessageVersion());
        oldMessage = newMessage;
        newMessage = orcidMessageVersionConverterChain.downgradeMessage(oldMessage, "1.1");
        assertNotNull(newMessage);
        assertEquals("1.1", newMessage.getMessageVersion());
        assertEquals("4444-4444-4444-4446", newMessage.getOrcidProfile().getOrcid().getValue());
        assertEquals("http://orcid.org/4444-4444-4444-4446", newMessage.getOrcidProfile().getOrcidId());
    }

    
}
