package org.orcid.core.manager.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.ehcache.Cache;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.locale.LocaleManagerImpl;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.SalesForceManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.salesforce.cache.MemberDetailsCacheKey;
import org.orcid.core.salesforce.dao.SalesForceDao;
import org.orcid.core.salesforce.model.Consortium;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.ContactPermission;
import org.orcid.core.salesforce.model.ContactRole;
import org.orcid.core.salesforce.model.ContactRoleType;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.OrgId;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.persistence.aop.ProfileLastModifiedAspect;
import org.orcid.persistence.dao.SalesForceConnectionDao;
import org.orcid.persistence.jpa.entities.SalesForceConnectionEntity;
import org.orcid.test.TargetProxyHelper;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceManagerImplTest {

    private static final String TEST_ORCID = "4444-4444-4444-4441";

    private SalesForceManager salesForceManager = new SalesForceManagerImpl();

    @Mock
    private SalesForceDao salesForceDao;

    @Mock
    private SourceManager sourceManager;

    @Mock
    private SalesForceConnectionDao salesForceConnectionDao;

    @Mock
    private Cache<String, List<Member>> salesForceMembersListCache;

    @Mock
    private Cache<MemberDetailsCacheKey, MemberDetails> salesForceMemberDetailsCache;
    
    @Mock
    private Cache<String, List<Member>> salesForceConsortiaListCache;

    @Mock
    private Cache<String, Consortium> salesForceConsortiumCache;

    @Mock
    private Cache<String, List<Contact>> salesForceContactsCache;

    @Mock
    private EmailManager emailManager;

    @Mock
    private ProfileLastModifiedAspect profileLastModifiedAspect;
    
    @Mock
    private OrcidUrlManager orcidUrlManager;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(salesForceManager, "emailManager", emailManager);
        TargetProxyHelper.injectIntoProxy(salesForceManager, "salesForceDao", salesForceDao);
        TargetProxyHelper.injectIntoProxy(salesForceManager, "sourceManager", sourceManager);
        TargetProxyHelper.injectIntoProxy(salesForceManager, "salesForceConnectionDao", salesForceConnectionDao);
        TargetProxyHelper.injectIntoProxy(salesForceManager, "salesForceMembersListCache", salesForceMembersListCache);
        TargetProxyHelper.injectIntoProxy(salesForceManager, "salesForceMemberDetailsCache", salesForceMemberDetailsCache);
        TargetProxyHelper.injectIntoProxy(salesForceManager, "salesForceConsortiaListCache", salesForceConsortiaListCache);
        TargetProxyHelper.injectIntoProxy(salesForceManager, "salesForceConsortiumCache", salesForceConsortiumCache);
        TargetProxyHelper.injectIntoProxy(salesForceManager, "salesForceContactsCache", salesForceContactsCache);
        TargetProxyHelper.injectIntoProxy(salesForceManager, "orcidUrlManager", orcidUrlManager);

        setUpUser();
        setUpContact1();
        setUpContact2();
        setUpOrcidUrlManager();
        setUpLocalManager();
    }

    private void setUpUser() {
        when(sourceManager.retrieveRealUserOrcid()).thenReturn(TEST_ORCID);
        SalesForceConnectionEntity connection = new SalesForceConnectionEntity();
        connection.setSalesForceAccountId("account1Id");
        List<SalesForceConnectionEntity> connections = new ArrayList<>();
        connections.add(connection);
        when(salesForceConnectionDao.findByOrcid(TEST_ORCID)).thenReturn(connections);
    }

    private void setUpContact1() {
        List<ContactRole> contact1Roles = new ArrayList<>();
        when(salesForceDao.retrieveContactRolesByContactIdAndAccountId("contact1Id", "account1Id")).thenReturn(contact1Roles);
    }

    private void setUpContact2() {
        List<ContactRole> contact2Roles = new ArrayList<>();
        contact2Roles.add(createContactRole("contact2Id", "contact2Idrole1Id", ContactRoleType.MAIN_CONTACT));
        when(salesForceDao.retrieveContactRolesByContactIdAndAccountId("contact2Id", "account1Id")).thenReturn(contact2Roles);
    }

    private void setUpContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(createContact("id1", "account1Id", "email1@test.orcid.org", "0000-0000-0000-0000"));
        contacts.add(createContact("id2", "account1Id", "email2@test.orcid.org", null));
        contacts.add(createContact("id3", "account1Id", "email3@test.orcid.org", "0000-0000-0000-0001"));
        when(salesForceDao.retrieveAllContactsByAccountId("account1Id")).thenReturn(contacts);
        when(salesForceDao.createContact(any(Contact.class))).thenReturn("id4");
    }

    private void setUpEmails() {
        Emails emails = new Emails();
        Email email = new Email();
        email.setEmail("email3@test.orcid.org");
        email.setPrimary(true);
        emails.getEmails().add(email);
        salesForceManager.setProfileLastModifiedAspect(profileLastModifiedAspect);
        when(profileLastModifiedAspect.retrieveLastModifiedDate("0000-0000-0000-0001")).thenReturn(null);
        when(emailManager.getEmails("0000-0000-0000-0001")).thenReturn(emails);
    }

    private void setUpOrcidUrlManager() {
        when(orcidUrlManager.getBaseUrl()).thenReturn("https://orcid.org");
    }
    
    private void setUpLocalManager() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(
                "classpath:i18n/about,classpath:i18n/api,classpath:i18n/email,classpath:i18n/javascript,classpath:i18n/messages,classpath:i18n/admin,classpath:i18n/identifiers,classpath:i18n/notranslate,classpath:i18n/notranslate"
                        .split(","));
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(5);
        LocaleManagerImpl localeManager = new LocaleManagerImpl();
        localeManager.setMessageSource(messageSource);
        TargetProxyHelper.injectIntoProxy(salesForceManager, "localeManager", localeManager);
    }

    private Contact createContact(String contactId, String accountId, String email, String orcid) {
        Contact c = new Contact();
        c.setId(contactId);
        c.setAccountId(accountId);
        c.setEmail(email);
        c.setOrcid(orcid);
        return c;
    }
    
    private Contact createContact(String firstName, String lastName, String email, String orcid, Boolean isVotingContact, ContactRoleType roleType, String memberName) {
        Contact contact = new Contact();
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setEmail(email);
        contact.setOrcid(orcid);
        ContactRole role = new ContactRole();
        role.setRoleType(roleType);
        role.setVotingContact(isVotingContact);
        contact.setRole(role);
        Member member = new Member();
        member.setPublicDisplayName(memberName);
        contact.setMember(member);
        return contact;
    }

    private ContactRole createContactRole(String contactId, String roleId, ContactRoleType roleType) {
        ContactRole contactRole = new ContactRole();
        contactRole.setId(roleId);
        contactRole.setRoleType(roleType);
        contactRole.setContactId(contactId);
        return contactRole;
    }

    private Contact createContactWithRole(String contactId, String accountId, String email, String orcid, String roleId, ContactRoleType roleType) {
        Contact contact = createContact(contactId, accountId, email, orcid);
        contact.setRole(createContactRole(contactId, roleId, roleType));
        return contact;
    }

    @Test
    public void testUpdateContact2() {
        // Switch from main to technical contact
        Contact contact = new Contact();
        contact.setId("contact2Id");
        contact.setAccountId("account1Id");
        ContactRole role = new ContactRole(ContactRoleType.TECHNICAL_CONTACT);
        role.setId("contact2Idrole1Id");
        contact.setRole(role);
        ((SalesForceManagerImpl) salesForceManager).updateContact(contact, Collections.<Contact> emptyList());
        verify(salesForceDao, times(1)).updateContactRole(argThat(r -> {
            return "contact2Idrole1Id".equals(r.getId()) && "contact2Id".equals(r.getContactId()) && ContactRoleType.MAIN_CONTACT.equals(r.getRoleType())
                    && !r.isCurrent();
        }));
        verify(salesForceDao, times(1)).createContactRole(argThat(r -> {
            return "contact2Id".equals(r.getContactId()) && "account1Id".equals(r.getAccountId()) && ContactRoleType.TECHNICAL_CONTACT.equals(r.getRoleType());
        }));
    }

    @Test
    public void createNewContactTest() {
        setUpContacts();
        Contact c1 = new Contact();
        c1.setEmail("new_email@test.orcid.org");
        c1.setAccountId("account1Id");
        salesForceManager.createContact(c1);
        verify(salesForceDao, times(1)).retrieveAllContactsByAccountId("account1Id");
        verify(salesForceDao, times(1)).createContact(c1);
        verify(salesForceDao, times(1)).createContactRole(argThat(a -> {
            return "id4".equals(a.getContactId()) && ContactRoleType.OTHER_CONTACT.equals(a.getRoleType()) && "account1Id".equals(a.getAccountId());
        }));
    }

    @Test
    public void createNewContact_WithExistingEmail_Test() {
        setUpContacts();
        Contact c1 = new Contact();
        c1.setEmail("email1@test.orcid.org");
        c1.setAccountId("account1Id");
        salesForceManager.createContact(c1);
        verify(salesForceDao, times(1)).retrieveAllContactsByAccountId("account1Id");
        verify(salesForceDao, times(0)).createContact(c1);
        verify(salesForceDao, times(1)).createContactRole(argThat(a -> {
            return "id1".equals(a.getContactId()) && ContactRoleType.OTHER_CONTACT.equals(a.getRoleType()) && "account1Id".equals(a.getAccountId());
        }));
    }

    @Test
    public void createNewContact_WithExistingOrcid_Test() {
        setUpEmails();
        setUpContacts();
        Contact c1 = new Contact();
        c1.setOrcid("0000-0000-0000-0001");
        c1.setAccountId("account1Id");
        salesForceManager.createContact(c1);
        verify(salesForceDao, times(1)).retrieveAllContactsByAccountId("account1Id");
        verify(salesForceDao, times(0)).createContact(c1);
        verify(salesForceDao, times(1)).createContactRole(argThat(a -> {
            return "id3".equals(a.getContactId()) && ContactRoleType.OTHER_CONTACT.equals(a.getRoleType()) && "account1Id".equals(a.getAccountId());
        }));
    }

    @Test
    public void testFindBestWebsiteMatch() throws MalformedURLException {
        List<Member> members = new ArrayList<>();
        members.add(createMember("1", "Account 1", "Account 1 Display", "https://account.com"));
        members.add(createMember("2", "Account 2", "Account 2 Display", "http://www.account.com"));
        members.add(createMember("3", "Account 3", "Account 3 Display", "https://account.com/abc"));
        members.add(createMember("4", "Account 4", "Account 4 Display", "http://www.else.co.uk"));

        assertEquals("1", salesForceManager.findBestWebsiteMatch(new URL("https://account.com"), members).get().getId());
        assertEquals("1", salesForceManager.findBestWebsiteMatch(new URL("http://account.com"), members).get().getId());
        assertEquals("1", salesForceManager.findBestWebsiteMatch(new URL("http://account.com/123?abc"), members).get().getId());
        assertEquals("2", salesForceManager.findBestWebsiteMatch(new URL("http://www.account.com"), members).get().getId());
        assertEquals("2", salesForceManager.findBestWebsiteMatch(new URL("https://www.account.com"), members).get().getId());
        assertEquals("3", salesForceManager.findBestWebsiteMatch(new URL("https://account.com/abc"), members).get().getId());
        assertEquals("4", salesForceManager.findBestWebsiteMatch(new URL("http://else.co.uk"), members).get().getId());
    }

    @Test
    public void testCalculateContactPermissions() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(createContactWithRole("contact1", "account1", "contact1@test.com", "0000-0000-0000-0001", "role1", ContactRoleType.MAIN_CONTACT));
        contacts.add(createContactWithRole("contact2", "account1", "contact2@test.com", "0000-0000-0000-0002", "role2", ContactRoleType.AGREEMENT_SIGNATORY));
        contacts.add(createContactWithRole("contact3", "account1", "contact3@test.com", "0000-0000-0000-0003", "role3", ContactRoleType.INVOICE_CONTACT));
        Contact votingContact = createContactWithRole("contact4", "account1", "contact4@test.com", "0000-0000-0000-0004", "role4", ContactRoleType.OTHER_CONTACT);
        votingContact.getRole().setVotingContact(true);
        contacts.add(votingContact);

        when(sourceManager.retrieveRealUserOrcid()).thenReturn("0000-0000-0000-0001");
        List<ContactPermission> permissions = salesForceManager.calculateContactPermissions(contacts);
        assertNotNull(permissions);
        assertEquals(4, permissions.size());
        Map<String, ContactPermission> permissionsMap = ContactPermission.mapByContactRoleId(permissions);
        assertTrue(permissionsMap.get("role1").isAllowedEdit());
        assertTrue(permissionsMap.get("role2").isAllowedEdit());
        assertTrue(permissionsMap.get("role3").isAllowedEdit());
        assertTrue(permissionsMap.get("role4").isAllowedEdit());

        when(sourceManager.retrieveRealUserOrcid()).thenReturn("0000-0000-0000-0003");
        permissions = salesForceManager.calculateContactPermissions(contacts);
        assertNotNull(permissions);
        assertEquals(4, permissions.size());
        permissionsMap = ContactPermission.mapByContactRoleId(permissions);
        assertFalse(permissionsMap.get("role1").isAllowedEdit());
        assertFalse(permissionsMap.get("role2").isAllowedEdit());
        assertTrue(permissionsMap.get("role3").isAllowedEdit());
        assertFalse(permissionsMap.get("role4").isAllowedEdit());
    }

    @Test
    public void testCheckContactUpdatePermissions() {
        List<Contact> existingContacts = new ArrayList<>();
        existingContacts.add(createContactWithRole("contact1", "account1", "contact1@test.com", "0000-0000-0000-0001", "role1", ContactRoleType.MAIN_CONTACT));
        existingContacts.add(createContactWithRole("contact2", "account1", "contact2@test.com", "0000-0000-0000-0002", "role2", ContactRoleType.AGREEMENT_SIGNATORY));
        existingContacts.add(createContactWithRole("contact3", "account1", "contact3@test.com", "0000-0000-0000-0003", "role3", ContactRoleType.INVOICE_CONTACT));
        Contact votingContact = createContactWithRole("contact4", "account1", "contact4@test.com", "0000-0000-0000-0004", "role4", ContactRoleType.OTHER_CONTACT);
        votingContact.getRole().setVotingContact(true);
        existingContacts.add(votingContact);

        List<Contact> updatedContacts = new ArrayList<>();
        Contact updatedContact1 = createContactWithRole("contact1", "account1", "contact1@test.com", "0000-0000-0000-0001", "role1", ContactRoleType.MAIN_CONTACT);
        updatedContacts.add(updatedContact1);
        updatedContacts.add(createContactWithRole("contact2", "account1", "contact2@test.com", "0000-0000-0000-0002", "role2", ContactRoleType.AGREEMENT_SIGNATORY));
        updatedContacts.add(createContactWithRole("contact3", "account1", "contact3@test.com", "0000-0000-0000-0003", "role3", ContactRoleType.INVOICE_CONTACT));
        Contact updatedVotingContact = createContactWithRole("contact4", "account1", "contact4@test.com", "0000-0000-0000-0004", "role4", ContactRoleType.OTHER_CONTACT);
        updatedVotingContact.getRole().setVotingContact(true);
        updatedContacts.add(updatedVotingContact);

        when(sourceManager.retrieveRealUserOrcid()).thenReturn("0000-0000-0000-0001");
        salesForceManager.checkContactUpdatePermissions(existingContacts, updatedContacts);

        updatedContact1.getRole().setRoleType(ContactRoleType.OTHER_CONTACT);
        when(sourceManager.retrieveRealUserOrcid()).thenReturn("0000-0000-0000-0001");
        try {
            salesForceManager.checkContactUpdatePermissions(existingContacts, updatedContacts);
        } catch (OrcidUnauthorizedException e) {
            fail("Should be able to change main contact role when am main contact");
        }

        when(sourceManager.retrieveRealUserOrcid()).thenReturn("0000-0000-0000-0003");
        boolean preventedChange = false;
        try {
            salesForceManager.checkContactUpdatePermissions(existingContacts, updatedContacts);
        } catch (OrcidUnauthorizedException e) {
            preventedChange = true;
        }
        if (!preventedChange) {
            fail("Should not be able to change main contact role when am not main/signatory contact");
        }

        // Set role back to what it was, but change voting contact.
        updatedContact1.getRole().setRoleType(ContactRoleType.MAIN_CONTACT);
        updatedContact1.getRole().setVotingContact(true);
        updatedVotingContact.getRole().setVotingContact(false);

        when(sourceManager.retrieveRealUserOrcid()).thenReturn("0000-0000-0000-0001");
        try {
            salesForceManager.checkContactUpdatePermissions(existingContacts, updatedContacts);
        } catch (OrcidUnauthorizedException e) {
            fail("Should be able to change voting contact when am main contact");
        }

        when(sourceManager.retrieveRealUserOrcid()).thenReturn("0000-0000-0000-0003");
        preventedChange = false;
        try {
            salesForceManager.checkContactUpdatePermissions(existingContacts, updatedContacts);
        } catch (OrcidUnauthorizedException e) {
            preventedChange = true;
        }
        if (!preventedChange) {
            fail("Should not be able to change voting contact when am not main/signatory contact");
        }
    }
    
    @Test
    public void testSubMemberContacts() throws IOException {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(createContact("Will", "Simpson", "w.simpson@orcid.org", "0000-0003-4654-1403", true, ContactRoleType.AGREEMENT_SIGNATORY, "ORCID"));
        contacts.add(createContact("Fname 1", "Lname 1", "test1@nowhere.org", "1111-1111-1111-1111", false, ContactRoleType.TECHNICAL_CONTACT, "Test Org"));
        contacts.add(createContact("Fname 2", "Lname 2", "test2@nowhere.org", null, true, ContactRoleType.MAIN_CONTACT, "Test Org"));
        
        StringWriter writer = new StringWriter();

        salesForceManager.writeContactsCsv(writer, contacts);
        String result = writer.toString();

        String expected = IOUtils.toString(getClass().getResource("/org/orcid/core/manager/expected_all_contacts.csv"));
        assertEquals(expected, result);
    }
    
    @Test
    public void testRetrieveAllOrgIds() throws MalformedURLException {
        List<Member> allMembers = Arrays.asList(createMember("account1", "account 1", "account 1", "https://bbc.co.uk"), createMember("account2", "account 2", "account 2", "https://bbc.co.uk"));
        Mockito.when(salesForceMembersListCache.get(Mockito.anyString())).thenReturn(allMembers);
        
        Mockito.when(salesForceDao.retrieveOrgIdsByAccountId(Mockito.eq("account1"))).thenReturn(getOrgIdList("firstId", "firstType", "secondId", "secondType"));
        Mockito.when(salesForceDao.retrieveOrgIdsByAccountId(Mockito.eq("account2"))).thenReturn(getOrgIdList("thirdId", "thirdType", "fourthId", "fourthType"));
        
        List<OrgId> allIds = salesForceManager.retrieveAllOrgIds();
        assertEquals(4, allIds.size());
        assertEquals("firstId", allIds.get(0).getOrgIdValue());
        assertEquals("firstType", allIds.get(0).getOrgIdType());
        assertEquals("secondId", allIds.get(1).getOrgIdValue());
        assertEquals("secondType", allIds.get(1).getOrgIdType());
        assertEquals("thirdId", allIds.get(2).getOrgIdValue());
        assertEquals("thirdType", allIds.get(2).getOrgIdType());
        assertEquals("fourthId", allIds.get(3).getOrgIdValue());
        assertEquals("fourthType", allIds.get(3).getOrgIdType());
    }
    
    private List<OrgId> getOrgIdList(String firstId, String firstType, String secondId, String secondType) {
        OrgId first = new OrgId();
        first.setOrgIdType(firstType);
        first.setOrgIdValue(firstId);
        
        OrgId second = new OrgId();
        second.setOrgIdType(secondType);
        second.setOrgIdValue(secondId);
        
        return Arrays.asList(first, second);
    }
    
    private Member createMember(String accountId, String name, String publicDisplayName, String website) throws MalformedURLException {
        Member member = new Member();
        member.setId(accountId);
        member.setName(name);
        member.setPublicDisplayName(publicDisplayName);
        member.setWebsiteUrl(new URL(website));
        return member;
    }

}
