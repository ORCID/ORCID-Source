package org.orcid.api.common.util.v3;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.orcid.api.publicV3.server.security.PublicAPISecurityManagerV3;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.utils.v3.SourceUtils;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Distinctions;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositions;
import org.orcid.jaxb.model.v3.release.record.summary.Memberships;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.release.record.summary.Qualifications;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResources;
import org.orcid.jaxb.model.v3.release.record.summary.Services;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PublicRecordUtilsTest {

    private PublicRecordUtils publicRecordUtils;

    private ActivitiesSummary emptyActivitiesSummary() {
        ActivitiesSummary activitiesSummary = new ActivitiesSummary();
        activitiesSummary.setDistinctions(new Distinctions());
        activitiesSummary.setEducations(new Educations());
        activitiesSummary.setEmployments(new Employments());
        activitiesSummary.setInvitedPositions(new InvitedPositions());
        activitiesSummary.setMemberships(new Memberships());
        activitiesSummary.setQualifications(new Qualifications());
        activitiesSummary.setServices(new Services());
        activitiesSummary.setFundings(new Fundings());
        activitiesSummary.setPeerReviews(new PeerReviews());
        activitiesSummary.setWorks(new Works());
        activitiesSummary.setResearchResources(new ResearchResources());
        return activitiesSummary;
    }

    @Before
    public void initUtils() {
        publicRecordUtils = new PublicRecordUtils();
    }

    @Test
    public void getPublicRecord_callsSecurityFetchFilterAndSourceNameSetters() {
        String orcid = "0009-0004-3164-3380";

        RecordManagerReadOnly recordManagerReadOnly = Mockito.mock(RecordManagerReadOnly.class);
        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManager.class);
        PublicAPISecurityManagerV3 publicAPISecurityManagerV3 = Mockito.mock(PublicAPISecurityManagerV3.class);
        SourceUtils sourceUtilsReadOnly = Mockito.mock(SourceUtils.class);

        ReflectionTestUtils.setField(publicRecordUtils, "recordManagerReadOnly", recordManagerReadOnly);
        ReflectionTestUtils.setField(publicRecordUtils, "orcidSecurityManager", orcidSecurityManager);
        ReflectionTestUtils.setField(publicRecordUtils, "publicAPISecurityManagerV3", publicAPISecurityManagerV3);
        ReflectionTestUtils.setField(publicRecordUtils, "sourceUtilsReadOnly", sourceUtilsReadOnly);

        Record record = new Record();
        record.setPerson(new Person());
        record.setActivitiesSummary(emptyActivitiesSummary());

        when(recordManagerReadOnly.getPublicRecord(orcid, true)).thenReturn(record);

        Record result = publicRecordUtils.getPublicRecord(orcid, true);

        assertSame(record, result);
        verify(orcidSecurityManager).checkProfile(orcid);
        verify(recordManagerReadOnly).getPublicRecord(orcid, true);
        verify(publicAPISecurityManagerV3).filter(record);
        verify(sourceUtilsReadOnly).setSourceName(record.getPerson());
        verify(sourceUtilsReadOnly).setSourceName(record.getActivitiesSummary());
    }

    @Test
    public void getPublicRecord_whenNoPersonOrActivities_doesNotCallSourceNameSetters() {
        String orcid = "0009-0004-3164-3380";

        RecordManagerReadOnly recordManagerReadOnly = Mockito.mock(RecordManagerReadOnly.class);
        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManager.class);
        PublicAPISecurityManagerV3 publicAPISecurityManagerV3 = Mockito.mock(PublicAPISecurityManagerV3.class);
        SourceUtils sourceUtilsReadOnly = Mockito.mock(SourceUtils.class);

        ReflectionTestUtils.setField(publicRecordUtils, "recordManagerReadOnly", recordManagerReadOnly);
        ReflectionTestUtils.setField(publicRecordUtils, "orcidSecurityManager", orcidSecurityManager);
        ReflectionTestUtils.setField(publicRecordUtils, "publicAPISecurityManagerV3", publicAPISecurityManagerV3);
        ReflectionTestUtils.setField(publicRecordUtils, "sourceUtilsReadOnly", sourceUtilsReadOnly);

        Record record = new Record();
        when(recordManagerReadOnly.getPublicRecord(orcid, false)).thenReturn(record);

        Record result = publicRecordUtils.getPublicRecord(orcid, false);

        assertSame(record, result);
        verify(orcidSecurityManager).checkProfile(orcid);
        verify(recordManagerReadOnly).getPublicRecord(orcid, false);
        verify(publicAPISecurityManagerV3).filter(record);
        verify(sourceUtilsReadOnly, never()).setSourceName(Mockito.any(Person.class));
        verify(sourceUtilsReadOnly, never()).setSourceName(Mockito.any(ActivitiesSummary.class));
    }
}
