package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

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
    
    @Test(expected = NoResultException.class)
    public void testGetNextImportSourceNameNoResult() {
        orgImportLogDao.getNextImportSourceName();
    }

    @Test
    public void testGetNextImportSourceName() {
        orgImportLogDao.merge(getLog("RINGGOLD", LocalDate.now().minusDays(5)));
        orgImportLogDao.merge(getLog("RINGGOLD", LocalDate.now().minusDays(15)));
        
        assertEquals("RINGGOLD", orgImportLogDao.getNextImportSourceName());
        
        
        orgImportLogDao.merge(getLog("FUNDREF", LocalDate.now().minusDays(6)));
        orgImportLogDao.merge(getLog("FUNDREF", LocalDate.now().minusDays(16)));
        
        assertEquals("FUNDREF", orgImportLogDao.getNextImportSourceName());
        
        
        orgImportLogDao.merge(getLog("GRID", LocalDate.now().minusDays(7)));
        orgImportLogDao.merge(getLog("GRID", LocalDate.now().minusDays(17)));
        
        assertEquals("GRID", orgImportLogDao.getNextImportSourceName());
        
        orgImportLogDao.merge(getLog("LEI", LocalDate.now().minusDays(8)));
        orgImportLogDao.merge(getLog("LEI", LocalDate.now().minusDays(18)));
        
        assertEquals("LEI", orgImportLogDao.getNextImportSourceName());
        
        orgImportLogDao.merge(getLog("RINGGOLD", LocalDate.now().minusDays(3)));
        orgImportLogDao.merge(getLog("FUNDREF", LocalDate.now().minusDays(3)));
        orgImportLogDao.merge(getLog("GRID", LocalDate.now().minusDays(4)));
        orgImportLogDao.merge(getLog("LEI", LocalDate.now().minusDays(3)));
        
        assertEquals("GRID", orgImportLogDao.getNextImportSourceName());
        
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
