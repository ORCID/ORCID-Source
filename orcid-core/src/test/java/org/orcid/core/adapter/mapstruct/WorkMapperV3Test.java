package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.orcid.core.adapter.mapstruct.WorkMapperV3;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.WorkExtended;

public class WorkMapperV3Test {

    @Test
    public void mapWorkBtoAShouldSetTypeAndJournalTitle() {
        WorkType type = WorkType.values()[0];
        WorkEntity entity = new WorkEntity();
        entity.setWorkType(type.name());
        entity.setJournalTitle("Journal Name");

        Work work = new Work();
        WorkMapperV3.INSTANCE.mapWorkBtoA(entity, work);

        assertEquals(type, work.getWorkType());
        assertEquals("Journal Name", work.getJournalTitle().getContent());
    }

    @Test
    public void mapWorkExtendedBtoAShouldPopulateContributorsWhenJsonPresent() {
        WorkType type = WorkType.values()[0];
        WorkEntity entity = new WorkEntity();
        entity.setWorkType(type.name());
        entity.setTopContributorsJson("[{\"orcid\":\"0000-0000-0000-0000\"}]");

        WorkExtended work = new WorkExtended();
        ContributorsRolesAndSequencesMapperV3 converter = mock(ContributorsRolesAndSequencesMapperV3.class);

        WorkMapperV3.INSTANCE.mapWorkExtendedBtoA(entity, work, converter);

        verify(converter).getContributorsRolesAndSequencesList(entity.getTopContributorsJson());
    }
}
