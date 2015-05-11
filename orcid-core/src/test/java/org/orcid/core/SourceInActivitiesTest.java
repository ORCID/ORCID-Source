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
package org.orcid.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.jaxb.model.common.Title;
import org.orcid.jaxb.model.message.ActivitiesVisibilityDefault;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.SendChangeNotifications;
import org.orcid.jaxb.model.message.SendOrcidNews;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.WorkTitle;
import org.orcid.persistence.dao.ProfileWorkDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.DateUtils;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class SourceInActivitiesTest extends BaseTest {
    
    private static final String CLIENT_1_ID = "APP-5555555555555555";
    private static final String CLIENT_2_ID = "APP-6666666666666666";
    
    @Resource
    private OrcidProfileManager orcidProfileManager;
    
    @Resource
    private WorkManager workManager;
    
    @Resource
    private ProfileWorkManager profileWorkManager;
    
    @Resource
    private ProfileWorkDao profileWorkDao;
    
    @Mock
    private SourceManager sourceManager;      
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml"));
    }        
    
    @Before
    public void before() {
        profileWorkManager.setSourceManager(sourceManager);
    }
    
    @AfterClass
    public static void after() throws Exception {
        removeDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }
    
    @Test
    public void sourceDoesntChangeAfterCreationTest() {        
        OrcidProfile newUser = getMinimalOrcidProfile();
        String userOrcid = newUser.getOrcidIdentifier().getPath();
        ProfileWorkEntity work1 = getProfileWorkEntity(userOrcid, null);
        assertNotNull(work1);
        assertFalse(PojoUtil.isEmpty(work1.getWork().getTitle()));
        assertEquals(userOrcid, work1.getSource().getSourceId());
                        
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        ProfileWorkEntity work2 = getProfileWorkEntity(userOrcid, CLIENT_1_ID);
        assertNotNull(work2);
        assertFalse(PojoUtil.isEmpty(work2.getWork().getTitle()));
        assertEquals(CLIENT_1_ID, work2.getSource().getSourceId());
                     
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_2_ID)));
        ProfileWorkEntity work3 = getProfileWorkEntity(userOrcid, CLIENT_2_ID);
        assertNotNull(work3);
        assertFalse(PojoUtil.isEmpty(work3.getWork().getTitle()));
        assertEquals(CLIENT_2_ID, work3.getSource().getSourceId());
                                                        
        ProfileWorkEntity fromDb1 = profileWorkManager.getProfileWork(userOrcid, String.valueOf(work1.getWork().getId()));
        assertNotNull(fromDb1);
        assertEquals(userOrcid, fromDb1.getSource().getSourceId());
        
        ProfileWorkEntity fromDb2 = profileWorkManager.getProfileWork(userOrcid, String.valueOf(work2.getWork().getId()));
        assertNotNull(fromDb2);
        assertEquals(CLIENT_1_ID, fromDb2.getSource().getSourceId());
                        
        ProfileWorkEntity fromDb3 = profileWorkManager.getProfileWork(userOrcid, String.valueOf(work3.getWork().getId()));
        assertNotNull(fromDb3);
        assertEquals(CLIENT_2_ID, fromDb3.getSource().getSourceId());        
    }     
    
    private OrcidProfile getMinimalOrcidProfile() {
        OrcidProfile profile = new OrcidProfile();
        OrcidBio bio = new OrcidBio();
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.addOrReplacePrimaryEmail(new org.orcid.jaxb.model.message.Email(System.currentTimeMillis() + "@user.com"));
        Preferences preferences = new Preferences();
        preferences.setSendChangeNotifications(new SendChangeNotifications(true));
        preferences.setSendOrcidNews(new SendOrcidNews(true));
        preferences.setSendMemberUpdateRequests(true);
        preferences.setSendEmailFrequencyDays("1");
        preferences.setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.fromValue("public")));
        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setFamilyName(new FamilyName("First"));
        personalDetails.setGivenNames(new GivenNames("Last"));
        bio.setContactDetails(contactDetails);
        bio.setPersonalDetails(personalDetails);
        OrcidInternal internal = new OrcidInternal();
        internal.setPreferences(preferences);
        profile.setOrcidBio(bio);       
        profile.setOrcidInternal(internal);
        OrcidHistory orcidHistory = new OrcidHistory();
        orcidHistory.setClaimed(new Claimed(true));
        orcidHistory.setCreationMethod(CreationMethod.fromValue("integration-test"));
        profile.setOrcidHistory(orcidHistory);
        orcidHistory.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
        profile.setPassword("password1");
        return orcidProfileManager.createOrcidProfile(profile, false);
    }
    
    private ProfileWorkEntity getProfileWorkEntity(String userOrcid, String clientId) {        
        String workId = "";
        if(PojoUtil.isEmpty(clientId)) {
            WorkEntity workEntity = new WorkEntity();
            workEntity.setTitle("Work " + System.currentTimeMillis());
            workEntity.setWorkType(WorkType.BOOK);
            workEntity = workManager.addWork(workEntity);
            workId = String.valueOf(workEntity.getId());
            profileWorkManager.addProfileWork(userOrcid, workEntity.getId(), Visibility.PUBLIC, userOrcid);
        } else {
            Work work = new Work();
            WorkTitle title = new WorkTitle();
            title.setTitle(new Title("Work " + System.currentTimeMillis()));            
            work.setWorkTitle(title);
            work.setWorkType(org.orcid.jaxb.model.record.WorkType.BOOK);
            work = profileWorkManager.createWork(userOrcid, work);
            workId = work.getPutCode();
        }
        return profileWorkManager.getProfileWork(userOrcid, workId);
    }
}
