package org.orcid.api.common.util.v3;

import org.junit.Test;
import org.mockito.Mockito;
import org.orcid.core.api.publicapi.v3.security.PublicAPISecurityManagerV3;
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

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PublicRecordUtilsTest {

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

    @Test
    public void getPublicRecord_callsSecurityFetchFilterAndSourceNameSetters() {
        String orcid = "0009-0004-3164-3380";

        RecordManagerReadOnly recordManagerReadOnly = Mockito.mock(RecordManagerReadOnly.class);
        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManager.class);
        PublicAPISecurityManagerV3 publicAPISecurityManagerV3 = Mockito.mock(PublicAPISecurityManagerV3.class);
        SourceUtils sourceUtilsReadOnly = Mockito.mock(SourceUtils.class);

        Record record = new Record();
        record.setPerson(new Person());
        record.setActivitiesSummary(emptyActivitiesSummary());

        when(recordManagerReadOnly.getPublicRecord(orcid, true)).thenReturn(record);

        Record result = PublicRecordUtils.getPublicRecord(
                orcid,
                recordManagerReadOnly,
                orcidSecurityManager,
                publicAPISecurityManagerV3,
                sourceUtilsReadOnly,
                true
        );

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

        Record record = new Record();
        when(recordManagerReadOnly.getPublicRecord(orcid, false)).thenReturn(record);

        Record result = PublicRecordUtils.getPublicRecord(
                orcid,
                recordManagerReadOnly,
                orcidSecurityManager,
                publicAPISecurityManagerV3,
                sourceUtilsReadOnly,
                false
        );

        assertSame(record, result);
        verify(orcidSecurityManager).checkProfile(orcid);
        verify(recordManagerReadOnly).getPublicRecord(orcid, false);
        verify(publicAPISecurityManagerV3).filter(record);
        verify(sourceUtilsReadOnly, never()).setSourceName(Mockito.any(Person.class));
        verify(sourceUtilsReadOnly, never()).setSourceName(Mockito.any(ActivitiesSummary.class));
    }
}

