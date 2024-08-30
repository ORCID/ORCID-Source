package org.orcid.core.adapter.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.FamilyName;
import org.orcid.jaxb.model.v3.release.record.GivenNames;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class JpaJaxbNameAdapterTest extends MockSourceNameCache {
    @Resource
    private JpaJaxbNameAdapter adapter;

    @Test
    public void fromNameToRecordNameEntityTest() throws JAXBException {
        Name name = new Name();
        name.setCreditName(new CreditName("Credit Name"));
        name.setFamilyName(new FamilyName("Family Name"));
        name.setGivenNames(new GivenNames("Given Names"));
        name.setPath("0000-0000-0000-0000");
        name.setVisibility(Visibility.PUBLIC);
        name.setSource(new Source("0000-0000-0000-0000"));
        name.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
        name.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(new Date())));

        RecordNameEntity entity = adapter.toRecordNameEntity(name);
        assertNotNull(entity);
        assertNull(entity.getDateCreated());
        assertNull(entity.getLastModified());
        assertEquals("Credit Name", entity.getCreditName());
        assertEquals("Family Name", entity.getFamilyName());
        assertEquals("Given Names", entity.getGivenNames());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.name(), entity.getVisibility());
        assertEquals("0000-0000-0000-0000", entity.getOrcid());
    }

    @Test
    public void fromOtherNameEntityToOtherNameTest() throws IllegalAccessException {
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        RecordNameEntity entity = new RecordNameEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(entity, date);
        entity.setCreditName("Credit Name");
        entity.setFamilyName("Family Name");
        entity.setGivenNames("Given Names");
        entity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.name());
        entity.setOrcid("0000-0000-0000-0000");

        Name name = adapter.toName(entity);
        assertNotNull(name);
        assertNotNull(name.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(name.getCreatedDate().getValue()));
        assertNotNull(name.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(name.getLastModifiedDate().getValue()));
        assertEquals("Credit Name", name.getCreditName().getContent());
        assertEquals("Family Name", name.getFamilyName().getContent());
        assertEquals("Given Names", name.getGivenNames().getContent());
        assertEquals("0000-0000-0000-0000", name.getPath());
        assertEquals(Visibility.PUBLIC, name.getVisibility());
    }
}