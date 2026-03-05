package org.orcid.api.memberV3.server.delegator;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.api.common.util.ApiUtils;
import org.orcid.api.memberV3.server.delegator.impl.MemberV3ApiServiceDelegatorImpl;
import org.orcid.core.common.manager.EmailDomainManager;
import org.orcid.core.common.manager.SummaryManager;
import org.orcid.core.exception.OrcidCoreExceptionMapper;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.StatusManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.read_only.ClientDetailsManagerReadOnly;
import org.orcid.core.manager.v3.*;
import org.orcid.core.manager.v3.read_only.*;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.core.utils.v3.ContributorUtils;
import org.orcid.core.utils.v3.SourceUtils;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.v3.release.record.*;
import org.springframework.context.MessageSource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class MemberV3ApiServiceDelegatorMockTest {

    @Mock
    protected WorkManager workManager;

    @Mock
    protected ProfileFundingManager profileFundingManager;

    @Mock
    protected ProfileEntityManager profileEntityManager;

    @Mock
    protected AffiliationsManager affiliationsManager;

    @Mock
    protected PeerReviewManager peerReviewManager;

    @Mock
    protected OrcidSecurityManager orcidSecurityManager;

    @Mock
    protected GroupIdRecordManager groupIdRecordManager;

    @Mock
    protected LocaleManager localeManager;

    @Mock
    protected ResearcherUrlManager researcherUrlManager;

    @Mock
    protected OtherNameManager otherNameManager;

    @Mock
    protected ExternalIdentifierManager externalIdentifierManager;

    @Mock
    protected ProfileKeywordManager profileKeywordManager;

    @Mock
    protected AddressManager addressManager;

    @Mock
    protected SourceUtils sourceUtils;

    @Mock
    protected ContributorUtils contributorUtils;

    @Mock
    protected OrcidSearchManager orcidSearchManager;

    @Mock
    protected OrcidCoreExceptionMapper orcidCoreExceptionMapper;

    @Mock
    protected WorkManagerReadOnly workManagerReadOnly;

    @Mock
    protected ProfileFundingManagerReadOnly profileFundingManagerReadOnly;

    @Mock
    protected AffiliationsManagerReadOnly affiliationsManagerReadOnly;

    @Mock
    protected PeerReviewManagerReadOnly peerReviewManagerReadOnly;

    @Mock
    protected ActivitiesSummaryManagerReadOnly activitiesSummaryManagerReadOnly;

    @Mock
    protected ResearchResourceManager researchResourceManager;

    @Mock
    protected ResearchResourceManagerReadOnly researchResourceManagerReadOnly;

    @Mock
    protected ResearcherUrlManagerReadOnly researcherUrlManagerReadOnly;

    @Mock
    protected OtherNameManagerReadOnly otherNameManagerReadOnly;

    @Mock
    protected EmailManagerReadOnly emailManagerReadOnly;

    @Mock
    protected ExternalIdentifierManagerReadOnly externalIdentifierManagerReadOnly;

    @Mock
    protected PersonalDetailsManagerReadOnly personalDetailsManagerReadOnly;

    @Mock
    protected ProfileKeywordManagerReadOnly profileKeywordManagerReadOnly;

    @Mock
    protected AddressManagerReadOnly addressManagerReadOnly;

    @Mock
    protected BiographyManagerReadOnly biographyManagerReadOnly;

    @Mock
    protected PersonDetailsManagerReadOnly personDetailsManagerReadOnly;

    @Mock
    protected RecordManagerReadOnly recordManagerReadOnly;

    @Mock
    protected GroupIdRecordManagerReadOnly groupIdRecordManagerReadOnly;

    @Mock
    protected ClientDetailsManagerReadOnly clientDetailsManagerReadOnly;

    @Mock
    protected ClientManagerReadOnly clientManagerReadOnly;

    @Mock
    protected MessageSource messageSource;

    @Mock
    protected StatusManager statusManager;

    @Mock
    protected SourceManager sourceManager;

    @Mock
    protected ApiUtils apiUtils;

    @Mock
    protected OrcidUrlManager orcidUrlManager;

    @Mock
    protected SummaryManager summaryManager;

    @Mock
    protected EmailDomainManager emailDomainManager;

    @Mock
    protected SourceEntityUtils sourceEntityUtils;

    @InjectMocks
    protected MemberV3ApiServiceDelegatorImpl serviceDelegator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
}
