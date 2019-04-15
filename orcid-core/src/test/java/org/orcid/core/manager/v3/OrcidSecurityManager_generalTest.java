package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.PersonalDetails;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidSecurityManager_generalTest extends OrcidSecurityManagerTestBase {

    /**
     * =================== public client tests ===================
     */
    @Test(expected = OrcidUnauthorizedException.class)
    public void testPublicClient_checkAndFilter_ActivitiesSummary() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, PUBLIC_CLIENT, ScopePathType.READ_PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_1, new ActivitiesSummary());
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testPublicClient_checkAndFilter_Collection() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, PUBLIC_CLIENT, ScopePathType.READ_PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_1, new ArrayList<Work>(), ScopePathType.READ_LIMITED);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testPublicClient_checkAndFilter_Person() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, PUBLIC_CLIENT, ScopePathType.READ_PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_1, new Person());
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testPublicClient_checkAndFilter_PersonalDetails() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, PUBLIC_CLIENT, ScopePathType.READ_PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_1, new PersonalDetails());
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testPublicClient_checkAndFilter_Record() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, PUBLIC_CLIENT, ScopePathType.READ_PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_1, new Record());
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testPublicClient_checkAndFilter_VisibilityType() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, PUBLIC_CLIENT, ScopePathType.READ_PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_1, new Work(), ScopePathType.READ_LIMITED);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testPublicClient_checkClientAccessAndScopes() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, PUBLIC_CLIENT, ScopePathType.READ_PUBLIC);
        orcidSecurityManager.checkClientAccessAndScopes(ORCID_1, ScopePathType.READ_LIMITED);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testPublicClient_checkScopes() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, PUBLIC_CLIENT, ScopePathType.READ_PUBLIC);
        orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
    }
    
    /**
     * =================== checkScopes test's ===================
     */
    @Test
    public void testCheckScopes_ReadPublic() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC);
    }

    @Test
    public void testCheckScopes_Authenticate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AUTHENTICATE);
        assertCheckScopesFailForOtherScopes(ScopePathType.AUTHENTICATE, ScopePathType.READ_PUBLIC);
    }

    @Test
    public void testCheckScopes_AffiliationsReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_READ_LIMITED);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.AFFILIATIONS_READ_LIMITED);
    }

    @Test
    public void testCheckScopes_AffiliationsCreate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_CREATE);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.AFFILIATIONS_CREATE);
    }

    @Test
    public void testCheckScopes_AffiliationsUpdate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_UPDATE);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.AFFILIATIONS_UPDATE);
    }

    @Test
    public void testCheckScopes_FundingReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.FUNDING_READ_LIMITED);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.FUNDING_READ_LIMITED);
    }

    @Test
    public void testCheckScopes_FundingCreate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.FUNDING_CREATE);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.FUNDING_CREATE);
    }

    @Test
    public void testCheckScopes_FundingUpdate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.FUNDING_UPDATE);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.FUNDING_UPDATE);
    }

    @Test
    public void testCheckScopes_PatentsReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PATENTS_READ_LIMITED);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ORCID_PATENTS_READ_LIMITED);
    }

    @Test
    public void testCheckScopes_PatentsCreate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PATENTS_CREATE);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ORCID_PATENTS_CREATE);
    }

    @Test
    public void testCheckScopes_PatentsUpdate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PATENTS_UPDATE);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ORCID_PATENTS_UPDATE);
    }

    @Test
    public void testCheckScopes_PeerReviewReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PEER_REVIEW_READ_LIMITED);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.PEER_REVIEW_READ_LIMITED);
    }

    @Test
    public void testCheckScopes_PeerReviewCreate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PEER_REVIEW_CREATE);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.PEER_REVIEW_CREATE);
    }

    @Test
    public void testCheckScopes_PeerReviewUpdate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PEER_REVIEW_UPDATE);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.PEER_REVIEW_UPDATE);
    }

    @Test
    public void testCheckScopes_OrcidWorksReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ORCID_WORKS_READ_LIMITED);
    }

    @Test
    public void testCheckScopes_OrcidWorksCreate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_CREATE);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ORCID_WORKS_CREATE);
    }

    @Test
    public void testCheckScopes_OrcidWorksUpdate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_UPDATE);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ORCID_WORKS_UPDATE);
    }

    @Test
    public void testCheckScopes_ActivitiesReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.AFFILIATIONS_READ_LIMITED,
                ScopePathType.FUNDING_READ_LIMITED, ScopePathType.ORCID_PATENTS_READ_LIMITED, ScopePathType.PEER_REVIEW_READ_LIMITED,
                ScopePathType.ORCID_WORKS_READ_LIMITED);
    }

    @Test
    public void testCheckScopes_ActivitiesUpdate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_UPDATE);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ACTIVITIES_UPDATE, ScopePathType.ORCID_WORKS_UPDATE,
                ScopePathType.ORCID_WORKS_CREATE, ScopePathType.FUNDING_UPDATE, ScopePathType.FUNDING_CREATE, ScopePathType.AFFILIATIONS_UPDATE,
                ScopePathType.AFFILIATIONS_CREATE, ScopePathType.ORCID_PATENTS_CREATE, ScopePathType.ORCID_PATENTS_UPDATE, ScopePathType.PEER_REVIEW_UPDATE,
                ScopePathType.PEER_REVIEW_CREATE, ScopePathType.ACTIVITIES_UPDATE);
    }

    @Test
    public void testCheckScopes_OrcidProfileReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PROFILE_READ_LIMITED);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.AFFILIATIONS_READ_LIMITED,
                ScopePathType.FUNDING_READ_LIMITED, ScopePathType.ORCID_PATENTS_READ_LIMITED, ScopePathType.PEER_REVIEW_READ_LIMITED,
                ScopePathType.ORCID_WORKS_READ_LIMITED, ScopePathType.ORCID_BIO_READ_LIMITED, ScopePathType.PERSON_READ_LIMITED, ScopePathType.READ_LIMITED,
                ScopePathType.ORCID_PROFILE_READ_LIMITED);
    }

    @Test
    public void testCheckScopes_ReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_LIMITED);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.AFFILIATIONS_READ_LIMITED,
                ScopePathType.FUNDING_READ_LIMITED, ScopePathType.ORCID_PATENTS_READ_LIMITED, ScopePathType.PEER_REVIEW_READ_LIMITED,
                ScopePathType.ORCID_WORKS_READ_LIMITED, ScopePathType.ORCID_BIO_READ_LIMITED, ScopePathType.PERSON_READ_LIMITED, ScopePathType.READ_LIMITED);
    }

    @Test
    public void testCheckScopes_OrcidBioUpdate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ORCID_BIO_UPDATE, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
    }

    @Test
    public void testCheckScopes_OrcidBioReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @Test
    public void testCheckScopes_OrcidBioExternalIdentifiersCreate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
        assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
    }

    private void assertCheckScopesFailForOtherScopes(ScopePathType... goodOnes) {
        List<ScopePathType> list = Arrays.asList(goodOnes);
        for (ScopePathType s : ScopePathType.values()) {
            if (!list.contains(s)) {
                assertItThrowOrcidAccessControlException(s);
            } else {
                orcidSecurityManager.checkScopes(s);
            }
        }
    }

    /**
     * =================== checkClientAccessAndScopes test's ===================
     */
    @Test(expected = OrcidUnauthorizedException.class)
    public void testCheckClientAccessAndScopes_When_TokenIsForOtherUser() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
        orcidSecurityManager.checkClientAccessAndScopes(ORCID_2, ScopePathType.ORCID_BIO_UPDATE);
        fail();
    }

    @Test
    public void testCheckClientAccessAndScopes_ReadPublic() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC);
    }

    @Test
    public void testCheckClientAccessAndScopes_Authenticate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AUTHENTICATE);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.AUTHENTICATE, ScopePathType.READ_PUBLIC);
    }

    @Test
    public void testCheckClientAccessAndScopes_AffiliationsReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_READ_LIMITED);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.AFFILIATIONS_READ_LIMITED);
    }

    @Test
    public void testCheckClientAccessAndScopes_AffiliationsCreate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_CREATE);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.AFFILIATIONS_CREATE);
    }

    @Test
    public void testCheckClientAccessAndScopes_AffiliationsUpdate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_UPDATE);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.AFFILIATIONS_UPDATE);
    }

    @Test
    public void testCheckClientAccessAndScopes_FundingReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.FUNDING_READ_LIMITED);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.FUNDING_READ_LIMITED);
    }

    @Test
    public void testCheckClientAccessAndScopes_FundingCreate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.FUNDING_CREATE);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.FUNDING_CREATE);
    }

    @Test
    public void testCheckClientAccessAndScopes_FundingUpdate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.FUNDING_UPDATE);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.FUNDING_UPDATE);
    }

    @Test
    public void testCheckClientAccessAndScopes_PatentsReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PATENTS_READ_LIMITED);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.ORCID_PATENTS_READ_LIMITED);
    }

    @Test
    public void testCheckClientAccessAndScopes_PatentsCreate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PATENTS_CREATE);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.ORCID_PATENTS_CREATE);
    }

    @Test
    public void testCheckClientAccessAndScopes_PatentsUpdate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PATENTS_UPDATE);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.ORCID_PATENTS_UPDATE);
    }

    @Test
    public void testCheckClientAccessAndScopes_PeerReviewReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PEER_REVIEW_READ_LIMITED);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.PEER_REVIEW_READ_LIMITED);
    }

    @Test
    public void testCheckClientAccessAndScopes_PeerReviewCreate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PEER_REVIEW_CREATE);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.PEER_REVIEW_CREATE);
    }

    @Test
    public void testCheckClientAccessAndScopes_PeerReviewUpdate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PEER_REVIEW_UPDATE);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.PEER_REVIEW_UPDATE);
    }

    @Test
    public void testCheckClientAccessAndScopes_OrcidWorksReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.ORCID_WORKS_READ_LIMITED);
    }

    @Test
    public void testCheckClientAccessAndScopes_OrcidWorksCreate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_CREATE);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.ORCID_WORKS_CREATE);
    }

    @Test
    public void testCheckClientAccessAndScopes_OrcidWorksUpdate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_UPDATE);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.ORCID_WORKS_UPDATE);
    }

    @Test
    public void testCheckClientAccessAndScopes_ActivitiesReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.ACTIVITIES_READ_LIMITED,
                ScopePathType.AFFILIATIONS_READ_LIMITED, ScopePathType.FUNDING_READ_LIMITED, ScopePathType.ORCID_PATENTS_READ_LIMITED,
                ScopePathType.PEER_REVIEW_READ_LIMITED, ScopePathType.ORCID_WORKS_READ_LIMITED);
    }

    @Test
    public void testCheckClientAccessAndScopes_ActivitiesUpdate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_UPDATE);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.ACTIVITIES_UPDATE, ScopePathType.ORCID_WORKS_UPDATE,
                ScopePathType.ORCID_WORKS_CREATE, ScopePathType.FUNDING_UPDATE, ScopePathType.FUNDING_CREATE, ScopePathType.AFFILIATIONS_UPDATE,
                ScopePathType.AFFILIATIONS_CREATE, ScopePathType.ORCID_PATENTS_CREATE, ScopePathType.ORCID_PATENTS_UPDATE, ScopePathType.PEER_REVIEW_UPDATE,
                ScopePathType.PEER_REVIEW_CREATE, ScopePathType.ACTIVITIES_UPDATE);
    }

    @Test
    public void testCheckClientAccessAndScopes_OrcidProfileReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PROFILE_READ_LIMITED);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.ACTIVITIES_READ_LIMITED,
                ScopePathType.AFFILIATIONS_READ_LIMITED, ScopePathType.FUNDING_READ_LIMITED, ScopePathType.ORCID_PATENTS_READ_LIMITED,
                ScopePathType.PEER_REVIEW_READ_LIMITED, ScopePathType.ORCID_WORKS_READ_LIMITED, ScopePathType.ORCID_BIO_READ_LIMITED, ScopePathType.PERSON_READ_LIMITED,
                ScopePathType.READ_LIMITED, ScopePathType.ORCID_PROFILE_READ_LIMITED);
    }

    @Test
    public void testCheckClientAccessAndScopes_ReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_LIMITED);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.ACTIVITIES_READ_LIMITED,
                ScopePathType.AFFILIATIONS_READ_LIMITED, ScopePathType.FUNDING_READ_LIMITED, ScopePathType.ORCID_PATENTS_READ_LIMITED,
                ScopePathType.PEER_REVIEW_READ_LIMITED, ScopePathType.ORCID_WORKS_READ_LIMITED, ScopePathType.ORCID_BIO_READ_LIMITED, ScopePathType.PERSON_READ_LIMITED,
                ScopePathType.READ_LIMITED);
    }

    @Test
    public void testCheckClientAccessAndScopes_OrcidBioUpdate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.ORCID_BIO_UPDATE,
                ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
    }

    @Test
    public void testCheckClientAccessAndScopes_OrcidBioReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @Test
    public void testCheckClientAccessAndScopes_OrcidBioExternalIdentifiersCreate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
        assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
    }

    private void assertCheckClientAccessAndScopesFailForOtherScopes(String orcid, ScopePathType... goodOnes) {
        List<ScopePathType> list = Arrays.asList(goodOnes);
        for (ScopePathType s : ScopePathType.values()) {
            if (!list.contains(s)) {
                assertItThrowOrcidAccessControlException(orcid, s);
            } else {
                orcidSecurityManager.checkClientAccessAndScopes(orcid, s);
            }
        }
    }

    /**
     * =================== checkAndFilter test's ===================
     */
    // ---- ELEMENTS WITHOUT SOURCE ----
    // Name element tests
    @Test
    public void testName_CanRead_When_ReadPublicToken_IsPublic() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void testName_CantRead_When_ReadPublicToken_IsLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.LIMITED);
        orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
        fail();
    }

    @Test(expected = OrcidAccessControlException.class)
    public void testName_CantRead_When_ReadPublicToken_IsPrivate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.PRIVATE);
        orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
        fail();
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testName_ThrowException_When_TokenIsForOtherUser() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
        Name name = createName(Visibility.PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_2, name, ScopePathType.ORCID_BIO_READ_LIMITED);
        fail();
    }

    @Test
    public void testName_CanRead_When_DontHaveReadScope_IsPublic() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
        Name name = createName(Visibility.PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void testName_CantRead_When_DontHaveReadScope_IsLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
        Name name = createName(Visibility.LIMITED);
        orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
        fail();
    }

    @Test(expected = OrcidAccessControlException.class)
    public void testName_CantRead_When_DontHaveReadScope_IsPrivate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
        Name name = createName(Visibility.PRIVATE);
        orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
        fail();
    }

    @Test
    public void testName_CanRead_When_HaveReadScope_IsPublic() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        Name name = createName(Visibility.PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @Test
    public void testName_CanRead_When_HaveReadScope_IsLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        Name name = createName(Visibility.LIMITED);
        orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testName_CantRead_When_HaveReadScope_IsPrivate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        Name name = createName(Visibility.PRIVATE);
        orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
        fail();
    }

    // Biography element tests
    @Test
    public void testBio_CanRead_When_ReadPublicToken_IsPublic() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Biography bio = createBiography(Visibility.PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void testBio_CantRead_When_ReadPublicToken_IsLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Biography bio = createBiography(Visibility.LIMITED);
        orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
        fail();
    }

    @Test(expected = OrcidAccessControlException.class)
    public void testBio_CantRead_When_ReadPublicToken_IsPrivate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Biography bio = createBiography(Visibility.PRIVATE);
        orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
        fail();
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testBio_ThrowException_When_TokenIsForOtherUser() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
        Biography bio = createBiography(Visibility.PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_2, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
        fail();
    }

    @Test
    public void testBio_CanRead_When_DontHaveReadScope_IsPublic() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
        Biography bio = createBiography(Visibility.PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void testBio_CantRead_When_DontHaveReadScope_IsLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
        Biography bio = createBiography(Visibility.LIMITED);
        orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
        fail();
    }

    @Test(expected = OrcidAccessControlException.class)
    public void testBio_CantRead_When_DontHaveReadScope_IsPrivate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
        Biography bio = createBiography(Visibility.PRIVATE);
        orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
        fail();
    }

    @Test
    public void testBio_CanRead_When_HaveReadScope_IsPublic() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        Biography bio = createBiography(Visibility.PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @Test
    public void testBio_CanRead_When_HaveReadScope_IsLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        Biography bio = createBiography(Visibility.LIMITED);
        orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testBio_CantRead_When_HaveReadScope_IsPrivate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        Biography bio = createBiography(Visibility.PRIVATE);
        orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
        fail();
    }

    // ---- ELEMENTS WITH SOURCE ----
    // Work element tests
    @Test
    public void testWork_CanRead_When_IsSource_And_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED);

        Work work = createWork(Visibility.PUBLIC, CLIENT_1);
        orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);

        work = createWork(Visibility.LIMITED, CLIENT_1);
        orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);

        work = createWork(Visibility.PRIVATE, CLIENT_1);
        orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
    }

    @Test
    public void testWork_CanRead_When_ReadPublicToken_IsPublic_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Work work = createWork(Visibility.PUBLIC, CLIENT_2);
        orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void testWork_CantRead_When_ReadPublicToken_IsLimited_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Work work = createWork(Visibility.LIMITED, CLIENT_2);
        orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
        fail();
    }

    @Test(expected = OrcidAccessControlException.class)
    public void testWork_CantRead_When_ReadPublicToken_IsPrivate_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Work work = createWork(Visibility.PRIVATE, CLIENT_2);
        orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
        fail();
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testWork_ThrowException_When_TokenIsForOtherUser_IsSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Work work = createWork(Visibility.PUBLIC, CLIENT_1);
        orcidSecurityManager.checkAndFilter(ORCID_2, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
        fail();
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testWork_ThrowException_When_TokenIsForOtherUser_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Work work = createWork(Visibility.PUBLIC, CLIENT_2);
        orcidSecurityManager.checkAndFilter(ORCID_2, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
        fail();
    }

    @Test
    public void testWork_CanRead_When_IsSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);

        Work work = createWork(Visibility.PUBLIC, CLIENT_1);
        orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);

        work = createWork(Visibility.LIMITED, CLIENT_1);
        orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);

        work = createWork(Visibility.PRIVATE, CLIENT_1);
        orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
    }

    @Test
    public void testWork_CanRead_When_DontHaveReadScope_IsPublic_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_CREATE);
        Work work = createWork(Visibility.PUBLIC, CLIENT_2);
        orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void testWork_CantRead_When_DontHaveReadScope_IsLimited_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_CREATE);
        Work work = createWork(Visibility.LIMITED, CLIENT_2);
        orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
        fail();
    }

    @Test(expected = OrcidAccessControlException.class)
    public void testWork_CantRead_When_DontHaveReadScope_IsPrivate_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_CREATE);
        Work work = createWork(Visibility.PRIVATE, CLIENT_2);
        orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
        fail();
    }

    @Test
    public void testWork_CanRead_When_HaveReadScope_IsPublic_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED);
        Work work = createWork(Visibility.PUBLIC, CLIENT_2);
        orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
    }

    @Test
    public void testWork_CanRead_When_HaveReadScope_IsLimited_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED);
        Work work = createWork(Visibility.LIMITED, CLIENT_2);
        orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testWork_CantRead_When_HaveReadScope_IsPrivate_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED);
        Work work = createWork(Visibility.PRIVATE, CLIENT_2);
        orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
        fail();
    }

    // ---- COLLECTIONS OF ELEMENTS ----
    @Test(expected = OrcidUnauthorizedException.class)
    public void testCollection_When_TokenIsForOtherUser() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        List<OtherName> list = new ArrayList<OtherName>();
        list.add(createOtherName(Visibility.PUBLIC, CLIENT_1));
        list.add(createOtherName(Visibility.PUBLIC, CLIENT_1));
        list.add(createOtherName(Visibility.PUBLIC, CLIENT_1));
        orcidSecurityManager.checkAndFilter(ORCID_2, list, ScopePathType.ORCID_BIO_READ_LIMITED);
        fail();
    }

    @Test
    public void testCollection_When_SourceOfAll_ReadPublicScope() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        List<OtherName> list = new ArrayList<OtherName>();
        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_1);
        OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_1);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        list.add(o1);
        list.add(o2);
        list.add(o3);
        orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
        assertEquals(3, list.size());
        assertTrue(list.contains(o1));
        assertTrue(list.contains(o2));
        assertTrue(list.contains(o3));
    }

    @Test
    public void testCollection_When_SourceOfAll_ReadLimitedScope() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        List<OtherName> list = new ArrayList<OtherName>();
        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_1);
        OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_1);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        list.add(o1);
        list.add(o2);
        list.add(o3);
        orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
        assertEquals(3, list.size());
        assertTrue(list.contains(o1));
        assertTrue(list.contains(o2));
        assertTrue(list.contains(o3));
    }

    @Test
    public void testCollection_When_NotSource_ReadPublicScope() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        List<OtherName> list = new ArrayList<OtherName>();
        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        list.add(o1);
        list.add(o2);
        list.add(o3);
        orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
        assertEquals(1, list.size());
        assertTrue(list.contains(o1));
        assertFalse(list.contains(o2));
        assertFalse(list.contains(o3));
    }

    @Test
    public void testCollection_When_SourceOfPrivate_ReadPublicScope() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        List<OtherName> list = new ArrayList<OtherName>();
        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        list.add(o1);
        list.add(o2);
        list.add(o3);
        orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
        assertEquals(2, list.size());
        assertTrue(list.contains(o1));
        assertFalse(list.contains(o2));
        assertTrue(list.contains(o3));
    }

    @Test
    public void testCollection_When_SourceOfLimitedAndPrivate_ReadPublicScope() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        List<OtherName> list = new ArrayList<OtherName>();
        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_1);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        list.add(o1);
        list.add(o2);
        list.add(o3);
        orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
        assertEquals(3, list.size());
        assertTrue(list.contains(o1));
        assertTrue(list.contains(o2));
        assertTrue(list.contains(o3));
    }

    @Test
    public void testCollection_When_NotSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        List<OtherName> list = new ArrayList<OtherName>();
        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        list.add(o1);
        list.add(o2);
        list.add(o3);
        orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
        assertEquals(2, list.size());
        assertTrue(list.contains(o1));
        assertTrue(list.contains(o2));
        assertFalse(list.contains(o3));
    }

    @Test
    public void testCollection_When_NotSource_WrongReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED);
        List<OtherName> list = new ArrayList<OtherName>();
        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        list.add(o1);
        list.add(o2);
        list.add(o3);
        orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
        assertEquals(1, list.size());
        assertTrue(list.contains(o1));
        assertFalse(list.contains(o2));
        assertFalse(list.contains(o3));
    }

    @Test
    public void testCollection_When_NotSource_ReadLimitedToken_NothingPublic() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        List<OtherName> list = new ArrayList<OtherName>();
        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o4 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherName o5 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
        assertEquals(2, list.size());
        assertFalse(list.contains(o1));
        assertTrue(list.contains(o2));
        assertFalse(list.contains(o3));
        assertTrue(list.contains(o4));
        assertFalse(list.contains(o5));
    }

    @Test
    public void testCollection_When_NotSource_ReadLimitedToken_AllPrivate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        List<OtherName> list = new ArrayList<OtherName>();
        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o4 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o5 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        list.add(o5);
        assertFalse(list.isEmpty());
        orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
        assertTrue(list.isEmpty());
    }

    @Test
    public void testCollection_When_SourceOfPrivate_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        List<OtherName> list = new ArrayList<OtherName>();
        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        list.add(o1);
        list.add(o2);
        list.add(o3);
        orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
        assertEquals(3, list.size());
        assertTrue(list.contains(o1));
        assertTrue(list.contains(o2));
        assertTrue(list.contains(o3));
    }

    // ---- PERSONAL DETAILS ----
    @Test(expected = OrcidUnauthorizedException.class)
    public void testPersonalDetails_When_TokenForOtherUser() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        PersonalDetails p = new PersonalDetails();
        orcidSecurityManager.checkAndFilter(ORCID_2, p);
        fail();
    }

    @Test
    public void testPersonalDetails_When_AllPublic_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.PUBLIC);
        Biography bio = createBiography(Visibility.PUBLIC);
        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
        PersonalDetails p = new PersonalDetails();
        p.setBiography(bio);
        p.setName(name);
        p.setOtherNames(otherNames);
        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertEquals(name, p.getName());
        assertEquals(bio, p.getBiography());
        assertNotNull(p.getOtherNames());
        assertNotNull(p.getOtherNames().getOtherNames());
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
    }

    @Test
    public void testPersonalDetails_When_SomeLimited_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.LIMITED);
        Biography bio = createBiography(Visibility.PUBLIC);
        OtherName o1 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
        PersonalDetails p = new PersonalDetails();
        p.setBiography(bio);
        p.setName(name);
        p.setOtherNames(otherNames);
        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertNull(p.getName());
        assertEquals(bio, p.getBiography());
        assertNotNull(p.getOtherNames());
        assertNotNull(p.getOtherNames().getOtherNames());
        assertEquals(1, p.getOtherNames().getOtherNames().size());
        assertFalse(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertFalse(p.getOtherNames().getOtherNames().contains(o3));
    }

    @Test
    public void testPersonalDetails_When_SomePrivate_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.PUBLIC);
        Biography bio = createBiography(Visibility.PRIVATE);
        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
        PersonalDetails p = new PersonalDetails();
        p.setBiography(bio);
        p.setName(name);
        p.setOtherNames(otherNames);
        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertEquals(name, p.getName());
        assertNull(p.getBiography());
        assertNotNull(p.getOtherNames());
        assertNotNull(p.getOtherNames().getOtherNames());
        assertEquals(1, p.getOtherNames().getOtherNames().size());
        assertFalse(p.getOtherNames().getOtherNames().contains(o1));
        assertFalse(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
    }

    @Test
    public void testPersonalDetails_When_AllPrivate_NoSource_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.PRIVATE);
        Biography bio = createBiography(Visibility.PRIVATE);
        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
        PersonalDetails p = new PersonalDetails();
        p.setBiography(bio);
        p.setName(name);
        p.setOtherNames(otherNames);
        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertNull(p.getName());
        assertNull(p.getBiography());
        assertNotNull(p.getOtherNames());
        assertNotNull(p.getOtherNames().getOtherNames());
        assertTrue(p.getOtherNames().getOtherNames().isEmpty());
    }

    @Test
    public void testPersonalDetails_When_AllPrivate_Source_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.PRIVATE);
        Biography bio = createBiography(Visibility.PRIVATE);
        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
        PersonalDetails p = new PersonalDetails();
        p.setBiography(bio);
        p.setName(name);
        p.setOtherNames(otherNames);
        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertNull(p.getName());
        assertNull(p.getBiography());
        assertNotNull(p.getOtherNames());
        assertNotNull(p.getOtherNames().getOtherNames());
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
    }

    @Test
    public void testPersonalDetails_When_AllPublic_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        Name name = createName(Visibility.PUBLIC);
        Biography bio = createBiography(Visibility.PUBLIC);
        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
        PersonalDetails p = new PersonalDetails();
        p.setBiography(bio);
        p.setName(name);
        p.setOtherNames(otherNames);
        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertEquals(name, p.getName());
        assertEquals(bio, p.getBiography());
        assertNotNull(p.getOtherNames());
        assertNotNull(p.getOtherNames().getOtherNames());
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
    }

    @Test
    public void testPersonalDetails_When_SomeLimited_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        Name name = createName(Visibility.LIMITED);
        Biography bio = createBiography(Visibility.LIMITED);
        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
        PersonalDetails p = new PersonalDetails();
        p.setBiography(bio);
        p.setName(name);
        p.setOtherNames(otherNames);
        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertEquals(name, p.getName());
        assertEquals(bio, p.getBiography());
        assertNotNull(p.getOtherNames());
        assertNotNull(p.getOtherNames().getOtherNames());
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
    }

    @Test
    public void testPersonalDetails_When_SomePrivate_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        Name name = createName(Visibility.PRIVATE);
        Biography bio = createBiography(Visibility.PUBLIC);
        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
        PersonalDetails p = new PersonalDetails();
        p.setBiography(bio);
        p.setName(name);
        p.setOtherNames(otherNames);
        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertNull(p.getName());
        assertEquals(bio, p.getBiography());
        assertNotNull(p.getOtherNames());
        assertNotNull(p.getOtherNames().getOtherNames());
        assertEquals(2, p.getOtherNames().getOtherNames().size());
        assertFalse(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
    }

    @Test
    public void testPersonalDetails_When_AllPrivate_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        Name name = createName(Visibility.PRIVATE);
        Biography bio = createBiography(Visibility.PRIVATE);
        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
        PersonalDetails p = new PersonalDetails();
        p.setBiography(bio);
        p.setName(name);
        p.setOtherNames(otherNames);
        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertNull(p.getName());
        assertNull(p.getBiography());
        assertNotNull(p.getOtherNames());
        assertNotNull(p.getOtherNames().getOtherNames());
        assertTrue(p.getOtherNames().getOtherNames().isEmpty());
    }

    @Test
    public void testPersonalDetails_When_AllPrivate_Source_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        Name name = createName(Visibility.PRIVATE);
        Biography bio = createBiography(Visibility.PRIVATE);
        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
        PersonalDetails p = new PersonalDetails();
        p.setBiography(bio);
        p.setName(name);
        p.setOtherNames(otherNames);
        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
        assertNull(p.getName());
        assertNull(p.getBiography());
        assertNotNull(p.getOtherNames());
        assertNotNull(p.getOtherNames().getOtherNames());
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
    }

    @Test
    public void testPersonalDetails_When_ReadLimitedToken_EmptyElement() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        PersonalDetails p = new PersonalDetails();
        orcidSecurityManager.checkAndFilter(ORCID_1, p);
        assertNotNull(p);
    }
}
