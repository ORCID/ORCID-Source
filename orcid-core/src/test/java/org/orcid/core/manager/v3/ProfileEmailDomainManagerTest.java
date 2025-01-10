package org.orcid.core.manager.v3;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.impl.ProfileEmailDomainManagerImpl;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.dao.EmailDomainDao;
import org.orcid.persistence.dao.ProfileEmailDomainDao;
import org.orcid.persistence.jpa.entities.EmailDomainEntity;
import org.orcid.persistence.jpa.entities.EmailDomainEntity.DomainCategory;
import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.ProfileEmailDomain;
import org.orcid.test.TargetProxyHelper;

public class ProfileEmailDomainManagerTest {
    @Mock
    private ProfileEmailDomainDao profileEmailDomainDaoMock;

    @Mock
    private ProfileEmailDomainDao profileEmailDomainDaoReadOnlyMock;

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManagerMock;

    @Mock
    private EmailDomainDao emailDomainDaoMock;

    ProfileEmailDomainManager pedm = new ProfileEmailDomainManagerImpl();


    private static final String ORCID = "0000-0000-0000-0001";
    private static final String ORCID_TWO = "0000-0000-0000-0002";
    private static final String EMAIL_DOMAIN = "orcid.org";
    private static final String EMAIL_DOMAIN_TWO = "email.com";
    private static final String EMAIL_DOMAIN_THREE = "domain.net";


    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(pedm, "profileEmailDomainDao", profileEmailDomainDaoMock);
        TargetProxyHelper.injectIntoProxy(pedm, "profileEmailDomainDaoReadOnly", profileEmailDomainDaoReadOnlyMock);
        TargetProxyHelper.injectIntoProxy(pedm, "emailDomainDao", emailDomainDaoMock);
        TargetProxyHelper.injectIntoProxy(pedm, "profileEntityCacheManager", profileEntityCacheManagerMock);

        ProfileEmailDomainEntity ped1 = new ProfileEmailDomainEntity();
        ProfileEmailDomainEntity ped2 = new ProfileEmailDomainEntity();
        ProfileEmailDomainEntity ped3 = new ProfileEmailDomainEntity();

        ped1.setEmailDomain(EMAIL_DOMAIN);
        ped1.setOrcid(ORCID);
        ped1.setDateCreated(new Date(124, 12, 12));
        ped1.setVisibility(Visibility.PUBLIC.value());

        ped2.setEmailDomain(EMAIL_DOMAIN_TWO);
        ped2.setOrcid(ORCID);
        ped2.setVisibility(Visibility.LIMITED.value());

        ped3.setEmailDomain(EMAIL_DOMAIN);
        ped3.setOrcid(ORCID_TWO);
        ped3.setDateCreated(new Date(124, 12, 30));
        ped3.setVisibility(Visibility.PUBLIC.value());

        when(profileEmailDomainDaoMock.findByEmailDomain(eq(ORCID), eq(EMAIL_DOMAIN))).thenReturn(ped1);
        when(profileEmailDomainDaoMock.findByEmailDomain(eq(ORCID), eq(EMAIL_DOMAIN_TWO))).thenReturn(ped2);
        when(profileEmailDomainDaoMock.findByEmailDomain(eq(ORCID_TWO), eq(EMAIL_DOMAIN))).thenReturn(ped3);
        when(profileEmailDomainDaoMock.findByEmailDomain(eq(ORCID), eq(EMAIL_DOMAIN_THREE))).thenReturn(null);

        when(profileEmailDomainDaoMock.findByOrcid(eq(ORCID))).thenReturn(List.of(ped1, ped2));
        when(profileEmailDomainDaoMock.findByOrcid(eq(ORCID_TWO))).thenReturn(List.of(ped3));

        when(profileEmailDomainDaoMock.findPublicEmailDomains(eq(ORCID))).thenReturn(List.of(ped1));
        when(profileEmailDomainDaoMock.findPublicEmailDomains(eq(ORCID_TWO))).thenReturn(List.of(ped2));

        when(profileEmailDomainDaoReadOnlyMock.addEmailDomain(eq(ORCID), eq(EMAIL_DOMAIN_TWO), eq(Visibility.LIMITED.value()))).thenReturn(ped2);

        when(profileEmailDomainDaoReadOnlyMock.updateVisibility(eq(ORCID), eq(EMAIL_DOMAIN_TWO), eq(Visibility.LIMITED.value()))).thenReturn(true);

        ProfileEntity profile = new ProfileEntity();
        profile.setActivitiesVisibilityDefault(Visibility.PUBLIC.value());
        when(profileEntityCacheManagerMock.retrieve(anyString())).thenReturn(profile);
    }

    @Test(expected = IllegalArgumentException.class)
    public void processDomain_nullOrcid() {
        pedm.processDomain(null, "email@orcid.org");
    }

    @Test(expected = IllegalArgumentException.class)
    public void processDomain_nullDomain() {
        pedm.processDomain(ORCID, null);
    }

    @Test
    public void processDomain_domainAlreadyAdded() {
        EmailDomainEntity professionalEmailDomain = new EmailDomainEntity();
        professionalEmailDomain.setCategory(DomainCategory.PROFESSIONAL);
        professionalEmailDomain.setEmailDomain(EMAIL_DOMAIN);
        when(emailDomainDaoMock.findByEmailDomain(eq(EMAIL_DOMAIN))).thenReturn(professionalEmailDomain);
        pedm.processDomain(ORCID, "email@orcid.org");
        verify(profileEmailDomainDaoMock, times(1)).findByEmailDomain(eq(ORCID), eq(EMAIL_DOMAIN));
        verify(profileEmailDomainDaoMock, never()).addEmailDomain(anyString(), anyString(), anyString());
    }

    @Test
    public void processDomain_doNotAddUnknownDomain() {
        when(emailDomainDaoMock.findByEmailDomain(eq(EMAIL_DOMAIN))).thenReturn(null);
        pedm.processDomain(ORCID, "email@orcid.org");
        verify(profileEmailDomainDaoMock, never()).findByEmailDomain(anyString(), anyString());
        verify(profileEmailDomainDaoMock, never()).addEmailDomain(anyString(), anyString(), anyString());
    }

    @Test
    public void processDomain_doNotAddPersonalDomain() {
        EmailDomainEntity professionalEmailDomain = new EmailDomainEntity();
        professionalEmailDomain.setCategory(DomainCategory.PERSONAL);
        professionalEmailDomain.setEmailDomain(EMAIL_DOMAIN);
        when(emailDomainDaoMock.findByEmailDomain(eq(EMAIL_DOMAIN))).thenReturn(professionalEmailDomain);
        pedm.processDomain(ORCID, "email@orcid.org");
        verify(profileEmailDomainDaoMock, never()).findByEmailDomain(anyString(), anyString());
        verify(profileEmailDomainDaoMock, never()).addEmailDomain(anyString(), anyString(), anyString());
    }

    @Test
    public void processDomain_addDomain() {
        EmailDomainEntity professionalEmailDomain = new EmailDomainEntity();
        professionalEmailDomain.setCategory(DomainCategory.PROFESSIONAL);
        professionalEmailDomain.setEmailDomain(EMAIL_DOMAIN_THREE);
        when(emailDomainDaoMock.findByEmailDomain(eq(EMAIL_DOMAIN_THREE))).thenReturn(professionalEmailDomain);
        pedm.processDomain(ORCID, "email@domain.net");
        verify(profileEmailDomainDaoMock, times(1)).findByEmailDomain(eq(ORCID), eq(EMAIL_DOMAIN_THREE));
        verify(profileEmailDomainDaoMock, times(1)).addEmailDomain(ORCID, EMAIL_DOMAIN_THREE, Visibility.PUBLIC.value());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateEmailDomains_nullOrcid() {
        pedm.updateEmailDomains(null, new org.orcid.pojo.ajaxForm.Emails());
    }

    @Test
    public void updateEmailDomains_updateVisibility() {
        org.orcid.pojo.ajaxForm.Emails emails = new org.orcid.pojo.ajaxForm.Emails();
        ProfileEmailDomain ed1 = new ProfileEmailDomain();
        ed1.setVisibility(Visibility.LIMITED.value());
        ed1.setValue(EMAIL_DOMAIN);
        ProfileEmailDomain ed2 = new ProfileEmailDomain();
        ed2.setVisibility(Visibility.PRIVATE.value());
        ed2.setValue(EMAIL_DOMAIN_TWO);
        emails.setEmailDomains(List.of(ed1, ed2));
        pedm.updateEmailDomains(ORCID, emails);
        verify(profileEmailDomainDaoMock, times(1)).updateVisibility(ORCID, EMAIL_DOMAIN, Visibility.LIMITED.value());
        verify(profileEmailDomainDaoMock, times(1)).updateVisibility(ORCID, EMAIL_DOMAIN_TWO, Visibility.PRIVATE.value());
        verify(profileEmailDomainDaoMock, never()).removeEmailDomain(anyString(), anyString());
    }

    @Test
    public void updateEmailDomains_makeNoChanges() {
        // Visibility setting is the same -- no change necessary
        org.orcid.pojo.ajaxForm.Emails emails = new org.orcid.pojo.ajaxForm.Emails();
        ProfileEmailDomain ed1 = new ProfileEmailDomain();
        ed1.setVisibility(Visibility.PUBLIC.value());
        ed1.setValue(EMAIL_DOMAIN);
        emails.setEmailDomains(List.of(ed1));
        pedm.updateEmailDomains(ORCID_TWO, emails);
        verify(profileEmailDomainDaoMock, never()).updateVisibility(anyString(), anyString(), anyString());
        verify(profileEmailDomainDaoMock, never()).removeEmailDomain(anyString(), anyString());
    }

    @Test
    public void updateEmailDomains_removeDomain() {
        org.orcid.pojo.ajaxForm.Emails emails = new org.orcid.pojo.ajaxForm.Emails();
        emails.setEmailDomains(List.of(new ProfileEmailDomain()));
        pedm.updateEmailDomains(ORCID, emails);
        verify(profileEmailDomainDaoMock, never()).updateVisibility(anyString(), anyString(), anyString());
        verify(profileEmailDomainDaoMock, times(1)).removeEmailDomain(ORCID, EMAIL_DOMAIN);
        verify(profileEmailDomainDaoMock, times(1)).removeEmailDomain(ORCID, EMAIL_DOMAIN_TWO);
    }
}