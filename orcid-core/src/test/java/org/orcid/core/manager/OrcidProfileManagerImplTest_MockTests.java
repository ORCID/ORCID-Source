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
package org.orcid.core.manager;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEventEntity;
import org.orcid.persistence.jpa.entities.EmailEventType;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class OrcidProfileManagerImplTest_MockTests {
    @Resource
    public OrcidProfileManager orcidProfileManager;
    
    @Mock
    public ProfileDao mockProfileDaoReadOnly;
    
    @Resource
    public ProfileDao profileDaoReadOnly;
    
    @Mock
    public EmailDao mockEmailDao;
    
    @Resource
    public EmailDao emailDao;
    
    @Mock
    public NotificationManager mockNotificationManager;
    
    @Resource
    public NotificationManager notificationManager;
    
    @Mock
    public GenericDao<EmailEventEntity, Long> mockEmailEventDao;
    
    @Resource
    public GenericDao<EmailEventEntity, Long> emailEventDao;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "profileDaoReadOnly", mockProfileDaoReadOnly);
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "emailDao", mockEmailDao);
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "notificationManager", mockNotificationManager);
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "emailEventDao", mockEmailEventDao);        
    }
    
    @After
    public void resetMocks() {
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "profileDaoReadOnly", profileDaoReadOnly);
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "emailDao", emailDao);
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "notificationManager", notificationManager);
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "emailEventDao", emailEventDao);        
    }
    
    @Test
    public void processUnverifiedEmails7DaysTest() {
        List<Pair<String, Date>> emails = new ArrayList<Pair<String, Date>>();
        Pair<String, Date> tooOld1 = Pair.of("tooOld1@test.orcid.org", LocalDateTime.now().minusDays(15).toDate());
        Pair<String, Date> tooOld2 = Pair.of("tooOld2@test.orcid.org", LocalDateTime.now().minusDays(20).toDate());
        Pair<String, Date> ok1 = Pair.of("ok1@test.orcid.org", LocalDateTime.now().minusDays(7).toDate());
        Pair<String, Date> ok2 = Pair.of("ok2@test.orcid.org", LocalDateTime.now().minusDays(14).toDate());
        emails.add(ok1);
        emails.add(ok2);
        emails.add(tooOld1);
        emails.add(tooOld2);
        
        when(mockProfileDaoReadOnly.findEmailsUnverfiedDays(Matchers.anyInt(), Matchers.anyInt(), Matchers.any())).thenReturn(emails).thenReturn(new ArrayList<Pair<String, Date>>());
        orcidProfileManager.processUnverifiedEmails7Days();
        verify(mockEmailEventDao, times(1)).persist(new EmailEventEntity("ok1@test.orcid.org", EmailEventType.VERIFY_EMAIL_7_DAYS_SENT));
        verify(mockEmailEventDao, times(1)).persist(new EmailEventEntity("ok2@test.orcid.org", EmailEventType.VERIFY_EMAIL_7_DAYS_SENT));
        verify(mockEmailEventDao, times(1)).persist(new EmailEventEntity("tooOld1@test.orcid.org", EmailEventType.VERIFY_EMAIL_TOO_OLD));
        verify(mockEmailEventDao, times(1)).persist(new EmailEventEntity("tooOld2@test.orcid.org", EmailEventType.VERIFY_EMAIL_TOO_OLD));
    }
}
