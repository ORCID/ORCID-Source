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
package orcid.pojo.ajaxForm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Test;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.pojo.ajaxForm.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkTest extends XMLTestCase {
    private Unmarshaller unmarshaller;

    ArrayList<Properties> pList = new ArrayList<>();

    private static final Logger LOG = LoggerFactory.getLogger(WorkTest.class);

    public WorkTest() throws JAXBException {
        super("Work Ajax Form");
        JAXBContext context = JAXBContext.newInstance(OrcidMessage.class);
        unmarshaller = context.createUnmarshaller();
    }

    @Test
    public void testValueOfAndBack() throws Exception {
        
        // check the empty work case
        OrcidWork ow = new OrcidWork();
        Work work = Work.valueOf(ow);
        OrcidWork ow2 = work.toOrcidWork();
        assertEquals(ow.toString(), ow2.toString());
        
        // loop through all the works in orcid-protected-full-message-latest.xml
        OrcidMessage om = getOrcidMessage("/orcid-protected-full-message-latest.xml");
        List<OrcidWork> owList = om.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork();
        for (OrcidWork curOw: owList) {
            Work curWork = Work.valueOf(curOw);
            OrcidWork curOw2 = curWork.toOrcidWork();  
            assertEquals(curOw.toString(), curOw2.toString());           
        }
        
    }

    private OrcidMessage getOrcidMessage(String s) throws JAXBException {
        InputStream inputStream = WorkTest.class.getResourceAsStream(s);
        return (OrcidMessage) unmarshaller.unmarshal(inputStream);
    }

}
