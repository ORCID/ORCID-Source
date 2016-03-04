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
package com.orcid.api.common.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.common.util.ElementUtils;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Email;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.Person;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class ElementUtilsTest {

    private Unmarshaller unmarshaller;
    
    @Before
    public void before() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Person.class);        
        unmarshaller = context.createUnmarshaller();
    }
    
    @Test
    public void cleanLastModifiedElementTest() throws JAXBException {
        //Test with empty lists
        Person person = getPerson("/person-full-message_V2-0_rc2.xml");
        person.getAddresses().setAddress(new ArrayList<Address>());
        person.getEmails().setEmails(new ArrayList<Email>());
        person.getExternalIdentifiers().setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>());
        person.getKeywords().setKeywords(new ArrayList<Keyword>());
        person.getOtherNames().setOtherNames(new ArrayList<OtherName>());
        person.getResearcherUrls().setResearcherUrls(new ArrayList<ResearcherUrl>());
        
        assertNotNull(person.getAddresses().getLastModifiedDate());
        assertNotNull(person.getEmails().getLastModifiedDate());
        assertNotNull(person.getExternalIdentifiers().getLastModifiedDate());
        assertNotNull(person.getKeywords().getLastModifiedDate());
        assertNotNull(person.getOtherNames().getLastModifiedDate());
        assertNotNull(person.getResearcherUrls().getLastModifiedDate());
        
        ElementUtils.cleanLastModifiedElement(person);
        
        assertNull(person.getAddresses().getLastModifiedDate());
        assertNull(person.getEmails().getLastModifiedDate());
        assertNull(person.getExternalIdentifiers().getLastModifiedDate());
        assertNull(person.getKeywords().getLastModifiedDate());
        assertNull(person.getOtherNames().getLastModifiedDate());
        assertNull(person.getResearcherUrls().getLastModifiedDate());
        
        //Test with empty lists        
        person = getPerson("/person-full-message_V2-0_rc2.xml");
                
        person.getAddresses().setAddress(null);
        person.getEmails().setEmails(null);
        person.getExternalIdentifiers().setExternalIdentifiers(null);
        person.getKeywords().setKeywords(null);
        person.getOtherNames().setOtherNames(null);
        person.getResearcherUrls().setResearcherUrls(null);
        
        assertNotNull(person.getAddresses().getLastModifiedDate());
        assertNotNull(person.getEmails().getLastModifiedDate());
        assertNotNull(person.getExternalIdentifiers().getLastModifiedDate());
        assertNotNull(person.getKeywords().getLastModifiedDate());
        assertNotNull(person.getOtherNames().getLastModifiedDate());
        assertNotNull(person.getResearcherUrls().getLastModifiedDate());
        
        ElementUtils.cleanLastModifiedElement(person);
        
        assertNull(person.getAddresses().getLastModifiedDate());
        assertNull(person.getEmails().getLastModifiedDate());
        assertNull(person.getExternalIdentifiers().getLastModifiedDate());
        assertNull(person.getKeywords().getLastModifiedDate());
        assertNull(person.getOtherNames().getLastModifiedDate());
        assertNull(person.getResearcherUrls().getLastModifiedDate());        
    }
    
    
    private Person getPerson(String path) throws JAXBException {
        InputStream stream = getClass().getResourceAsStream(path);
        return (Person) unmarshaller.unmarshal(stream);
    }
}
