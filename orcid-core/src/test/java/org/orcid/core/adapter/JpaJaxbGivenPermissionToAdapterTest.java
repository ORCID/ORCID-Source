package org.orcid.core.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.record_rc2.Delegation;
import org.orcid.jaxb.model.record_rc2.DelegationDetails;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.ProfileSummaryEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbGivenPermissionToAdapterTest {
    @Resource
    private JpaJaxbGivenPermissionToAdapter jpaJaxbGivenPermissionToAdapter;
    
    @Test
    public void fromDelegationDetailsToGivenPermissionToEntityTest() throws JAXBException {
        Delegation delegation = getDelegation();
        assertNotNull(delegation);
        assertNotNull(delegation.getGivenPermissionBy());        
        assertNotNull(delegation.getGivenPermissionBy().getDelegationDetails());
        assertNotNull(delegation.getGivenPermissionTo());
        assertNotNull(delegation.getGivenPermissionTo().getDelegationDetails());
        
        DelegationDetails details = delegation.getGivenPermissionTo().getDelegationDetails();
        assertEquals(Long.valueOf(1), details.getPutCode());
        assertNotNull(details.getApprovalDate());
        assertNotNull(details.getApprovalDate().getValue());        
        assertNotNull(details.getDelegateSummary());
        assertNotNull(details.getDelegateSummary().getCreditName());
        assertEquals("given-to-credit-name", details.getDelegateSummary().getCreditName().getContent());
        assertEquals(Visibility.PUBLIC, details.getDelegateSummary().getCreditName().getVisibility());
        assertNotNull(details.getDelegateSummary().getLastModifiedDate());        
        assertNotNull(details.getDelegateSummary().getOrcidIdentifier());
        assertEquals("orcid.org", details.getDelegateSummary().getOrcidIdentifier().getHost());
        assertEquals("http://orcid.org/8888-8888-8888-8880", details.getDelegateSummary().getOrcidIdentifier().getUri());
        assertEquals("8888-8888-8888-8880", details.getDelegateSummary().getOrcidIdentifier().getPath());
        
        GivenPermissionToEntity entity = jpaJaxbGivenPermissionToAdapter.toGivenPermissionTo(details);
        assertNotNull(entity);                        
        assertEquals(Long.valueOf(1), entity.getId());        
        assertNotNull(entity.getReceiver());        
        assertEquals("given-to-credit-name", entity.getReceiver().getCreditName());
        assertEquals(org.orcid.jaxb.model.message.Visibility.PUBLIC, entity.getReceiver().getNamesVisibility());                
        assertNotNull(entity.getApprovalDate());        
        assertNotNull(entity.getLastModified());
    }
    
    @Test
    public void fromGivenPermissionToEntityToDelegationDetailsTest() {
        GivenPermissionToEntity entity = getGivenPermissionToEntity();
        DelegationDetails details = jpaJaxbGivenPermissionToAdapter.toDelegationDetails(entity);
        assertNotNull(details);
        assertNotNull(details.getApprovalDate());
        assertNotNull(details.getApprovalDate().getValue());
        assertEquals(1, details.getApprovalDate().getValue().getDay());
        assertEquals(1, details.getApprovalDate().getValue().getMonth());
        assertEquals(2016, details.getApprovalDate().getValue().getYear());
        assertNotNull(details.getDelegateSummary());
        assertNotNull(details.getDelegateSummary().getCreditName());
        assertEquals("credit-name", details.getDelegateSummary().getCreditName().getContent());
        assertEquals(Visibility.PUBLIC, details.getDelegateSummary().getCreditName().getVisibility());
        assertNotNull(details.getDelegateSummary().getLastModifiedDate());                
        assertEquals(1, details.getDelegateSummary().getLastModifiedDate().getValue().getDay());
        assertEquals(1, details.getDelegateSummary().getLastModifiedDate().getValue().getMonth());
        assertEquals(2016, details.getDelegateSummary().getLastModifiedDate().getValue().getYear());                
        assertNotNull(details.getDelegateSummary().getOrcidIdentifier());
        assertEquals("9999-9999-9999-9999", details.getDelegateSummary().getOrcidIdentifier().getPath());
        assertEquals(Long.valueOf(1), details.getPutCode());
    }
    
    private Delegation getDelegation() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Delegation.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0_rc2/samples/delegation-2.0_rc2.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Delegation) unmarshaller.unmarshal(inputStream);
    }
    
    private GivenPermissionToEntity getGivenPermissionToEntity() {
        GivenPermissionToEntity entity = new GivenPermissionToEntity();
        Calendar c = new GregorianCalendar(2016, 0, 1);
        entity.setApprovalDate(c.getTime());
        entity.setDateCreated(c.getTime());
        entity.setGiver("0000-0000-0000-0000");
        entity.setId(1L);
        entity.setLastModified(c.getTime());
        ProfileSummaryEntity summary = new ProfileSummaryEntity();
        summary.setCreditName("credit-name");
        summary.setNamesVisibility(org.orcid.jaxb.model.message.Visibility.PUBLIC);
        summary.setId("9999-9999-9999-9999");
        entity.setReceiver(summary);
        return entity;
    }
}
