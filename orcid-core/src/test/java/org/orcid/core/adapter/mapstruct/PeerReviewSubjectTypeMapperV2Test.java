package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.mapstruct.PeerReviewSubjectTypeMapperV2;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class PeerReviewSubjectTypeMapperV2Test {
        
    private PeerReviewSubjectTypeMapperV2 peerReviewSubjectTypeConverter = PeerReviewSubjectTypeMapperV2.INSTANCE;

    @Test
    public void testConvertTo() {
        for(WorkType t : WorkType.values()) {
            assertEquals(t.name(), peerReviewSubjectTypeConverter.convertTo(t));
        }
    }

    @Test
    public void testConvertFrom() {
        for(WorkType t : WorkType.values()) {
            assertEquals(t, peerReviewSubjectTypeConverter.convertFrom(t.name()));
        }
        
        assertEquals(WorkType.DISSERTATION, peerReviewSubjectTypeConverter.convertFrom(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name()));
        assertEquals(WorkType.OTHER, peerReviewSubjectTypeConverter.convertFrom(org.orcid.jaxb.model.common.WorkType.ANNOTATION.name()));
        assertEquals(WorkType.OTHER, peerReviewSubjectTypeConverter.convertFrom(org.orcid.jaxb.model.common.WorkType.DATA_MANAGEMENT_PLAN.name()));
        assertEquals(WorkType.OTHER, peerReviewSubjectTypeConverter.convertFrom(org.orcid.jaxb.model.common.WorkType.PHYSICAL_OBJECT.name()));
        assertEquals(WorkType.OTHER, peerReviewSubjectTypeConverter.convertFrom(org.orcid.jaxb.model.common.WorkType.PREPRINT.name()));
        assertEquals(WorkType.OTHER, peerReviewSubjectTypeConverter.convertFrom(org.orcid.jaxb.model.common.WorkType.SOFTWARE.name()));
    }
}
