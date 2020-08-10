package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.OrgImportLogEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class OrgImportLogDaoTest {

    @Resource
    private OrgImportLogDao orgImportLogDao;
    
    @Test
    public void testGetNextImportSourceNameNoResult() {
        assertEquals(0, orgImportLogDao.getImportSourceOrder().size());
    }

    @Test
    public void testGetNextImportSourceName() {
        orgImportLogDao.merge(getLog("RINGGOLD", LocalDate.now().minusDays(5)));
        orgImportLogDao.merge(getLog("RINGGOLD", LocalDate.now().minusDays(15)));
        
        List<String> sourceNames = orgImportLogDao.getImportSourceOrder();
        assertEquals(1, sourceNames.size());
        assertEquals("RINGGOLD", sourceNames.get(0));
        
        orgImportLogDao.merge(getLog("FUNDREF", LocalDate.now().minusDays(6)));
        orgImportLogDao.merge(getLog("FUNDREF", LocalDate.now().minusDays(16)));
        
        sourceNames = orgImportLogDao.getImportSourceOrder();
        assertEquals(2, sourceNames.size());
        assertEquals("FUNDREF", sourceNames.get(0));
        
        orgImportLogDao.merge(getLog("GRID", LocalDate.now().minusDays(7)));
        orgImportLogDao.merge(getLog("GRID", LocalDate.now().minusDays(17)));
        
        sourceNames = orgImportLogDao.getImportSourceOrder();
        assertEquals(3, sourceNames.size());
        assertEquals("GRID", sourceNames.get(0));
        
        orgImportLogDao.merge(getLog("LEI", LocalDate.now().minusDays(8)));
        orgImportLogDao.merge(getLog("LEI", LocalDate.now().minusDays(18)));
        
        sourceNames = orgImportLogDao.getImportSourceOrder();
        assertEquals(4, sourceNames.size());
        assertEquals("LEI", sourceNames.get(0));
        
        orgImportLogDao.merge(getLog("RINGGOLD", LocalDate.now().minusDays(3)));
        orgImportLogDao.merge(getLog("FUNDREF", LocalDate.now().minusDays(3)));
        orgImportLogDao.merge(getLog("GRID", LocalDate.now().minusDays(4)));
        orgImportLogDao.merge(getLog("LEI", LocalDate.now().minusDays(3)));
        
        sourceNames = orgImportLogDao.getImportSourceOrder();
        assertEquals(4, sourceNames.size());
        assertEquals("GRID", sourceNames.get(0));
        
        orgImportLogDao.removeAll();
    }

    private OrgImportLogEntity getLog(String source, LocalDate date) {
        OrgImportLogEntity entity = new OrgImportLogEntity();
        entity.setSource(source);
        entity.setStart(Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        entity.setEnd(Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        return entity;
    }

}
