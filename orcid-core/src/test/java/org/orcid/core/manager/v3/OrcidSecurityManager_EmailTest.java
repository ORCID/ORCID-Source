package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.Email;
import org.orcid.jaxb.model.v3.rc2.record.Emails;
import org.orcid.jaxb.model.v3.rc2.record.Person;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidSecurityManager_EmailTest extends OrcidSecurityManagerTestBase {

    @Test(expected = OrcidUnauthorizedException.class)
    public void testEmail_TokenForOtherUser() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_2, new ArrayList<Email>(), ScopePathType.ORCID_BIO_READ_LIMITED);
    }
    
    @Test
    public void testEmail_NoSource_ReadPublic() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);

        Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e2 = createEmail(Visibility.LIMITED, CLIENT_2);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);

        List<Email> emails = new ArrayList<Email>(Arrays.asList(e1, e2, e3));

        orcidSecurityManager.checkAndFilter(ORCID_1, emails, ScopePathType.ORCID_BIO_READ_LIMITED);

        assertEquals(1, emails.size());
        assertTrue(emails.contains(e1));
        assertFalse(emails.contains(e2));
        assertFalse(emails.contains(e3));
    }

    @Test
    public void testEmail_SourceOfPrivate_ReadPublic() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);

        Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e2 = createEmail(Visibility.LIMITED, CLIENT_2);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_1);

        List<Email> emails = new ArrayList<Email>(Arrays.asList(e1, e2, e3));

        orcidSecurityManager.checkAndFilter(ORCID_1, emails, ScopePathType.ORCID_BIO_READ_LIMITED);

        assertEquals(2, emails.size());
        assertTrue(emails.contains(e1));
        assertFalse(emails.contains(e2));
        assertTrue(emails.contains(e3));
    }

    @Test
    public void testEmail_SourceOfALL_ReadPublic() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);

        Email e1 = createEmail(Visibility.PUBLIC, CLIENT_1);
        Email e2 = createEmail(Visibility.LIMITED, CLIENT_1);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_1);

        List<Email> emails = new ArrayList<Email>(Arrays.asList(e1, e2, e3));

        orcidSecurityManager.checkAndFilter(ORCID_1, emails, ScopePathType.ORCID_BIO_READ_LIMITED);

        assertEquals(3, emails.size());
        assertTrue(emails.contains(e1));
        assertTrue(emails.contains(e2));
        assertTrue(emails.contains(e3));
    }
    
    @Test
    public void testEmail_NoSource_AllPrivate_ReadPrivateEmail() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.EMAIL_READ_PRIVATE);

        Email e1 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Email e2 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);

        List<Email> emails = new ArrayList<Email>(Arrays.asList(e1, e2, e3));

        orcidSecurityManager.checkAndFilter(ORCID_1, emails, ScopePathType.ORCID_BIO_READ_LIMITED);

        assertEquals(3, emails.size());
        assertTrue(emails.contains(e1));
        assertTrue(emails.contains(e2));
        assertTrue(emails.contains(e3));
    }
    
    @Test
    public void testEmail_ReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);

        Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e2 = createEmail(Visibility.LIMITED, CLIENT_2);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);

        List<Email> emails = new ArrayList<Email>(Arrays.asList(e1, e2, e3));

        orcidSecurityManager.checkAndFilter(ORCID_1, emails, ScopePathType.ORCID_BIO_READ_LIMITED);

        assertEquals(2, emails.size());
        assertTrue(emails.contains(e1));
        assertTrue(emails.contains(e2));
        assertFalse(emails.contains(e3));
    }

    @Test
    public void testEmail_ReadPrivate_onePrivate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.EMAIL_READ_PRIVATE);

        Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e2 = createEmail(Visibility.LIMITED, CLIENT_2);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);

        List<Email> emails = new ArrayList<Email>(Arrays.asList(e1, e2, e3));

        orcidSecurityManager.checkAndFilter(ORCID_1, emails, ScopePathType.ORCID_BIO_READ_LIMITED);

        assertEquals(3, emails.size());
        assertTrue(emails.contains(e1));
        assertTrue(emails.contains(e2));
        assertTrue(emails.contains(e3));
    }    

    @Test
    public void testPerson_NoReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);

        Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e2 = createEmail(Visibility.LIMITED, CLIENT_2);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);

        List<Email> emailList = new ArrayList<Email>(Arrays.asList(e1, e2, e3));
        Emails emails = new Emails();
        emails.setEmails(emailList);
        
        Person p = new Person();
        p.setEmails(emails);
        
        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);        
        assertNotNull(p.getEmails());
        assertEquals(1, p.getEmails().getEmails().size());
        assertTrue(p.getEmails().getEmails().contains(e1));
        assertFalse(p.getEmails().getEmails().contains(e2));
        assertFalse(p.getEmails().getEmails().contains(e3));
    }

    @Test
    public void testPerson_ReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_LIMITED);

        Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e2 = createEmail(Visibility.LIMITED, CLIENT_2);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);

        List<Email> emailList = new ArrayList<Email>(Arrays.asList(e1, e2, e3));
        Emails emails = new Emails();
        emails.setEmails(emailList);
        
        Person p = new Person();
        p.setEmails(emails);
        
        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);        
        assertNotNull(p.getEmails());
        assertEquals(2, p.getEmails().getEmails().size());
        assertTrue(p.getEmails().getEmails().contains(e1));
        assertTrue(p.getEmails().getEmails().contains(e2));
        assertFalse(p.getEmails().getEmails().contains(e3));
    }

    @Test
    public void testPerson_ReadPrivateEmail() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.EMAIL_READ_PRIVATE);

        Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e2 = createEmail(Visibility.LIMITED, CLIENT_2);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);

        List<Email> emailList = new ArrayList<Email>(Arrays.asList(e1, e2, e3));
        Emails emails = new Emails();
        emails.setEmails(emailList);
        
        Person p = new Person();
        p.setEmails(emails);
        
        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);        
        assertNotNull(p.getEmails());
        assertEquals(3, p.getEmails().getEmails().size());
        assertTrue(p.getEmails().getEmails().contains(e1));
        assertTrue(p.getEmails().getEmails().contains(e2));
        assertTrue(p.getEmails().getEmails().contains(e3));
    }
}
