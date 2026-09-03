package org.orcid.core.manager.v3;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.common.util.AuthenticationUtils;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidCoreExceptionMapper;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.impl.OrcidSecurityManagerImpl;
import org.orcid.core.manager.v3.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.core.manager.v3.read_only.impl.PeerReviewManagerReadOnlyImpl;
import org.orcid.core.manager.v3.read_only.impl.ProfileFundingManagerReadOnlyImpl;
import org.orcid.core.manager.v3.read_only.impl.WorkManagerReadOnlyImpl;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Country;
import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.FamilyName;
import org.orcid.jaxb.model.v3.release.record.GivenNames;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.SourceAware;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Distinctions;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositions;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Memberships;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Qualifications;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Services;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * 
 * @author Will Simpson
 *
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public abstract class OrcidSecurityManagerTestBase {

    /**
     * The class under test. It is declared as the implementation rather than the
     * interface because {@code @InjectMocks} needs a concrete type; every call
     * site below and in the subclasses uses interface methods only.
     */
    @InjectMocks
    protected OrcidSecurityManagerImpl orcidSecurityManager = new OrcidSecurityManagerImpl();

    protected final String ORCID_1 = "0000-0000-0000-0001";
    protected final String ORCID_2 = "0000-0000-0000-0002";

    protected final String CLIENT_1 = "APP-0000000000000001";
    protected final String CLIENT_2 = "APP-0000000000000002";
    protected final String PUBLIC_CLIENT = "APP-0000000000000003";

    protected final String EXTID_1 = "extId1";
    protected final String EXTID_2 = "extId2";
    protected final String EXTID_3 = "extId3";
    protected final String EXTID_SHARED = "shared";

    /*
     * These three are NOT collaborators of the class under test. They are used
     * only by the create*(...) helpers at the bottom of this file, to build the
     * expected grouping of an activities summary. groupWorks/groupFundings/
     * groupPeerReviews each build a local group generator and read nothing that
     * is injected, so real instances are correct here and a mock would return
     * null and collapse every grouping assertion in the subclasses.
     */
    protected WorkManagerReadOnly workManagerReadOnly = new WorkManagerReadOnlyImpl(100);

    protected ProfileFundingManagerReadOnly profileFundingManagerReadOnly = new ProfileFundingManagerReadOnlyImpl();

    protected PeerReviewManagerReadOnly peerReviewManagerReadOnly = new PeerReviewManagerReadOnlyImpl();

    @Mock
    protected ProfileEntityCacheManager profileEntityCacheManager;

    @Mock
    protected ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Mock
    protected SourceManager sourceManager;

    @Mock
    protected OrcidCoreExceptionMapper orcidCoreExceptionMapper;

    @Mock
    protected OrcidUserDetailsService orcidUserDetailsService;

    @Mock
    protected SourceEntityUtils sourceEntityUtils;

    @Before
    public void before() {
        // @InjectMocks does not resolve @Value fields, so they are set here.
        // claimWaitPeriodDays in particular must stay non-zero: at 0,
        // DateUtils.olderThan(justCreatedDate, 0) is already true a millisecond
        // later, and the "record not claimed and not old enough" guard silently
        // stops throwing.
        ReflectionTestUtils.setField(orcidSecurityManager, "writeValiditySeconds", 3600);
        ReflectionTestUtils.setField(orcidSecurityManager, "claimWaitPeriodDays", 10);
        ReflectionTestUtils.setField(orcidSecurityManager, "baseUrl", "https://testserver.orcid.org");

        ClientDetailsEntity client1 = new ClientDetailsEntity();
        client1.setId(CLIENT_1);
        client1.setClientType(ClientType.CREATOR.name());

        ClientDetailsEntity client2 = new ClientDetailsEntity();
        client2.setId(CLIENT_2);
        client2.setClientType(ClientType.UPDATER.name());

        ClientDetailsEntity publicClient = new ClientDetailsEntity();
        publicClient.setId(PUBLIC_CLIENT);
        publicClient.setClientType(ClientType.PUBLIC_CLIENT.name());

        // One stub per client id, deliberately. A blanket any(String) stub
        // returning a non-public client would erase the public client guard and
        // make every testPublicClient_* test pass for the wrong reason.
        when(clientDetailsEntityCacheManager.retrieve(CLIENT_1)).thenReturn(client1);
        when(clientDetailsEntityCacheManager.retrieve(CLIENT_2)).thenReturn(client2);
        when(clientDetailsEntityCacheManager.retrieve(PUBLIC_CLIENT)).thenReturn(publicClient);

        // The acting client has to follow whatever SecurityContext the
        // individual test installed. This delegation is byte for byte what
        // SourceManagerImpl.retrieveActiveSourceId() does; a constant here would
        // decouple the acting client from the test's own setup.
        when(sourceManager.retrieveActiveSourceId()).thenAnswer(invocation -> AuthenticationUtils.retrieveActiveSourceId());

        // WorkBulk filtering replaces a rejected work with the mapped error, and
        // asserts on its type, so this must return a fresh non-null OrcidError.
        // Deliberately narrowed to the one throwable that actually reaches the
        // mapper from these tests: checkAndFilter wraps its per-element body in
        // catch (Exception), so a broad any(Throwable.class) stub would turn an
        // NPE from some future null collaborator into a value that still
        // satisfies `instanceof OrcidError` and the test would stay green while
        // proving nothing.
        when(orcidCoreExceptionMapper.getV3OrcidError(isA(OrcidVisibilityException.class)))
                .thenAnswer(invocation -> new OrcidError());
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
        
        waitAMillisecond(); // for testing purposes, let's avoid equal created dates
        try {
            work.setCreatedDate(new CreatedDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar())));
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
        
        ExternalID extId = new ExternalID();
        extId.setValue(extIdValue);
        ExternalIDs extIds = new ExternalIDs();
        extIds.getExternalIdentifier().add(extId);
        work.setExternalIdentifiers(extIds);
        addSharedExtId(extIds);
        setSource(work, sourceId);
        return work;
    }
    
    private void waitAMillisecond() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException("Thread shouldn't be interrupted", e);
        }
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
