package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.groupIds.issn.IssnPortalUrlBuilder;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.manager.v3.GroupIdRecordManager;
import org.orcid.core.manager.v3.PeerReviewManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.core.utils.Actors;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.common.PeerReviewType;
import org.orcid.jaxb.model.common.Role;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.pojo.OrgDisambiguated;
import org.orcid.pojo.grouping.PeerReviewGroup;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PeerReviewsControllerTest {

    private static final String USER_ORCID = "4444-4444-4444-4446";

    private PeerReviewsController peerReviewsController;

    @Mock
    private OrgDisambiguatedManager orgDisambiguatedManager;

    @Mock
    private PeerReviewManager peerReviewManager;

    @Mock
    private GroupIdRecordManager groupIdRecordManager;

    @Mock
    private IssnPortalUrlBuilder issnPortalUrlBuilder;

    @Mock
    private LocaleManager localeManager;

    @Mock
    private LanguagesMap lm;

    @Mock
    private ProfileEntityManager profileEntityManager;

    @Before
    public void before() {
        peerReviewsController = new PeerReviewsController();
        ReflectionTestUtils.setField(peerReviewsController, "orgDisambiguatedManager", orgDisambiguatedManager);
        ReflectionTestUtils.setField(peerReviewsController, "peerReviewManager", peerReviewManager);
        ReflectionTestUtils.setField(peerReviewsController, "groupIdRecordManager", groupIdRecordManager);
        ReflectionTestUtils.setField(peerReviewsController, "issnPortalUrlBuilder", issnPortalUrlBuilder);
        ReflectionTestUtils.setField(peerReviewsController, "lm", lm);

        // localeManager and profileEntityManager are re-declared by
        // PeerReviewsController (lines 59 and 74) over the copies BaseController
        // and BaseWorkspaceController declare; every one has to be set or
        // BaseController.getMessage() NPEs.
        ReflectionTestUtils.setField(peerReviewsController, PeerReviewsController.class, "localeManager", localeManager, LocaleManager.class);
        ReflectionTestUtils.setField(peerReviewsController, BaseController.class, "localeManager", localeManager, LocaleManager.class);
        ReflectionTestUtils.setField(peerReviewsController, PeerReviewsController.class, "profileEntityManager", profileEntityManager, ProfileEntityManager.class);
        ReflectionTestUtils.setField(peerReviewsController, BaseWorkspaceController.class, "profileEntityManager", profileEntityManager, ProfileEntityManager.class);
        ReflectionTestUtils.setField(peerReviewsController, BaseController.class, "profileEntityManager", profileEntityManager, ProfileEntityManager.class);

        when(localeManager.resolveMessage(anyString(), any())).thenAnswer(invocation -> invocation.getArgument(0));

        Actors.user(USER_ORCID);
    }

    @After
    public void after() {
        Actors.clear();
    }

    @Test
    public void testSearchDisambiguated() {
        Mockito.when(orgDisambiguatedManager.searchOrgsFromSolr(Mockito.eq("search"), Mockito.eq(0), Mockito.eq(0), Mockito.eq(false)))
                .thenReturn(getListOfMixedOrgsDiambiguated());

        List<Map<String, String>> results = peerReviewsController.searchDisambiguated("search", 0);
        assertEquals(4, results.size());
        assertEquals("first", results.get(0).get("value"));
        assertEquals("second", results.get(1).get("value"));
        assertEquals("third", results.get(2).get("value"));
        assertEquals("fourth", results.get(3).get("value"));
    }

    private List<OrgDisambiguated> getListOfMixedOrgsDiambiguated() {
        OrgDisambiguated first = new OrgDisambiguated();
        first.setValue("first");
        first.setSourceType(OrgDisambiguatedSourceType.FUNDREF.name());

        OrgDisambiguated second = new OrgDisambiguated();
        second.setValue("second");
        second.setSourceType(OrgDisambiguatedSourceType.RINGGOLD.name());

        OrgDisambiguated third = new OrgDisambiguated();
        third.setValue("third");
        third.setSourceType(OrgDisambiguatedSourceType.GRID.name());

        OrgDisambiguated fourth = new OrgDisambiguated();
        fourth.setValue("fourth");
        fourth.setSourceType(OrgDisambiguatedSourceType.LEI.name());

        return Arrays.asList(first, second, third, fourth);
    }

    /**
     * The grouping itself is PeerReviewManagerImpl.groupPeerReviews; what stays
     * here is the controller's own work -- resolving each group's
     * GroupIdRecord, wrapping it in the pojo, and applying
     * PeerReviewGroupComparator in the requested direction. The groups are
     * deliberately handed back out of order so the sort is really exercised.
     */
    @Test
    public void testGetPeerReviews() {
        List<PeerReviewSummary> summaries = Arrays.asList(summary(1L, "publons:group-c"), summary(2L, "publons:group-a"), summary(3L, "publons:group-d"),
                summary(4L, "publons:group-b"));
        when(peerReviewManager.getPeerReviewSummaryList(USER_ORCID)).thenReturn(summaries);
        when(peerReviewManager.groupPeerReviews(eq(summaries), eq(false))).thenReturn(peerReviews(summaries));

        when(groupIdRecordManager.findByGroupId("publons:group-a")).thenReturn(Optional.of(groupIdRecord(1L, "publons:group-a", "Group A")));
        when(groupIdRecordManager.findByGroupId("publons:group-b")).thenReturn(Optional.of(groupIdRecord(2L, "publons:group-b", "Group B")));
        when(groupIdRecordManager.findByGroupId("publons:group-c")).thenReturn(Optional.of(groupIdRecord(3L, "publons:group-c", "Group C")));
        when(groupIdRecordManager.findByGroupId("publons:group-d")).thenReturn(Optional.of(groupIdRecord(4L, "publons:group-d", "Group D")));

        List<PeerReviewGroup> groups = peerReviewsController.getPeerReviewsJson(true);
        assertNotNull(groups);
        assertEquals(4, groups.size());
        assertNotNull(groups.get(0).getPeerReviewDuplicateGroups());
        assertNotNull(groups.get(1).getPeerReviewDuplicateGroups());
        assertNotNull(groups.get(2).getPeerReviewDuplicateGroups());
        assertNotNull(groups.get(3).getPeerReviewDuplicateGroups());
        assertEquals(1, groups.get(0).getPeerReviewDuplicateGroups().size());
        assertEquals(1, groups.get(1).getPeerReviewDuplicateGroups().size());
        assertEquals(1, groups.get(2).getPeerReviewDuplicateGroups().size());
        assertEquals(1, groups.get(3).getPeerReviewDuplicateGroups().size());

        assertTrue(groups.get(0).getName().compareTo(groups.get(1).getName()) < 0);
        assertTrue(groups.get(1).getName().compareTo(groups.get(2).getName()) < 0);
        assertTrue(groups.get(2).getName().compareTo(groups.get(3).getName()) < 0);

        groups = peerReviewsController.getPeerReviewsJson(false);
        assertNotNull(groups);
        assertEquals(4, groups.size());
        assertNotNull(groups.get(0).getPeerReviewDuplicateGroups());
        assertNotNull(groups.get(1).getPeerReviewDuplicateGroups());
        assertNotNull(groups.get(2).getPeerReviewDuplicateGroups());
        assertNotNull(groups.get(3).getPeerReviewDuplicateGroups());
        assertEquals(1, groups.get(0).getPeerReviewDuplicateGroups().size());
        assertEquals(1, groups.get(1).getPeerReviewDuplicateGroups().size());
        assertEquals(1, groups.get(2).getPeerReviewDuplicateGroups().size());
        assertEquals(1, groups.get(3).getPeerReviewDuplicateGroups().size());

        assertTrue(groups.get(0).getName().compareTo(groups.get(1).getName()) > 0);
        assertTrue(groups.get(1).getName().compareTo(groups.get(2).getName()) > 0);
        assertTrue(groups.get(2).getName().compareTo(groups.get(3).getName()) > 0);
    }

    private PeerReviews peerReviews(List<PeerReviewSummary> summaries) {
        PeerReviews peerReviews = new PeerReviews();
        for (PeerReviewSummary summary : summaries) {
            org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup group = new org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup();
            PeerReviewDuplicateGroup duplicateGroup = new PeerReviewDuplicateGroup();
            duplicateGroup.getPeerReviewSummary().add(summary);
            group.getPeerReviewGroup().add(duplicateGroup);
            peerReviews.getPeerReviewGroup().add(group);
        }
        return peerReviews;
    }

    private PeerReviewSummary summary(Long putCode, String groupId) {
        PeerReviewSummary summary = new PeerReviewSummary();
        summary.setPutCode(putCode);
        summary.setGroupId(groupId);
        summary.setDisplayIndex("0");
        summary.setVisibility(Visibility.PUBLIC);
        summary.setRole(Role.REVIEWER);
        summary.setType(PeerReviewType.REVIEW);
        Source source = new Source();
        source.setSourceOrcid(new SourceOrcid(USER_ORCID));
        summary.setSource(source);
        summary.setExternalIdentifiers(new org.orcid.jaxb.model.v3.release.record.ExternalIDs());
        return summary;
    }

    private GroupIdRecord groupIdRecord(Long putCode, String groupId, String name) {
        GroupIdRecord record = new GroupIdRecord();
        record.setPutCode(putCode);
        record.setGroupId(groupId);
        record.setName(name);
        record.setDescription(name + " description");
        record.setType("publisher");
        return record;
    }
}
