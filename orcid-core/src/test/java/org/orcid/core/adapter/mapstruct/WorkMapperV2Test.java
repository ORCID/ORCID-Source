package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;

public class WorkMapperV2Test {

    @Test
    public void mapWorkAtoBShouldMapDissertationToDissertationThesis() {
        Work work = mock(Work.class, RETURNS_DEEP_STUBS);
        org.mockito.Mockito.when(work.getWorkType()).thenReturn(WorkType.DISSERTATION);
        WorkEntity entity = new WorkEntity();

        WorkMapperV2.INSTANCE.mapWorkAtoB(work, entity);

        assertEquals(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name(), entity.getWorkType());
    }

    @Test
    public void mapWorkBtoAShouldMapReviewToOther() {
        WorkEntity entity = new WorkEntity();
        entity.setWorkType(WorkType.REVIEW.name());
        Work work = new Work();

        WorkMapperV2.INSTANCE.mapWorkBtoA(entity, work);

        assertEquals(WorkType.OTHER, work.getWorkType());
    }

    @Test
    public void mapWorkSummaryToMinimizedBtoAShouldResolveDowngrade() {
        MinimizedWorkEntity entity = new MinimizedWorkEntity();
        entity.setWorkType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name());
        WorkSummary summary = new WorkSummary();

        WorkMapperV2.INSTANCE.mapWorkSummaryToMinimizedBtoA(entity, summary);

        assertEquals(WorkType.DISSERTATION, summary.getType());
    }
}
