package org.orcid.core.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class V3VersionConverterChainTest {
    
    @Resource
    private V3VersionConverterChain v3VersionConverterChain;

    @Test
    public void upgradeRC1ToRC2Test() {
        org.orcid.jaxb.model.v3.rc1.record.Work rc1Work = new org.orcid.jaxb.model.v3.rc1.record.Work();
        rc1Work.setWorkType(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION);
        org.orcid.jaxb.model.v3.rc2.record.Work rc2Work = (org.orcid.jaxb.model.v3.rc2.record.Work)(v3VersionConverterChain.upgrade(new V3Convertible(rc1Work, "3.0_rc1"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2Work);
        assertEquals(org.orcid.jaxb.model.v3.rc2.record.WorkType.DISSERTATION_THESIS, rc2Work.getWorkType());
    
        org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary rc1WorkSummary = new org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary();
        rc1WorkSummary.setType(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION);
        org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary rc2WorkSummary = (org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary)(v3VersionConverterChain.upgrade(new V3Convertible(rc1WorkSummary, "3.0_rc1"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2WorkSummary);
        assertEquals(org.orcid.jaxb.model.v3.rc2.record.WorkType.DISSERTATION_THESIS, rc2WorkSummary.getType());
    }
    
    @Test
    public void downgradeRC2ToRC1Test() {
        org.orcid.jaxb.model.v3.rc2.record.Work rc2Work = new org.orcid.jaxb.model.v3.rc2.record.Work();
        rc2Work.setWorkType(org.orcid.jaxb.model.v3.rc2.record.WorkType.DISSERTATION_THESIS);
        org.orcid.jaxb.model.v3.rc1.record.Work rc1Work = (org.orcid.jaxb.model.v3.rc1.record.Work)(v3VersionConverterChain.downgrade(new V3Convertible(rc2Work, "3.0_rc2"), "3.0_rc1")).getObjectToConvert();
        assertNotNull(rc1Work);
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION, rc1Work.getWorkType());
    
        org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary rc2WorkSummary = new org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary();
        rc2WorkSummary.setType(org.orcid.jaxb.model.v3.rc2.record.WorkType.DISSERTATION_THESIS);
        org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary rc1WorkSummary = (org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary)(v3VersionConverterChain.downgrade(new V3Convertible(rc2WorkSummary, "3.0_rc2"), "3.0_rc1")).getObjectToConvert();
        assertNotNull(rc1WorkSummary);
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION, rc1WorkSummary.getType());
    }
}
