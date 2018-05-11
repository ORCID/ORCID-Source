package org.orcid.core.manager.v3;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc1.common.Country;
import org.orcid.jaxb.model.v3.rc1.common.CreditName;
import org.orcid.jaxb.model.v3.rc1.common.Iso3166Country;
import org.orcid.jaxb.model.v3.rc1.common.Source;
import org.orcid.jaxb.model.v3.rc1.common.SourceClientId;
import org.orcid.jaxb.model.v3.rc1.common.Url;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.record.Address;
import org.orcid.jaxb.model.v3.rc1.record.Biography;
import org.orcid.jaxb.model.v3.rc1.record.Email;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc1.record.FamilyName;
import org.orcid.jaxb.model.v3.rc1.record.GivenNames;
import org.orcid.jaxb.model.v3.rc1.record.Keyword;
import org.orcid.jaxb.model.v3.rc1.record.Name;
import org.orcid.jaxb.model.v3.rc1.record.OtherName;
import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc1.record.SourceAware;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Distinctions;
import org.orcid.jaxb.model.v3.rc1.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Educations;
import org.orcid.jaxb.model.v3.rc1.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Employments;
import org.orcid.jaxb.model.v3.rc1.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Fundings;
import org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositions;
import org.orcid.jaxb.model.v3.rc1.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Memberships;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.rc1.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Qualifications;
import org.orcid.jaxb.model.v3.rc1.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Services;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Works;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Will Simpson
 *
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class OrcidSecurityManagerTestBase {

    @Resource(name = "orcidSecurityManagerV3")
    protected OrcidSecurityManager orcidSecurityManager;

    protected final String ORCID_1 = "0000-0000-0000-0001";
    protected final String ORCID_2 = "0000-0000-0000-0002";

    protected final String CLIENT_1 = "APP-0000000000000001";
    protected final String CLIENT_2 = "APP-0000000000000002";
    protected final String PUBLIC_CLIENT = "APP-0000000000000003";

    protected final String EXTID_1 = "extId1";
    protected final String EXTID_2 = "extId2";
    protected final String EXTID_3 = "extId3";
    protected final String EXTID_SHARED = "shared";

    @Resource(name = "workManagerReadOnlyV3")
    protected WorkManagerReadOnly workManagerReadOnly;

    @Resource(name = "profileFundingManagerReadOnlyV3")
    protected ProfileFundingManagerReadOnly profileFundingManagerReadOnly;

    @Resource(name = "peerReviewManagerReadOnlyV3")
    protected PeerReviewManagerReadOnly peerReviewManagerReadOnly;

    @Mock
    protected ProfileEntityCacheManager profileEntityCacheManager;

    @Mock
    protected ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "profileEntityCacheManager", profileEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "clientDetailsEntityCacheManager", clientDetailsEntityCacheManager);
        ProfileEntity p1 = new ProfileEntity();
        p1.setClaimed(true);
        p1.setId(ORCID_1);

        ProfileEntity p2 = new ProfileEntity();
        p2.setClaimed(true);
        p2.setId(ORCID_2);
        when(profileEntityCacheManager.retrieve(ORCID_1)).thenReturn(p1);
        when(profileEntityCacheManager.retrieve(ORCID_2)).thenReturn(p2);
        
        ClientDetailsEntity client1 = new ClientDetailsEntity();
        client1.setId(CLIENT_1);
        client1.setClientType(ClientType.CREATOR.name());
        
        ClientDetailsEntity client2 = new ClientDetailsEntity();
        client2.setId(CLIENT_2);
        client2.setClientType(ClientType.UPDATER.name());
        
        ClientDetailsEntity publicClient = new ClientDetailsEntity();
        publicClient.setId(PUBLIC_CLIENT);
        publicClient.setClientType(ClientType.PUBLIC_CLIENT.name());
        
        when(clientDetailsEntityCacheManager.retrieve(CLIENT_1)).thenReturn(client1);
        when(clientDetailsEntityCacheManager.retrieve(CLIENT_2)).thenReturn(client2);
        when(clientDetailsEntityCacheManager.retrieve(PUBLIC_CLIENT)).thenReturn(publicClient);
    }

    @After
    public void after() {
        SecurityContextTestUtils.setUpSecurityContextForAnonymous();
    }

    /**
     * Utilities
     */
    protected void assertItThrowOrcidAccessControlException(String orcid, ScopePathType s) {
        try {
            orcidSecurityManager.checkClientAccessAndScopes(orcid, s);
            fail();
        } catch (OrcidAccessControlException e) {
            return;
        } catch (Exception e) {
            fail();
        }
        fail();
    }

    protected void assertItThrowOrcidAccessControlException(ScopePathType s) {
        try {
            orcidSecurityManager.checkScopes(s);
            fail();
        } catch (OrcidAccessControlException e) {
            return;
        } catch (Exception e) {
            fail();
        }
        fail();
    }

    protected Name createName(Visibility v) {
        Name name = new Name();
        name.setVisibility(v);
        name.setCreditName(new CreditName("Credit Name"));
        name.setFamilyName(new FamilyName("Family Name"));
        name.setGivenNames(new GivenNames("Given Names"));
        return name;
    }

    protected Biography createBiography(Visibility v) {
        return new Biography("Biography", v);
    }

    protected Address createAddress(Visibility v, String sourceId) {
        Address a = new Address();
        a.setVisibility(v);
        Iso3166Country[] all = Iso3166Country.values();
        Random r = new Random();
        int index = r.nextInt(all.length);
        if (index < 0 || index >= all.length) {
            index = 0;
        }
        a.setCountry(new Country(all[index]));
        setSource(a, sourceId);
        return a;
    }

    protected OtherName createOtherName(Visibility v, String sourceId) {
        OtherName otherName = new OtherName();
        otherName.setContent("other-name-" + System.currentTimeMillis());
        otherName.setVisibility(v);
        setSource(otherName, sourceId);
        return otherName;
    }

    protected PersonExternalIdentifier createPersonExternalIdentifier(Visibility v, String sourceId) {
        PersonExternalIdentifier p = new PersonExternalIdentifier();
        p.setValue("ext-id-" + System.currentTimeMillis());
        p.setVisibility(v);
        setSource(p, sourceId);
        return p;
    }

    protected ResearcherUrl createResearcherUrl(Visibility v, String sourceId) {
        ResearcherUrl r = new ResearcherUrl();
        r.setUrl(new Url("http://orcid.org/test/" + System.currentTimeMillis()));
        r.setVisibility(v);
        setSource(r, sourceId);
        return r;
    }

    protected Email createEmail(Visibility v, String sourceId) {
        Email email = new Email();
        email.setEmail("test-email-" + System.currentTimeMillis() + "@test.orcid.org");
        email.setVisibility(v);
        setSource(email, sourceId);
        return email;
    }

    protected Keyword createKeyword(Visibility v, String sourceId) {
        Keyword k = new Keyword();
        k.setContent("keyword-" + System.currentTimeMillis());
        k.setVisibility(v);
        setSource(k, sourceId);
        return k;
    }

    protected Work createWork(Visibility v, String sourceId) {
        Work work = new Work();
        work.setVisibility(v);
        setSource(work, sourceId);
        return work;
    }

    protected WorkSummary createWorkSummary(Visibility v, String sourceId, String extIdValue) {
        WorkSummary work = new WorkSummary();
        work.setVisibility(v);
        ExternalID extId = new ExternalID();
        extId.setValue(extIdValue);
        ExternalIDs extIds = new ExternalIDs();
        extIds.getExternalIdentifier().add(extId);
        work.setExternalIdentifiers(extIds);
        addSharedExtId(extIds);
        setSource(work, sourceId);
        return work;
    }

    protected Works createWorks(WorkSummary... elements) {
        return workManagerReadOnly.groupWorks(new ArrayList<WorkSummary>(Arrays.asList(elements)), false);
    }

    protected FundingSummary createFundingSummary(Visibility v, String sourceId, String extIdValue) {
        FundingSummary f = new FundingSummary();
        f.setVisibility(v);
        setSource(f, sourceId);
        ExternalID extId = new ExternalID();
        extId.setValue(extIdValue);
        ExternalIDs extIds = new ExternalIDs();
        extIds.getExternalIdentifier().add(extId);
        addSharedExtId(extIds);
        f.setExternalIdentifiers(extIds);
        return f;
    }

    protected Fundings createFundings(FundingSummary... elements) {
        return profileFundingManagerReadOnly.groupFundings(new ArrayList<FundingSummary>(Arrays.asList(elements)), false);
    }

    protected PeerReviewSummary createPeerReviewSummary(Visibility v, String sourceId, String extIdValue) {
        PeerReviewSummary p = new PeerReviewSummary();
        p.setVisibility(v);
        p.setGroupId(EXTID_SHARED);
        setSource(p, sourceId);
        ExternalID extId = new ExternalID();
        extId.setValue(extIdValue);
        ExternalIDs extIds = new ExternalIDs();
        extIds.getExternalIdentifier().add(extId);
        addSharedExtId(extIds);
        p.setExternalIdentifiers(extIds);
        return p;
    }

    protected PeerReviews createPeerReviews(PeerReviewSummary... elements) {
        return peerReviewManagerReadOnly.groupPeerReviews(new ArrayList<PeerReviewSummary>(Arrays.asList(elements)), false);
    }
    
    protected DistinctionSummary createDistinctionSummary(Visibility v, String sourceId) {
        DistinctionSummary e = new DistinctionSummary();
        e.setVisibility(v);
        setSource(e, sourceId);
        return e;
    }
    
    protected Distinctions createDistinctions(DistinctionSummary... elements) {
        Distinctions e = new Distinctions();
        for (DistinctionSummary s : elements) {
            AffiliationGroup<DistinctionSummary> group = new AffiliationGroup<>();
            group.getActivities().add(s);
            e.retrieveGroups().add(group);
        }
        return e;
    }
    
    protected EducationSummary createEducationSummary(Visibility v, String sourceId) {
        EducationSummary e = new EducationSummary();
        e.setVisibility(v);
        setSource(e, sourceId);
        return e;
    }
    
    protected Educations createEducations(EducationSummary... elements) {
        Educations e = new Educations();
        for (EducationSummary s : elements) {
            AffiliationGroup<EducationSummary> group = new AffiliationGroup<>();
            group.getActivities().add(s);
            e.retrieveGroups().add(group);
        }
        return e;
    }

    protected EmploymentSummary createEmploymentSummary(Visibility v, String sourceId) {
        EmploymentSummary e = new EmploymentSummary();
        e.setVisibility(v);
        setSource(e, sourceId);
        return e;
    }

    protected Employments createEmployments(EmploymentSummary... elements) {
        Employments e = new Employments();
        for (EmploymentSummary s : elements) {
            AffiliationGroup<EmploymentSummary> group = new AffiliationGroup<>();
            group.getActivities().add(s);
            e.retrieveGroups().add(group);
        }
        return e;
    }

    protected InvitedPositionSummary createInvitedPositionSummary(Visibility v, String sourceId) {
        InvitedPositionSummary e = new InvitedPositionSummary();
        e.setVisibility(v);
        setSource(e, sourceId);
        return e;
    }
    
    protected InvitedPositions createInvitedPositions(InvitedPositionSummary... elements) {
        InvitedPositions e = new InvitedPositions();
        for (InvitedPositionSummary s : elements) {
            AffiliationGroup<InvitedPositionSummary> group = new AffiliationGroup<>();
            group.getActivities().add(s);
            e.retrieveGroups().add(group);
        }
        return e;
    }
    
    protected MembershipSummary createMembershipSummary(Visibility v, String sourceId) {
        MembershipSummary e = new MembershipSummary();
        e.setVisibility(v);
        setSource(e, sourceId);
        return e;
    }
    
    protected Memberships createMemberships(MembershipSummary... elements) {
        Memberships e = new Memberships();
        for (MembershipSummary s : elements) {
            AffiliationGroup<MembershipSummary> group = new AffiliationGroup<>();
            group.getActivities().add(s);
            e.retrieveGroups().add(group);
        }
        return e;
    }
    
    protected QualificationSummary createQualificationSummary(Visibility v, String sourceId) {
        QualificationSummary e = new QualificationSummary();
        e.setVisibility(v);
        setSource(e, sourceId);
        return e;
    }
    
    protected Qualifications createQualifications(QualificationSummary... elements) {
        Qualifications e = new Qualifications();
        for (QualificationSummary s : elements) {
            AffiliationGroup<QualificationSummary> group = new AffiliationGroup<>();
            group.getActivities().add(s);
            e.retrieveGroups().add(group);
        }
        return e;
    }
    
    protected ServiceSummary createServiceSummary(Visibility v, String sourceId) {
        ServiceSummary e = new ServiceSummary();
        e.setVisibility(v);
        setSource(e, sourceId);
        return e;
    }
    
    protected Services createServices(ServiceSummary... elements) {
        Services e = new Services();
        for (ServiceSummary s : elements) {
            AffiliationGroup<ServiceSummary> group = new AffiliationGroup<>();
            group.getActivities().add(s);
            e.retrieveGroups().add(group);
        }
        return e;
    }
    
    protected void addSharedExtId(ExternalIDs extIds) {
        ExternalID extId = new ExternalID();
        extId.setValue(EXTID_SHARED);
        extIds.getExternalIdentifier().add(extId);
    }

    protected ExternalID getExtId(String value) {
        ExternalID extId = new ExternalID();
        extId.setValue(value);
        return extId;
    }

    protected ExternalID getExtId(String value, String type) {
        ExternalID extId = new ExternalID();
        extId.setValue(value);
        extId.setType(type);
        return extId;
    }

    protected void setSource(SourceAware element, String sourceId) {
        Source source = new Source();
        source.setSourceClientId(new SourceClientId(sourceId));
        element.setSource(source);
    }
}
