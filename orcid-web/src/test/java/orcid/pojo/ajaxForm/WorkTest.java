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
package orcid.pojo.ajaxForm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.sf.ehcache.util.MemoryEfficientByteArrayOutputStream;

import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Test;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.pojo.ajaxForm.Work;

public class WorkTest extends XMLTestCase {
    private Unmarshaller unmarshaller;

    ArrayList<Properties> pList = new ArrayList<>();

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
        // Work will add WorkExternalIdentifiers
        assertFalse(ow.toString().equals(ow2.toString()));
        // Run the test again but with empty WorkExternalIdentifiers added
        ow.setWorkExternalIdentifiers(new WorkExternalIdentifiers());
        work = Work.valueOf(ow);
        ow2 = work.toOrcidWork();
        assertEquals(ow.toString(),ow2.toString());
        
        
        
        // loop through all the works in orcid-protected-full-message-latest.xml
        OrcidMessage om = getOrcidMessage("/orcid-protected-full-message-latest.xml");
        List<OrcidWork> owList = om.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork();
        for (OrcidWork curOw: owList) {
            // add in blank WorkExternalIdentifiers if it doesn't exist
            // since Work will add it in to allow the user to add
            // an identifier
            if (curOw.getWorkExternalIdentifiers() == null)
                curOw.setWorkExternalIdentifiers(new WorkExternalIdentifiers());
            Work curWork = Work.valueOf(curOw);
            OrcidWork curOw2 = curWork.toOrcidWork();  
            assertEquals(curOw.toString(), curOw2.toString());           
        }
        
    }
    
    @Test
    public void testSerializeWork() throws Exception {
        InputStream inputStream = WorkTest.class.getResourceAsStream("/orcid-work.xml");
        OrcidWork orcidWork = (OrcidWork) unmarshaller.unmarshal(inputStream);
        Work work =  Work.minimizedValueOf(orcidWork);
        MemoryEfficientByteArrayOutputStream.serialize(work);
    }

    private OrcidMessage getOrcidMessage(String s) throws JAXBException {
        InputStream inputStream = WorkTest.class.getResourceAsStream(s);
        return (OrcidMessage) unmarshaller.unmarshal(inputStream);
    }

}
