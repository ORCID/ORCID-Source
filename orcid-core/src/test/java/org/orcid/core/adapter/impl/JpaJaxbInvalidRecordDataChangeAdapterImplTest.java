package org.orcid.core.adapter.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.orcid.model.record_correction.RecordCorrection;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;

public class JpaJaxbInvalidRecordDataChangeAdapterImplTest {

    private final JpaJaxbInvalidRecordDataChangeAdapterImpl adapter = new JpaJaxbInvalidRecordDataChangeAdapterImpl();

    @Test
    public void toInvalidRecordDataChangeShouldReturnNullForNullInput() {
        assertNull(adapter.toInvalidRecordDataChange(null));
    }

    @Test
    public void toInvalidRecordDataChangeShouldMapFields() {
        InvalidRecordDataChangeEntity entity = new InvalidRecordDataChangeEntity();
        entity.setId(11L);
        entity.setDescription("description");
        entity.setSqlUsedToUpdate("sql");
        entity.setNumChanged(3L);
        entity.setType("FUNDING");

        RecordCorrection mapped = adapter.toInvalidRecordDataChange(entity);

        assertNotNull(mapped);
        assertEquals(Long.valueOf(11L), mapped.getSequence());
        assertEquals("description", mapped.getDescription());
        assertEquals("sql", mapped.getSqlUsedToUpdate());
        assertEquals(Long.valueOf(3L), mapped.getNumChanged());
        assertEquals("FUNDING", mapped.getType());
    }

    @Test
    public void toInvalidRecordDataChangesShouldMapList() {
        InvalidRecordDataChangeEntity first = new InvalidRecordDataChangeEntity();
        first.setId(1L);
        InvalidRecordDataChangeEntity second = new InvalidRecordDataChangeEntity();
        second.setId(2L);

        List<RecordCorrection> mapped = adapter.toInvalidRecordDataChanges(Arrays.asList(first, second));

        assertNotNull(mapped);
        assertEquals(2, mapped.size());
        assertEquals(Long.valueOf(1L), mapped.get(0).getSequence());
        assertEquals(Long.valueOf(2L), mapped.get(1).getSequence());
    }
}
