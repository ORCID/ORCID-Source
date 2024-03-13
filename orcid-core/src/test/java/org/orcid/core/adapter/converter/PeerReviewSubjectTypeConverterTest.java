package org.orcid.core.adapter.converter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class PeerReviewSubjectTypeConverterTest {
        
    private PeerReviewSubjectTypeConverter peerReviewSubjectTypeConverter = new PeerReviewSubjectTypeConverter();

    @Test
    public void testConvertTo() {
        for(WorkType t : WorkType.values()) {
            assertEquals(t.name(), peerReviewSubjectTypeConverter.convertTo(t, null));
        }
    }

    @Test
    public void testConvertFrom() {
        for(WorkType t : WorkType.values()) {
            assertEquals(t, peerReviewSubjectTypeConverter.convertFrom(t.name(), null));
        }
        
        assertEquals(WorkType.DISSERTATION, peerReviewSubjectTypeConverter.convertFrom(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name(), null));
        assertEquals(WorkType.OTHER, peerReviewSubjectTypeConverter.convertFrom(org.orcid.jaxb.model.common.WorkType.ANNOTATION.name(), null));
        assertEquals(WorkType.OTHER, peerReviewSubjectTypeConverter.convertFrom(org.orcid.jaxb.model.common.WorkType.DATA_MANAGEMENT_PLAN.name(), null));
        assertEquals(WorkType.OTHER, peerReviewSubjectTypeConverter.convertFrom(org.orcid.jaxb.model.common.WorkType.PHYSICAL_OBJECT.name(), null));
        assertEquals(WorkType.OTHER, peerReviewSubjectTypeConverter.convertFrom(org.orcid.jaxb.model.common.WorkType.PREPRINT.name(), null));
        assertEquals(WorkType.OTHER, peerReviewSubjectTypeConverter.convertFrom(org.orcid.jaxb.model.common.WorkType.SOFTWARE.name(), null));
    }
}
