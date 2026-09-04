package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.orcid.core.adapter.v3.JpaJaxbEmailAdapter;
import org.orcid.core.aop.ProfileLastModifiedAspect;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.v3.impl.EmailManagerImpl;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class EmailManagerTest {

    private static final String ORCID = "0000-0000-0000-0001";

    private EmailManagerImpl emailManager;

    @Mock
    private EmailDao emailDao;
    @Mock
    private ProfileDao profileDao;
    @Mock
    private SourceManager sourceManager;
    @Mock
    private ProfileEmailDomainManager profileEmailDomainManager;
    @Mock
    private ProfileHistoryEventManager profileHistoryEventManager;
    @Mock
    private OrcidSecurityManager orcidSecurityManager;
    @Mock
    private EncryptionManager encryptionManager;
    @Mock
    private JpaJaxbEmailAdapter jpaJaxbEmailAdapter;
    @Mock
    private ProfileLastModifiedAspect profileLastModifiedAspect;

    @Before
    public void setUp() {
        emailManager = org.mockito.Mockito.spy(new EmailManagerImpl());

        ReflectionTestUtils.setField(emailManager, "emailDao", emailDao);
        ReflectionTestUtils.setField(emailManager, "profileDao", profileDao);
        ReflectionTestUtils.setField(emailManager, "sourceManager", sourceManager);
        ReflectionTestUtils.setField(emailManager, "profileEmailDomainManager", profileEmailDomainManager);
        ReflectionTestUtils.setField(emailManager, "profileHistoryEventManager", profileHistoryEventManager);
        ReflectionTestUtils.setField(emailManager, "orcidSecurityManager", orcidSecurityManager);
        ReflectionTestUtils.setField(emailManager, "encryptionManager", encryptionManager);
        ReflectionTestUtils.setField(emailManager, "jpaJaxbEmailAdapter", jpaJaxbEmailAdapter);
        ReflectionTestUtils.setField(emailManager, "profileLastModifiedAspect", profileLastModifiedAspect);

        lenient().when(encryptionManager.getEmailHash(anyString())).thenAnswer(i -> "hash:" + i.getArguments()[0]);
        lenient().when(jpaJaxbEmailAdapter.toEmailList(any(Collection.class))).thenAnswer(i -> toEmailList((Collection<EmailEntity>) i.getArguments()[0]));
        lenient().when(profileLastModifiedAspect.retrieveLastModifiedDate(anyString())).thenReturn(new Date(1L));
    }

    @Test
    public void removeEmailRejectsPrimaryEmail() {
        when(emailDao.isPrimaryEmail("orcid", "a@b.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> emailManager.removeEmail("orcid", "a@b.com"));

        assertEquals("Can't mark primary email as deleted", ex.getMessage());
        verify(emailDao, never()).removeEmail(anyString(), anyString());
    }

    @Test
    public void removeEmailRejectsUsersOnlyEmail() {
        when(emailDao.isPrimaryEmail("orcid", "a@b.com")).thenReturn(false);
        when(emailDao.findByOrcid(eq("orcid"), anyLong())).thenReturn(singletonEmailEntities("a@b.com"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> emailManager.removeEmail("orcid", "a@b.com"));

        assertEquals("Can't mark user's only email as deleted", ex.getMessage());
        verify(emailDao, never()).removeEmail(anyString(), anyString());
    }

    @Test
    public void removeEmailDelegatesToDao() {
        when(emailDao.isPrimaryEmail("orcid", "a@b.com")).thenReturn(false);
        when(emailDao.findByOrcid(eq("orcid"), anyLong())).thenReturn(twoEmailEntities("a@b.com", "x@y.com"));
        when(emailDao.removeEmail("orcid", "a@b.com")).thenReturn(true);

        assertTrue(emailManager.removeEmail("orcid", "a@b.com"));
    }

    @Test
    public void verifyEmailReturnsFalseWhenDaoRejects() {
        when(emailDao.verifyEmail("a@b.com")).thenReturn(false);

        assertFalse(emailManager.verifyEmail("orcid", "a@b.com"));
        verify(profileEmailDomainManager, never()).processDomain(anyString(), anyString());
    }

    @Test
    public void verifyEmailReturnsTrueWhenDaoVerifies() {
        when(emailDao.verifyEmail("a@b.com")).thenReturn(true);

        assertTrue(emailManager.verifyEmail("orcid", "a@b.com"));
    }

    @Test
    public void verifyPrimaryEmailVerifiesResolvedPrimary() {
        EmailEntity primary = new EmailEntity();
        primary.setEmail("primary@orcid.org");
        when(emailDao.findPrimaryEmail("orcid")).thenReturn(primary);
        when(emailDao.verifyEmail("primary@orcid.org")).thenReturn(true);

        assertTrue(emailManager.verifyPrimaryEmail("orcid"));
    }

    @Test
    public void verifyPrimaryEmailNoResultUpdatesFallbackAndRethrows() {
        when(emailDao.findPrimaryEmail("orcid")).thenThrow(new NoResultException());
        when(emailDao.findNewestVerifiedOrNewestEmail("orcid")).thenReturn("fallback@orcid.org");

        assertThrows(NoResultException.class, () -> emailManager.verifyPrimaryEmail("orcid"));
        verify(emailDao).updatePrimary("orcid", "fallback@orcid.org");
    }

    @Test
    public void verifyPrimaryEmailNonUniqueUpdatesFallbackAndRethrows() {
        when(emailDao.findPrimaryEmail("orcid")).thenThrow(new NonUniqueResultException());
        when(emailDao.findNewestPrimaryEmail("orcid")).thenReturn("newest@orcid.org");

        assertThrows(NonUniqueResultException.class, () -> emailManager.verifyPrimaryEmail("orcid"));
        verify(emailDao).updatePrimary("orcid", "newest@orcid.org");
    }

    @Test
    public void moveEmailToOtherAccountUpdatesDestinationWhenMoved() {
        when(emailDao.moveEmailToOtherAccountAsNonPrimary("a@b.com", "source", "dest")).thenReturn(true);

        assertTrue(emailManager.moveEmailToOtherAccount("a@b.com", "source", "dest"));
        verify(profileDao).updateLastModifiedDateAndIndexingStatusWithoutResult(eq("dest"), any(Date.class), eq(IndexingStatus.PENDING));
    }

    @Test
    public void moveEmailToOtherAccountSkipsDestinationUpdateWhenNotMoved() {
        when(emailDao.moveEmailToOtherAccountAsNonPrimary("a@b.com", "source", "dest")).thenReturn(false);

        assertFalse(emailManager.moveEmailToOtherAccount("a@b.com", "source", "dest"));
        verify(profileDao, never()).updateLastModifiedDateAndIndexingStatusWithoutResult(anyString(), any(Date.class), any(IndexingStatus.class));
    }

    @Test
    public void verifySetCurrentAndPrimaryRejectsBlankParams() {
        assertThrows(IllegalArgumentException.class, () -> emailManager.verifySetCurrentAndPrimary(" ", "a@b.com"));
        assertThrows(IllegalArgumentException.class, () -> emailManager.verifySetCurrentAndPrimary("orcid", " "));
    }

    @Test
    public void verifySetCurrentAndPrimaryDelegates() {
        when(emailDao.updateVerifySetCurrentAndPrimary("orcid", "a@b.com")).thenReturn(true);

        assertTrue(emailManager.verifySetCurrentAndPrimary("orcid", "a@b.com"));
    }

    @Test
    public void isAutoDeprecateEnableForEmailReturnsFalseForBlank() {
        assertFalse(emailManager.isAutoDeprecateEnableForEmail(" "));
        verify(emailDao, never()).isAutoDeprecateEnableForEmailUsingHash(anyString());
    }

    @Test
    public void isAutoDeprecateEnableForEmailUsesHashLookup() {
        when(emailDao.isAutoDeprecateEnableForEmailUsingHash(anyString())).thenReturn(true);

        assertTrue(emailManager.isAutoDeprecateEnableForEmail("  TeSt@email.com  "));
    }

    @Test
    public void addEmailAddsFilteredAndHashedValue() {
        SourceEntity source = new SourceEntity();
        source.setSourceProfile(new ProfileEntity(ORCID));
        when(sourceManager.retrieveActiveSourceEntity()).thenReturn(source);
        Email input = email(" TeSt@email.com ", false, Visibility.PUBLIC);

        Map<String, String> result = emailManager.addEmail(ORCID, input);

        assertTrue(result.isEmpty());
        verify(emailDao).addEmail(eq(ORCID), eq("TeSt@email.com"), eq("hash:TeSt@email.com"), eq(Visibility.PUBLIC.name()), eq(ORCID), isNull());
    }

    @Test
    public void addEmailPrimaryChangedReturnsOldAndNew() {
        SourceEntity source = new SourceEntity();
        source.setSourceProfile(new ProfileEntity(ORCID));
        when(sourceManager.retrieveActiveSourceEntity()).thenReturn(source);
        doReturn(email("old@orcid.org", true, Visibility.PUBLIC)).when(emailManager).findPrimaryEmail(ORCID);

        Map<String, String> result = emailManager.addEmail(ORCID, email("new@orcid.org", true, Visibility.PUBLIC));

        assertEquals("new@orcid.org", result.get("new"));
        assertEquals("old@orcid.org", result.get("old"));
    }

    @Test
    public void addEmailPrimaryUnchangedReturnsEmptyMap() {
        SourceEntity source = new SourceEntity();
        source.setSourceProfile(new ProfileEntity(ORCID));
        when(sourceManager.retrieveActiveSourceEntity()).thenReturn(source);
        doReturn(email("same@orcid.org", true, Visibility.PUBLIC)).when(emailManager).findPrimaryEmail(ORCID);

        Map<String, String> result = emailManager.addEmail(ORCID, email("same@orcid.org", true, Visibility.PUBLIC));

        assertTrue(result.isEmpty());
    }

    @Test
    public void hideAllEmailsDelegatesToDao() {
        when(emailDao.hideAllEmails("orcid")).thenReturn(true);

        assertTrue(emailManager.hideAllEmails("orcid"));
    }

    @Test
    public void updateVisibilityDelegatesToDao() {
        when(emailDao.updateVisibility("orcid", "a@b.com", Visibility.PRIVATE.name())).thenReturn(true);

        assertTrue(emailManager.updateVisibility("orcid", "a@b.com", Visibility.PRIVATE));
    }

    @Test
    public void setPrimaryUpdatesAndRecordsHistoryWithVerificationFlag() {
        doReturn(email("old@orcid.org", true, Visibility.PUBLIC)).when(emailManager).findPrimaryEmail("orcid");
        EmailEntity newPrimary = new EmailEntity();
        newPrimary.setEmail("new@orcid.org");
        newPrimary.setVerified(false);
        when(emailDao.findByEmail("new@orcid.org")).thenReturn(newPrimary);

        Map<String, String> result = emailManager.setPrimary("orcid", "new@orcid.org", request("127.0.0.1"));

        assertEquals("old@orcid.org", result.get("old"));
        assertEquals("new@orcid.org", result.get("new"));
        assertEquals("true", result.get("sendVerification"));
        verify(profileHistoryEventManager).recordEmailUpdateEvent("orcid", "127.0.0.1",
                "Primary email changed from old@orcid.org to new@orcid.org");
    }

    @Test
    public void setPrimaryUsesUnknownIpWhenRequestIsNull() {
        doReturn(email("old@orcid.org", true, Visibility.PUBLIC)).when(emailManager).findPrimaryEmail("orcid");
        EmailEntity newPrimary = new EmailEntity();
        newPrimary.setEmail("new@orcid.org");
        newPrimary.setVerified(true);
        when(emailDao.findByEmail("new@orcid.org")).thenReturn(newPrimary);

        emailManager.setPrimary("orcid", "new@orcid.org", null);

        verify(profileHistoryEventManager).recordEmailUpdateEvent("orcid", "unknown",
                "Primary email changed from old@orcid.org to new@orcid.org");
    }

    @Test
    public void setPrimaryNoOpWhenEmailMatchesCurrent() {
        doReturn(email("same@orcid.org", true, Visibility.PUBLIC)).when(emailManager).findPrimaryEmail("orcid");
        when(emailDao.findByEmail("same@orcid.org")).thenReturn(getEmailEntity("same@orcid.org", false, true, "orcid"));

        Map<String, String> result = emailManager.setPrimary("orcid", "same@orcid.org", request("127.0.0.1"));

        assertTrue(result.isEmpty());
        verify(emailDao, never()).updatePrimary(anyString(), anyString());
        verify(profileHistoryEventManager, never()).recordEmailUpdateEvent(anyString(), anyString(), anyString());
    }

    @Test
    public void setPrimaryNoOpWhenTargetNotFound() {
        doReturn(email("old@orcid.org", true, Visibility.PUBLIC)).when(emailManager).findPrimaryEmail("orcid");
        when(emailDao.findByEmail("missing@orcid.org")).thenReturn(null);

        Map<String, String> result = emailManager.setPrimary("orcid", "missing@orcid.org", request("127.0.0.1"));

        assertTrue(result.isEmpty());
        verify(emailDao, never()).updatePrimary(anyString(), anyString());
    }

    @Test
    public void editEmailPrimaryAddressChangeRecordsHistoryAndReturnsKeys() {
        EmailEntity original = getEmailEntity("old@orcid.org", true, true, "orcid");
        original.setId("old-hash");
        original.setVisibility("PRIVATE");
        when(emailDao.findByEmail("old@orcid.org")).thenReturn(original);

        Map<String, String> result = emailManager.editEmail("orcid", "old@orcid.org", "new@orcid.org", request("127.0.0.1"));

        assertEquals("old@orcid.org", result.get("old"));
        assertEquals("new@orcid.org", result.get("new"));
        assertEquals("new@orcid.org", result.get("verifyAddress"));
        verify(emailDao).remove("old-hash");
        verify(profileHistoryEventManager).recordEmailUpdateEvent("orcid", "127.0.0.1", "Email changed from old@orcid.org to new@orcid.org");
    }

    @Test
    public void editEmailSameValueDoesNotRecordHistory() {
        EmailEntity original = getEmailEntity("same@orcid.org", true, true, "orcid");
        original.setId("old-hash");
        original.setVisibility("PRIVATE");
        when(emailDao.findByEmail("same@orcid.org")).thenReturn(original);

        Map<String, String> result = emailManager.editEmail("orcid", "same@orcid.org", " same@orcid.org ", request("127.0.0.1"));

        assertEquals("same@orcid.org", result.get("verifyAddress"));
        verify(profileHistoryEventManager, never()).recordEmailUpdateEvent(anyString(), anyString(), anyString());
    }

    @Test
    public void editEmailUsesUnknownIpWhenRequestNull() {
        EmailEntity original = getEmailEntity("old@orcid.org", false, true, "orcid");
        original.setId("old-hash");
        original.setVisibility("PRIVATE");
        when(emailDao.findByEmail("old@orcid.org")).thenReturn(original);

        emailManager.editEmail("orcid", "old@orcid.org", "new@orcid.org", null);

        verify(profileHistoryEventManager).recordEmailUpdateEvent("orcid", "unknown", "Email changed from old@orcid.org to new@orcid.org");
    }

    @Test
    public void reactivatePrimaryEmailRejectsDifferentOrcid() {
        EmailEntity existing = getEmailEntity("e@x.com", false, false, "other");
        when(emailDao.find("hash:e@x.com")).thenReturn(existing);

        assertThrows(IllegalArgumentException.class, () -> emailManager.reactivatePrimaryEmail("orcid", "e@x.com"));
    }

    @Test
    public void reactivatePrimaryEmailRejectsConflictingAddress() {
        EmailEntity existing = getEmailEntity("different@x.com", false, false, "orcid");
        when(emailDao.find("hash:email@x.com")).thenReturn(existing);

        assertThrows(IllegalArgumentException.class, () -> emailManager.reactivatePrimaryEmail("orcid", "email@x.com"));
    }

    @Test
    public void reactivatePrimaryEmailPopulatesBlankAndMerges() {
        EmailEntity existing = getEmailEntity(null, false, false, "orcid");
        existing.setDateVerified(null);
        when(emailDao.find("hash:User@Test.com")).thenReturn(existing);

        emailManager.reactivatePrimaryEmail("orcid", "User@Test.com");

        assertEquals("User@Test.com", existing.getEmail());
        assertTrue(existing.getPrimary());
        assertTrue(existing.getVerified());
        assertNotNull(existing.getDateVerified());
        verify(emailDao).merge(existing);
        verify(emailDao).flush();
    }

    @Test
    public void clearEmailsAfterReactivationReturnsZeroForBlankOrcid() {
        assertEquals(Integer.valueOf(0), emailManager.clearEmailsAfterReactivation(" "));
        verify(emailDao, never()).clearEmailsAfterReactivation(anyString());
    }

    @Test
    public void clearEmailsAfterReactivationDelegates() {
        when(emailDao.clearEmailsAfterReactivation("orcid")).thenReturn(3);

        assertEquals(Integer.valueOf(3), emailManager.clearEmailsAfterReactivation("orcid"));
    }

    @Test
    public void reactivateOrCreateCreatesWhenMissing() {
        when(emailDao.find("hash:new@orcid.org")).thenReturn(null);

        assertTrue(emailManager.reactivateOrCreate("orcid", "new@orcid.org", Visibility.PUBLIC));
        verify(emailDao).addEmail("orcid", "new@orcid.org", "hash:new@orcid.org", Visibility.PUBLIC.name(), "orcid", null);
    }

    @Test
    public void reactivateOrCreateMergesWhenOwnedBySameOrcid() {
        EmailEntity existing = getEmailEntity("Old@Orcid.org", true, true, "orcid");
        when(emailDao.find("hash:New@Orcid.org")).thenReturn(existing);

        assertTrue(emailManager.reactivateOrCreate("orcid", "New@Orcid.org", Visibility.PRIVATE));
        assertEquals("New@Orcid.org", existing.getEmail());
        assertFalse(existing.getPrimary());
        assertFalse(existing.getVerified());
        assertEquals(Visibility.PRIVATE.name(), existing.getVisibility());
        verify(emailDao).merge(existing);
        verify(emailDao).flush();
    }

    @Test
    public void reactivateOrCreateRejectsEmailBelongingToAnotherRecord() {
        EmailEntity existing = getEmailEntity("x@x.com", false, false, "other");
        when(emailDao.find("hash:x@x.com")).thenReturn(existing);

        assertThrows(IllegalArgumentException.class, () -> emailManager.reactivateOrCreate("orcid", "x@x.com", Visibility.PUBLIC));
    }

    @Test
    public void removeUnclaimedEmailRejectsClaimedProfiles() {
        ProfileEntity claimed = new ProfileEntity();
        claimed.setClaimed(Boolean.TRUE);
        when(profileDao.find("orcid")).thenReturn(claimed);

        assertThrows(IllegalArgumentException.class, () -> emailManager.removeUnclaimedEmail("orcid", "e@x.com"));
        verify(emailDao, never()).removeEmail(anyString(), anyString());
    }

    @Test
    public void removeUnclaimedEmailRemovesForUnclaimedProfiles() {
        ProfileEntity unclaimed = new ProfileEntity();
        unclaimed.setClaimed(Boolean.FALSE);
        when(profileDao.find("orcid")).thenReturn(unclaimed);

        emailManager.removeUnclaimedEmail("orcid", "e@x.com");

        verify(emailDao).removeEmail("orcid", "e@x.com");
    }

    @Test
    public void removeEmailsRejectsNonAdmin() {
        when(orcidSecurityManager.isAdmin()).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> emailManager.removeEmails("orcid", singletonStrings("remove@x.com")));
    }

    @Test
    public void removeEmailsRejectsRemovingAllEmails() {
        when(orcidSecurityManager.isAdmin()).thenReturn(true);
        when(emailDao.findByOrcid(eq("orcid"), anyLong())).thenReturn(twoEmailEntities("a@x.com", "b@x.com"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> emailManager.removeEmails("orcid", twoStrings("a@x.com", "b@x.com")));
        assertEquals("Can't mark all user's as deleted", ex.getMessage());
    }

    @Test
    public void removeEmailsRemovesAndReturnsRemainingWithoutPrimaryChange() {
        when(orcidSecurityManager.isAdmin()).thenReturn(true);
        EmailEntity remove = getEmailEntity("remove@x.com", false, true, "orcid");
        EmailEntity keepPrimary = getEmailEntity("keep@x.com", true, true, "orcid");
        List<EmailEntity> before = twoEmailEntities(remove, keepPrimary);
        List<EmailEntity> after = singletonEmailEntities(keepPrimary);
        when(emailDao.findByOrcid(eq("orcid"), anyLong())).thenReturn(before).thenReturn(after);

        List<Email> result = emailManager.removeEmails("orcid", singletonStrings("remove@x.com"));

        assertEquals(1, result.size());
        assertEquals("keep@x.com", result.get(0).getEmail());
        verify(emailDao).removeEmail("orcid", "remove@x.com");
        verify(emailDao, never()).updatePrimary(anyString(), anyString());
        verify(profileEmailDomainManager).updateEmailDomains(eq("orcid"), isNull(), any(Emails.class));
    }

    @Test
    public void removeEmailsPromotesNewPrimaryWhenNeeded() {
        when(orcidSecurityManager.isAdmin()).thenReturn(true);
        EmailEntity oldPrimary = getEmailEntity("old@x.com", true, true, "orcid");
        EmailEntity keep = getEmailEntity("keep@x.com", false, true, "orcid");
        List<EmailEntity> before = twoEmailEntities(oldPrimary, keep);
        List<EmailEntity> after = singletonEmailEntities(keep);
        when(emailDao.findByOrcid(eq("orcid"), anyLong())).thenReturn(before).thenReturn(after);

        List<Email> result = emailManager.removeEmails("orcid", singletonStrings("old@x.com"));

        assertEquals(1, result.size());
        assertEquals("keep@x.com", result.get(0).getEmail());
        assertTrue(keep.getPrimary());
        verify(emailDao).updatePrimary("orcid", "keep@x.com");
    }

    @Test
    public void removeEmailsSendsRemainingEmailsToDomainUpdate() {
        when(orcidSecurityManager.isAdmin()).thenReturn(true);
        EmailEntity remove = getEmailEntity("remove@x.com", false, true, "orcid");
        EmailEntity keep = getEmailEntity("keep@x.com", true, true, "orcid");
        List<EmailEntity> before = twoEmailEntities(remove, keep);
        List<EmailEntity> after = singletonEmailEntities(keep);
        when(emailDao.findByOrcid(eq("orcid"), anyLong())).thenReturn(before).thenReturn(after);

        emailManager.removeEmails("orcid", singletonStrings("remove@x.com"));

        ArgumentCaptor<Emails> captor = ArgumentCaptor.forClass(Emails.class);
        verify(profileEmailDomainManager).updateEmailDomains(eq("orcid"), isNull(), captor.capture());
        assertEquals(1, captor.getValue().getEmails().size());
        assertEquals("keep@x.com", captor.getValue().getEmails().get(0).getEmail());
    }

    private MockHttpServletRequest request(String ip) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(ip);
        return request;
    }

    private Email email(String value, boolean primary, Visibility visibility) {
        Email email = new Email();
        email.setEmail(value);
        email.setPrimary(primary);
        email.setVisibility(visibility);
        return email;
    }

    private EmailEntity getEmailEntity(String email, boolean primary, boolean verified, String orcid) {
        EmailEntity entity = new EmailEntity();
        entity.setEmail(email);
        entity.setPrimary(primary);
        entity.setVerified(verified);
        entity.setOrcid(orcid);
        entity.setCurrent(true);
        entity.setVisibility(Visibility.PUBLIC.name());
        return entity;
    }

    private List<EmailEntity> singletonEmailEntities(String email) {
        List<EmailEntity> result = new ArrayList<>();
        result.add(getEmailEntity(email, false, true, "orcid"));
        return result;
    }

    private List<EmailEntity> singletonEmailEntities(EmailEntity email) {
        List<EmailEntity> result = new ArrayList<>();
        result.add(email);
        return result;
    }

    private List<EmailEntity> twoEmailEntities(String email1, String email2) {
        List<EmailEntity> result = new ArrayList<>();
        result.add(getEmailEntity(email1, false, true, "orcid"));
        result.add(getEmailEntity(email2, false, true, "orcid"));
        return result;
    }

    private List<EmailEntity> twoEmailEntities(EmailEntity e1, EmailEntity e2) {
        List<EmailEntity> result = new ArrayList<>();
        result.add(e1);
        result.add(e2);
        return result;
    }

    private List<String> singletonStrings(String value) {
        List<String> result = new ArrayList<>();
        result.add(value);
        return result;
    }

    private List<String> twoStrings(String one, String two) {
        List<String> result = new ArrayList<>();
        result.add(one);
        result.add(two);
        return result;
    }

    private List<Email> toEmailList(Collection<EmailEntity> entities) {
        List<Email> list = new ArrayList<>();
        if (entities == null) {
            return list;
        }
        for (EmailEntity entity : entities) {
            Email email = new Email();
            email.setEmail(entity.getEmail());
            email.setPrimary(Boolean.TRUE.equals(entity.getPrimary()));
            email.setVerified(Boolean.TRUE.equals(entity.getVerified()));
            list.add(email);
        }
        return list;
    }
}
