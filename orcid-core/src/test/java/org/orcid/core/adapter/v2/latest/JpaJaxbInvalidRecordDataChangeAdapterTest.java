package org.orcid.core.adapter.v2.latest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.JpaJaxbInvalidRecordDataChangeAdapter;
import org.orcid.model.record_correction.RecordCorrection;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.junit.Before;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.adapter.mapstruct.impl.JpaJaxbInvalidRecordDataChangeAdapterImpl;
import org.orcid.core.adapter.MockedMapStructAdapters;

@RunWith(MockitoJUnitRunner.Silent.class)
public class JpaJaxbInvalidRecordDataChangeAdapterTest {

    private final MockedMapStructAdapters adapters = new MockedMapStructAdapters();

    private JpaJaxbInvalidRecordDataChangeAdapter adapter;

    @Before
    public void setUpAdapter() throws Exception {
        // REAL MapStruct mapper: the whole behaviour under test is the mapping configuration,
        // so a mocked mapper would make every assertion below an assertion about a mock.
        adapter = adapters.get(JpaJaxbInvalidRecordDataChangeAdapterImpl.class);
    }

    @Test
    public void fromEntityTest() throws IllegalAccessException {
        InvalidRecordDataChangeEntity entity = getEntity();
        RecordCorrection element = adapter.toInvalidRecordDataChange(entity);
        assertNotNull(element);
        assertNotNull(element.getDateCreated());
        assertNotNull(element.getLastModified());
        assertEquals(element.getSequence(), entity.getId());
        assertEquals(element.getDateCreated(), entity.getDateCreated());
        assertEquals(element.getDescription(), entity.getDescription());
        assertEquals(element.getLastModified(), entity.getLastModified());
        assertEquals(element.getNumChanged(), entity.getNumChanged());
        assertEquals(element.getSqlUsedToUpdate(), entity.getSqlUsedToUpdate());
        assertEquals(element.getType(), entity.getType());
    }

    private InvalidRecordDataChangeEntity getEntity() throws IllegalAccessException {
        InvalidRecordDataChangeEntity entity = new InvalidRecordDataChangeEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(entity, new Date());
        entity.setDescription("description");
        entity.setId(1234L);
        entity.setNumChanged(24816L);
        entity.setSqlUsedToUpdate("update table set data = 'value' where key = key");
        entity.setType("type");
        return entity;
    }
}
