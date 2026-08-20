package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.orcid.core.adapter.mapstruct.SourceMapperV3;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;

public class SourceMapperV3Test {

    @Test
    public void toSourceShouldUseContextSourceWhenPresent() {
        ExternalIdentifierEntity entity = new ExternalIdentifierEntity();
        entity.setSourceId("0000-0001-2345-6789");

        Source contextSource = new Source();
        Map<String, Source> sourceMap = new HashMap<String, Source>();
        sourceMap.put(SourceEntityUtils.getSourceKey(entity), contextSource);

        Source merged = new Source();
        SourceEntityUtils sourceEntityUtils = mock(SourceEntityUtils.class);
        when(sourceEntityUtils.mergeAndPopulateSource(contextSource, entity)).thenReturn(merged);

        Source result = SourceMapperV3.INSTANCE.toSource(entity, sourceMap, sourceEntityUtils);

        assertSame(merged, result);
    }

    @Test
    public void toSourceShouldUseNullContextSourceWhenMapMissing() {
        ExternalIdentifierEntity entity = new ExternalIdentifierEntity();
        entity.setSourceId("0000-0001-2345-6789");

        Source merged = new Source();
        SourceEntityUtils sourceEntityUtils = mock(SourceEntityUtils.class);
        when(sourceEntityUtils.mergeAndPopulateSource(null, entity)).thenReturn(merged);

        Source result = SourceMapperV3.INSTANCE.toSource(entity, null, sourceEntityUtils);

        assertEquals(merged, result);
    }
}
