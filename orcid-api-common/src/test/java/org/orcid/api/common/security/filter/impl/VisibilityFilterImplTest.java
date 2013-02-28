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
package org.orcid.api.common.security.filter.impl;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Test;
import org.orcid.core.security.visibility.filter.impl.VisibilityFilterImpl;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.Visibility;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 16/03/2012
 */
public class VisibilityFilterImplTest extends XMLTestCase {

    private Unmarshaller unmarshaller;

    private VisibilityFilterImpl visibilityFilter = new VisibilityFilterImpl();

    private OrcidMessage protectedOrcidMessage;
    private OrcidMessage publicOrcidMessage;

    public VisibilityFilterImplTest() throws JAXBException {
        super("Test Visibility Filter");
        JAXBContext context = JAXBContext.newInstance(OrcidMessage.class);
        unmarshaller = context.createUnmarshaller();
    }

    @Test
    public void testFilterStripWithVisibilities() throws Exception {
        protectedOrcidMessage = getOrcidMessage("/orcid-protected-full-message-latest.xml");
        publicOrcidMessage = getOrcidMessage("/orcid-stripped-with-visibility-message-latest.xml");
        OrcidMessage orcidMessage = visibilityFilter.filter(protectedOrcidMessage, Visibility.PUBLIC);
        Diff myDiff = new Diff(publicOrcidMessage.toString(), orcidMessage.toString());
        assertEquals(publicOrcidMessage.toString(), orcidMessage.toString());
        assertTrue(myDiff.toString(), myDiff.similar());
    }

    @Test
    public void testFilterStripWithNoVisibilities() throws Exception {
        protectedOrcidMessage = getOrcidMessage("/orcid-protected-full-message-latest.xml");
        publicOrcidMessage = getOrcidMessage("/orcid-stripped-no-visibility-message-latest.xml");
        OrcidMessage orcidMessage = visibilityFilter.filter(protectedOrcidMessage, true, Visibility.PUBLIC);
        Diff myDiff = new Diff(publicOrcidMessage.toString(), orcidMessage.toString());
        assertEquals(publicOrcidMessage.toString(), orcidMessage.toString());
        assertTrue(myDiff.toString(), myDiff.similar());
    }

    private OrcidMessage getOrcidMessage(String s) throws JAXBException {
        InputStream inputStream = VisibilityFilterImplTest.class.getResourceAsStream(s);
        return (OrcidMessage) unmarshaller.unmarshal(inputStream);

    }
}
