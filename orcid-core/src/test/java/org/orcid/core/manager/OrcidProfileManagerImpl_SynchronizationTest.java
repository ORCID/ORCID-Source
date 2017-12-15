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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.jaxb.model.message.ActivitiesVisibilityDefault;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.DateUtils;

/**
 * @author Will Simpson
 */
public class OrcidProfileManagerImpl_SynchronizationTest extends OrcidProfileManagerBaseTest {
    private static final String MEMBER_ID = "0000-0000-0000-0000";
    private static final String CLIENT_1 = "0000-0000-0000-0001";
    private static final String CLIENT_2 = "0000-0000-0000-0002";
    private static final String TEST_ORCID = "0000-0000-0000-0001";

    @Resource
    private Jaxb2JpaAdapter jaxb2JpaAdapter;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Resource
    private WorkManager workManager;

    @Resource
    private SourceManager sourceManager;
    
    @Resource
    private OrcidJaxbCopyManager orcidJaxbCopyManager;

    @Mock
    private SourceManager mockSourceManager;

    @Mock
    private SourceManager anotherMockSourceManager;

    private static boolean init = false;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(jaxb2JpaAdapter, "sourceManager", mockSourceManager);
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "sourceManager", mockSourceManager);        
        TargetProxyHelper.injectIntoProxy(orcidJaxbCopyManager, "sourceManager", mockSourceManager);
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceClient(new ClientDetailsEntity(CLIENT_1));
        when(mockSourceManager.retrieveSourceEntity()).thenReturn(sourceEntity);

        SourceEntity sourceEntity2 = new SourceEntity();
        sourceEntity2.setSourceClient(new ClientDetailsEntity(CLIENT_2));
        when(anotherMockSourceManager.retrieveSourceEntity()).thenReturn(sourceEntity2);

        if (!init) {
            orcidProfileManager.setCompareWorksUsingScopusWay(true);

            OrcidProfile applicationProfile = new OrcidProfile();
            applicationProfile.setOrcidIdentifier(MEMBER_ID);
            OrcidBio applicationBio = new OrcidBio();
            applicationProfile.setOrcidBio(applicationBio);
            PersonalDetails applicationPersonalDetails = new PersonalDetails();
            applicationBio.setPersonalDetails(applicationPersonalDetails);
            applicationPersonalDetails.setCreditName(new CreditName("ORCID TEST"));
            orcidProfileManager.createOrcidProfile(applicationProfile, false, false);

            ClientDetailsEntity clientDetails = new ClientDetailsEntity();
            clientDetails.setId(CLIENT_1);
            clientDetails.setGroupProfileId(MEMBER_ID);
            clientDetailsManager.merge(clientDetails);

            ClientDetailsEntity clientDetails2 = new ClientDetailsEntity();
            clientDetails2.setId(CLIENT_2);
            clientDetails.setGroupProfileId(MEMBER_ID);
            clientDetailsManager.merge(clientDetails2);

            OrcidProfile profile1 = createBasicProfile();
            profile1.setOrcidActivities(null);
            // Change the orcid identifier
            profile1.setOrcidIdentifier(TEST_ORCID);
            profile1.setOrcidHistory(new OrcidHistory());
            profile1.getOrcidHistory().setClaimed(new Claimed(true));
            profile1.getOrcidHistory().setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis())));
            // Change the email address
            profile1.getOrcidBio().getContactDetails().retrievePrimaryEmail().setValue(TEST_ORCID + "@test.com");
            profile1 = orcidProfileManager.createOrcidProfile(profile1, false, false);

            ProfileEntity persisted = profileDao.find(TEST_ORCID);
            assertNotNull(persisted.getHashedOrcid());

            init = true;
        }
        // Set the default visibility to PRIVATE
        OrcidProfile profile1 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        profile1.getOrcidInternal().getPreferences().setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.PRIVATE));
        orcidProfileManager.updatePreferences(TEST_ORCID, profile1.getOrcidInternal().getPreferences());        
    }

    @After
    public void after() {
        workManager.removeAllWorks(TEST_ORCID);
        TargetProxyHelper.injectIntoProxy(jaxb2JpaAdapter, "sourceManager", sourceManager);
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "sourceManager", sourceManager);
        TargetProxyHelper.injectIntoProxy(orcidJaxbCopyManager, "sourceManager", sourceManager);        
    }

    @Test
    public void multipleThreadsAddingWorksTest() {
        fail();
    }
}
