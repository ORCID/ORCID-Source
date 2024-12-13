package org.orcid.core.common.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.common.manager.impl.EmailDomainManagerImpl;
import org.orcid.core.common.manager.impl.EmailDomainManagerImpl.STATUS;
import org.orcid.persistence.dao.EmailDomainDao;
import org.orcid.persistence.jpa.entities.EmailDomainEntity;
import org.orcid.persistence.jpa.entities.EmailDomainEntity.DomainCategory;
import org.orcid.test.TargetProxyHelper;

public class EmailDomainManagerTest {
    @Mock
    private EmailDomainDao emailDomainDaoMock;

    @Mock
    private EmailDomainDao emailDomainDaoReadOnlyMock;

    EmailDomainManager edm = new EmailDomainManagerImpl();

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(edm, "emailDomainDao", emailDomainDaoMock);
        TargetProxyHelper.injectIntoProxy(edm, "emailDomainDaoReadOnly", emailDomainDaoReadOnlyMock);
        
        EmailDomainEntity e1 = new EmailDomainEntity("gmail.com", DomainCategory.PERSONAL);
        EmailDomainEntity e2 = new EmailDomainEntity("yahoo.com", DomainCategory.PERSONAL);
        EmailDomainEntity e3 = new EmailDomainEntity("orcid.org", DomainCategory.PROFESSIONAL, "https://ror.org/04fa4r544");
        e3.setId(1000L);
        
        when(emailDomainDaoReadOnlyMock.findByCategory(eq(DomainCategory.PERSONAL))).thenReturn(List.of(e1, e2));
        when(emailDomainDaoReadOnlyMock.findByCategory(eq(DomainCategory.PROFESSIONAL))).thenReturn(List.of(e3));
        
        when(emailDomainDaoReadOnlyMock.findByEmailDomain("gmail.com")).thenReturn(List.of(e1));
        when(emailDomainDaoReadOnlyMock.findByEmailDomain("orcid.org")).thenReturn(List.of(e3));

        when(emailDomainDaoMock.createEmailDomain(eq("new.domain.com"), eq(DomainCategory.PROFESSIONAL), eq("https://ror.org/0"))).thenReturn(new EmailDomainEntity("new.domain.com", DomainCategory.PROFESSIONAL, "https://ror.org/0"));
        when(emailDomainDaoMock.updateRorId(1000L, "https://ror.org/0")).thenReturn(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEmailDomain_NullDomainTest() {
        edm.createEmailDomain(null, DomainCategory.PROFESSIONAL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEmailDomain_EmptyDomainTest() {
        edm.createEmailDomain("        ", DomainCategory.PROFESSIONAL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEmailDomain_InvalidDomainTest() {
        edm.createEmailDomain("$$$", DomainCategory.PROFESSIONAL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEmailDomain_NullCategoryTest() {
        edm.createEmailDomain("orcid.org", null);
    }

    @Test
    public void createEmailDomainTest() {
        edm.createEmailDomain("orcid.org", DomainCategory.PROFESSIONAL);
        verify(emailDomainDaoMock, times(1)).createEmailDomain(eq("orcid.org"), eq(DomainCategory.PROFESSIONAL));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateCategory_nullCategoryTest() {
        edm.updateCategory(0, null);
    }
    
    @Test
    public void updateCategoryTest() {
        edm.updateCategory(0, DomainCategory.PERSONAL);
        verify(emailDomainDaoMock, times(1)).updateCategory(eq(Long.valueOf(0)), eq(DomainCategory.PERSONAL));
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByEmailDomain_NullDomainTest() {
        edm.findByEmailDomain(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByEmailDomain_EmptyDomainTest() {
        edm.findByEmailDomain("              ");
    }
    
    @Test
    public void findByEmailDomain_NothingFoundTest() {
        assert(edm.findByEmailDomain("other.com").isEmpty());
    }
    
    @Test
    public void findByEmailDomainTest() {
        List<EmailDomainEntity> ede = edm.findByEmailDomain("gmail.com");
        assertNotNull(ede);
        if(ede !=null) {
        assertEquals("gmail.com", ede.get(0).getEmailDomain());
        assertEquals(DomainCategory.PERSONAL, ede.get(0).getCategory());
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void findByCategory_NullCategoryTest() {
        edm.findByEmailDomain(null);
    }
    
    @Test
    public void findByCategory_NothingFoundTest() {
        assertTrue(edm.findByCategory(DomainCategory.UNDEFINED).isEmpty());
    }
    
    @Test
    public void findByCategory_OneResultTest() {
        List<EmailDomainEntity> personal = edm.findByCategory(DomainCategory.PROFESSIONAL);
        assertNotNull(personal);
        assertEquals(1, personal.size());
        assertEquals(DomainCategory.PROFESSIONAL, personal.get(0).getCategory());
        assertEquals("orcid.org", personal.get(0).getEmailDomain());
    }
    
    @Test
    public void findByCategory_TwoResultsTest() {
        List<EmailDomainEntity> personal = edm.findByCategory(DomainCategory.PERSONAL);
        assertNotNull(personal);
        assertEquals(2, personal.size());
        assertEquals(DomainCategory.PERSONAL, personal.get(0).getCategory());
        assertEquals("gmail.com", personal.get(0).getEmailDomain());
        assertEquals(DomainCategory.PERSONAL, personal.get(1).getCategory());
        assertEquals("yahoo.com", personal.get(1).getEmailDomain());
    }
    
    @Test
    public void createOrUpdateEmailDomain_CreateTest() {
        STATUS s = edm.createOrUpdateEmailDomain("new.domain.com", "https://ror.org/0");
        assertEquals(STATUS.CREATED, s);
        verify(emailDomainDaoMock, times(1)).createEmailDomain(eq("new.domain.com"), eq(DomainCategory.PROFESSIONAL), eq("https://ror.org/0"));
        verify(emailDomainDaoMock, never()).updateRorId(anyLong(), anyString());
    }
    
    @Test
    public void createOrUpdateEmailDomain_UpdateTest() {
        STATUS s = edm.createOrUpdateEmailDomain("orcid.org", "https://ror.org/0");
        assertEquals(STATUS.UPDATED, s);
        verify(emailDomainDaoMock, times(1)).updateRorId(eq(1000L), eq("https://ror.org/0"));
        verify(emailDomainDaoMock, never()).createEmailDomain(anyString(), any(), anyString());
    }
    
    @Test
    public void createOrUpdateEmailDomain_NoUpdatesTest() {
        STATUS s = edm.createOrUpdateEmailDomain("orcid.org", "https://ror.org/04fa4r544");
        assertNull(s);
        verify(emailDomainDaoMock, never()).updateRorId(anyLong(), anyString());
        verify(emailDomainDaoMock, never()).createEmailDomain(anyString(), any(), anyString());
    }
}
