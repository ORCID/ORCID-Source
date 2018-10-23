package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.Address;
import org.orcid.jaxb.model.v3.rc2.record.Addresses;
import org.orcid.jaxb.model.v3.rc2.record.Biography;
import org.orcid.jaxb.model.v3.rc2.record.Email;
import org.orcid.jaxb.model.v3.rc2.record.Emails;
import org.orcid.jaxb.model.v3.rc2.record.Keyword;
import org.orcid.jaxb.model.v3.rc2.record.Keywords;
import org.orcid.jaxb.model.v3.rc2.record.Name;
import org.orcid.jaxb.model.v3.rc2.record.OtherName;
import org.orcid.jaxb.model.v3.rc2.record.OtherNames;
import org.orcid.jaxb.model.v3.rc2.record.Person;
import org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrls;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidSecurityManager_PersonTest extends OrcidSecurityManagerTestBase {

    @Test(expected = OrcidUnauthorizedException.class)
    public void testPerson_When_TokenForOtherUser() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PERSON_READ_LIMITED);
        Person p = new Person();
        orcidSecurityManager.checkAndFilter(ORCID_2, p);
        fail();
    }

    @Test
    public void testPerson_When_AllPublic_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.PUBLIC);
        Biography bio = createBiography(Visibility.PUBLIC);

        Address a1 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Address a2 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Address a3 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e2 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e3 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertEquals(name, p.getName());
        assertEquals(bio, p.getBiography());
        // Check addresses
        assertEquals(3, p.getAddresses().getAddress().size());
        assertTrue(p.getAddresses().getAddress().contains(a1));
        assertTrue(p.getAddresses().getAddress().contains(a2));
        assertTrue(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(3, p.getEmails().getEmails().size());
        assertTrue(p.getEmails().getEmails().contains(e1));
        assertTrue(p.getEmails().getEmails().contains(e2));
        assertTrue(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(3, p.getKeywords().getKeywords().size());
        assertTrue(p.getKeywords().getKeywords().contains(k1));
        assertTrue(p.getKeywords().getKeywords().contains(k2));
        assertTrue(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
    }

    @Test
    public void testPerson_When_SomeLimited_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.LIMITED);
        Biography bio = createBiography(Visibility.PUBLIC);

        Address a1 = createAddress(Visibility.LIMITED, CLIENT_2);
        Address a2 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Address a3 = createAddress(Visibility.LIMITED, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.LIMITED, CLIENT_2);
        Email e2 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e3 = createEmail(Visibility.LIMITED, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.LIMITED, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.LIMITED, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.LIMITED, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.LIMITED, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.LIMITED, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.LIMITED, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertNull(p.getName());
        assertEquals(bio, p.getBiography());
        // Check addresses
        assertEquals(1, p.getAddresses().getAddress().size());
        assertFalse(p.getAddresses().getAddress().contains(a1));
        assertTrue(p.getAddresses().getAddress().contains(a2));
        assertFalse(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(1, p.getEmails().getEmails().size());
        assertFalse(p.getEmails().getEmails().contains(e1));
        assertTrue(p.getEmails().getEmails().contains(e2));
        assertFalse(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(1, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(1, p.getKeywords().getKeywords().size());
        assertFalse(p.getKeywords().getKeywords().contains(k1));
        assertTrue(p.getKeywords().getKeywords().contains(k2));
        assertFalse(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(1, p.getOtherNames().getOtherNames().size());
        assertFalse(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertFalse(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
        assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r3));
    }

    @Test
    public void testPerson_When_SomePrivate_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.PUBLIC);
        Biography bio = createBiography(Visibility.PRIVATE);

        Address a1 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Address a2 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Address a3 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Email e2 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Email e3 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertEquals(name, p.getName());
        assertNull(p.getBiography());
        // Check addresses
        assertEquals(1, p.getAddresses().getAddress().size());
        assertFalse(p.getAddresses().getAddress().contains(a1));
        assertFalse(p.getAddresses().getAddress().contains(a2));
        assertTrue(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(1, p.getEmails().getEmails().size());
        assertFalse(p.getEmails().getEmails().contains(e1));
        assertFalse(p.getEmails().getEmails().contains(e2));
        assertTrue(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(1, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(1, p.getKeywords().getKeywords().size());
        assertFalse(p.getKeywords().getKeywords().contains(k1));
        assertFalse(p.getKeywords().getKeywords().contains(k2));
        assertTrue(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(1, p.getOtherNames().getOtherNames().size());
        assertFalse(p.getOtherNames().getOtherNames().contains(o1));
        assertFalse(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
        assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
    }

    @Test
    public void testPerson_When_AllPrivate_NoSource_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.PRIVATE);
        Biography bio = createBiography(Visibility.PRIVATE);

        Address a1 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Address a2 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Address a3 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Email e2 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertNull(p.getName());
        assertNull(p.getBiography());
        // Check addresses
        assertEquals(0, p.getAddresses().getAddress().size());
        // Check emails
        assertEquals(0, p.getEmails().getEmails().size());
        // Check ext ids
        assertEquals(0, p.getExternalIdentifiers().getExternalIdentifiers().size());
        // Check keywords
        assertEquals(0, p.getKeywords().getKeywords().size());
        // Check other names
        assertEquals(0, p.getOtherNames().getOtherNames().size());
        // Check researcher urls
        assertEquals(0, p.getResearcherUrls().getResearcherUrls().size());
    }

    @Test
    public void testPerson_When_AllPrivate_Source_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.PRIVATE);
        Biography bio = createBiography(Visibility.PRIVATE);

        Address a1 = createAddress(Visibility.PRIVATE, CLIENT_1);
        Address a2 = createAddress(Visibility.PRIVATE, CLIENT_1);
        Address a3 = createAddress(Visibility.PRIVATE, CLIENT_1);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PRIVATE, CLIENT_1);
        Email e2 = createEmail(Visibility.PRIVATE, CLIENT_1);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_1);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PRIVATE, CLIENT_1);
        Keyword k2 = createKeyword(Visibility.PRIVATE, CLIENT_1);
        Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_1);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertNull(p.getName());
        assertNull(p.getBiography());
        // Check addresses
        assertEquals(3, p.getAddresses().getAddress().size());
        assertTrue(p.getAddresses().getAddress().contains(a1));
        assertTrue(p.getAddresses().getAddress().contains(a2));
        assertTrue(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(3, p.getEmails().getEmails().size());
        assertTrue(p.getEmails().getEmails().contains(e1));
        assertTrue(p.getEmails().getEmails().contains(e2));
        assertTrue(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(3, p.getKeywords().getKeywords().size());
        assertTrue(p.getKeywords().getKeywords().contains(k1));
        assertTrue(p.getKeywords().getKeywords().contains(k2));
        assertTrue(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
    }

    @Test
    public void testPerson_When_AllPublic_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PERSON_READ_LIMITED);
        Name name = createName(Visibility.PUBLIC);
        Biography bio = createBiography(Visibility.PUBLIC);

        Address a1 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Address a2 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Address a3 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e2 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e3 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertEquals(name, p.getName());
        assertEquals(bio, p.getBiography());
        // Check addresses
        assertEquals(3, p.getAddresses().getAddress().size());
        assertTrue(p.getAddresses().getAddress().contains(a1));
        assertTrue(p.getAddresses().getAddress().contains(a2));
        assertTrue(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(3, p.getEmails().getEmails().size());
        assertTrue(p.getEmails().getEmails().contains(e1));
        assertTrue(p.getEmails().getEmails().contains(e2));
        assertTrue(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(3, p.getKeywords().getKeywords().size());
        assertTrue(p.getKeywords().getKeywords().contains(k1));
        assertTrue(p.getKeywords().getKeywords().contains(k2));
        assertTrue(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
    }

    @Test
    public void testPerson_When_SomeLimited_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PERSON_READ_LIMITED);
        Name name = createName(Visibility.LIMITED);
        Biography bio = createBiography(Visibility.LIMITED);

        Address a1 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Address a2 = createAddress(Visibility.LIMITED, CLIENT_2);
        Address a3 = createAddress(Visibility.LIMITED, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e2 = createEmail(Visibility.LIMITED, CLIENT_2);
        Email e3 = createEmail(Visibility.LIMITED, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.LIMITED, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.LIMITED, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.LIMITED, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.LIMITED, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.LIMITED, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.LIMITED, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertEquals(name, p.getName());
        assertEquals(bio, p.getBiography());
        // Check addresses
        assertEquals(3, p.getAddresses().getAddress().size());
        assertTrue(p.getAddresses().getAddress().contains(a1));
        assertTrue(p.getAddresses().getAddress().contains(a2));
        assertTrue(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(3, p.getEmails().getEmails().size());
        assertTrue(p.getEmails().getEmails().contains(e1));
        assertTrue(p.getEmails().getEmails().contains(e2));
        assertTrue(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(3, p.getKeywords().getKeywords().size());
        assertTrue(p.getKeywords().getKeywords().contains(k1));
        assertTrue(p.getKeywords().getKeywords().contains(k2));
        assertTrue(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
    }

    @Test
    public void testPerson_When_SomePrivate_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PERSON_READ_LIMITED);
        Name name = createName(Visibility.PRIVATE);
        Biography bio = createBiography(Visibility.PUBLIC);

        Address a1 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Address a2 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Address a3 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e2 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertNull(p.getName());
        assertEquals(bio, p.getBiography());
        // Check addresses
        assertEquals(1, p.getAddresses().getAddress().size());
        assertTrue(p.getAddresses().getAddress().contains(a1));
        assertFalse(p.getAddresses().getAddress().contains(a2));
        assertFalse(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(1, p.getEmails().getEmails().size());
        assertTrue(p.getEmails().getEmails().contains(e1));
        assertFalse(p.getEmails().getEmails().contains(e2));
        assertFalse(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(1, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(1, p.getKeywords().getKeywords().size());
        assertTrue(p.getKeywords().getKeywords().contains(k1));
        assertFalse(p.getKeywords().getKeywords().contains(k2));
        assertFalse(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(1, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertFalse(p.getOtherNames().getOtherNames().contains(o2));
        assertFalse(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r3));
    }

    @Test
    public void testPerson_When_AllPrivate_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PERSON_READ_LIMITED);
        Name name = createName(Visibility.PRIVATE);
        Biography bio = createBiography(Visibility.PRIVATE);

        Address a1 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Address a2 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Address a3 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Email e2 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertNull(p.getName());
        assertNull(p.getBiography());
        // Check addresses
        assertEquals(0, p.getAddresses().getAddress().size());
        // Check emails
        assertEquals(0, p.getEmails().getEmails().size());
        // Check ext ids
        assertEquals(0, p.getExternalIdentifiers().getExternalIdentifiers().size());
        // Check keywords
        assertEquals(0, p.getKeywords().getKeywords().size());
        // Check other names
        assertEquals(0, p.getOtherNames().getOtherNames().size());
        // Check researcher urls
        assertEquals(0, p.getResearcherUrls().getResearcherUrls().size());
    }

    @Test
    public void testPerson_When_AllPrivate_Source_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PERSON_READ_LIMITED);
        Name name = createName(Visibility.PRIVATE);
        Biography bio = createBiography(Visibility.PRIVATE);

        Address a1 = createAddress(Visibility.PRIVATE, CLIENT_1);
        Address a2 = createAddress(Visibility.PRIVATE, CLIENT_1);
        Address a3 = createAddress(Visibility.PRIVATE, CLIENT_1);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PRIVATE, CLIENT_1);
        Email e2 = createEmail(Visibility.PRIVATE, CLIENT_1);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_1);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PRIVATE, CLIENT_1);
        Keyword k2 = createKeyword(Visibility.PRIVATE, CLIENT_1);
        Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_1);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertNull(p.getName());
        assertNull(p.getBiography());
        // Check addresses
        assertEquals(3, p.getAddresses().getAddress().size());
        assertTrue(p.getAddresses().getAddress().contains(a1));
        assertTrue(p.getAddresses().getAddress().contains(a2));
        assertTrue(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(3, p.getEmails().getEmails().size());
        assertTrue(p.getEmails().getEmails().contains(e1));
        assertTrue(p.getEmails().getEmails().contains(e2));
        assertTrue(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(3, p.getKeywords().getKeywords().size());
        assertTrue(p.getKeywords().getKeywords().contains(k1));
        assertTrue(p.getKeywords().getKeywords().contains(k2));
        assertTrue(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
    }

    @Test
    public void testPerson_When_MixedVisibility_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PERSON_READ_LIMITED);
        Name name = createName(Visibility.LIMITED);
        Biography bio = createBiography(Visibility.PUBLIC);

        Address a1 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Address a2 = createAddress(Visibility.LIMITED, CLIENT_2);
        Address a3 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e2 = createEmail(Visibility.LIMITED, CLIENT_2);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.LIMITED, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.LIMITED, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.LIMITED, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertEquals(name, p.getName());
        assertEquals(bio, p.getBiography());
        // Check addresses
        assertEquals(2, p.getAddresses().getAddress().size());
        assertTrue(p.getAddresses().getAddress().contains(a1));
        assertTrue(p.getAddresses().getAddress().contains(a2));
        assertFalse(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(2, p.getEmails().getEmails().size());
        assertTrue(p.getEmails().getEmails().contains(e1));
        assertTrue(p.getEmails().getEmails().contains(e2));
        assertFalse(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(2, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(2, p.getKeywords().getKeywords().size());
        assertTrue(p.getKeywords().getKeywords().contains(k1));
        assertTrue(p.getKeywords().getKeywords().contains(k2));
        assertFalse(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(2, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertFalse(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(2, p.getResearcherUrls().getResearcherUrls().size());
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r3));
    }

    @Test
    public void testPerson_When_ReadLimitedToken_EmptyElement() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PERSON_READ_LIMITED);
        Person p = new Person();
        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
    }
}
