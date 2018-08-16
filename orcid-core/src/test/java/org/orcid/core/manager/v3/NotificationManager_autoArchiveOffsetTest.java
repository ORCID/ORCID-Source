package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.v3.rc1.notification.Notification;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
@Transactional
public class NotificationManager_autoArchiveOffsetTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml",
            "/data/BiographyEntityData.xml", "/data/NotificationEntityData.xml");
    
    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }
    
    @Before
    public void before() {
        TargetProxyHelper.injectIntoProxy(notificationManager, "notificationArchiveOffset", 3);
    }

    @Test
    public void archiveOffsetNotificationsTest() throws Exception {
        String orcid = "0000-0000-0000-0003";
        Integer unread = notificationManager.getUnreadCount(orcid);
        assertEquals(Integer.valueOf(14), unread);
        Integer archived = notificationManager.archiveOffsetNotifications();
        // It will archive 15 notifications:
        // 11 from 0000-0000-0000-0003
        // 3 from 4444-4444-4444-4441
        // 1 from 
        assertEquals(Integer.valueOf(15), archived);
        List<Notification> notifications = notificationManager.findByOrcid(orcid, false, 0, 100);
        assertEquals(3, notifications.size());
        assertEquals(Long.valueOf(1013), notifications.get(0).getPutCode());
        assertEquals(Long.valueOf(1011), notifications.get(1).getPutCode());
        assertEquals(Long.valueOf(1009), notifications.get(2).getPutCode());
    }  
        
}
