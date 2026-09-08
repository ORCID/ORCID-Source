package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;

public class SourceMapperV3Test {

    @Test
    public void toSourceShouldPopulateSource() {
        ExternalIdentifierEntity entity = new ExternalIdentifierEntity();
        entity.setSourceId("0000-0001-2345-6789");

        Source merged = new Source();
        SourceEntityUtils sourceEntityUtils = mock(SourceEntityUtils.class);
        when(sourceEntityUtils.mergeAndPopulateSource(null, entity)).thenReturn(merged);

        Source result = new SourceMapperV3(sourceEntityUtils).toSource(entity);

        assertEquals(merged, result);
    }
}
