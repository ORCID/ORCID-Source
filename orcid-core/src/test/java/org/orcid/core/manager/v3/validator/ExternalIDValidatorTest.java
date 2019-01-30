package org.orcid.core.manager.v3.validator;

import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.rc2.common.Url;
import org.orcid.jaxb.model.v3.rc2.notification.permission.Item;
import org.orcid.jaxb.model.v3.rc2.notification.permission.Items;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.ExternalIDs;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class ExternalIDValidatorTest{

    @Resource(name = "externalIDValidatorV3")
    private ExternalIDValidator validator;
    
    @Before
    public void before() {
        validator.setRequireRelationshipOnExternalIdentifier(false);
    }
    
    @Test
    public void testValidateWorkOrPeerReview(){
        //call for ExternalID and ExternalIDs
        
        //ID valid
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("doi");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));
        validator.validateWorkOrPeerReview(id1);
                
        //ID bad type
        try{
            id1.setType("invalid");
            validator.validateWorkOrPeerReview(id1);
            fail("no exception thrown for invalid type");
        }catch(Exception e){
            if (!(e instanceof ActivityIdentifierValidationException))
                throw e;
        }
        
        //id null
        try{
            id1.setType(null);
            validator.validateWorkOrPeerReview(id1);
            fail("no exception thrown for invalid type");
        }catch(Exception e){
            if (!(e instanceof ActivityIdentifierValidationException))
                throw e;
        }

        ExternalIDs ids = new ExternalIDs();
        ids.getExternalIdentifier().add(id1);
        
        //IDS one invalid
        id1.setType("invalid");
        try{
            validator.validatePeerReview(ids);
            fail("no exception thrown for invalid type");
        }catch(Exception e){
            if (!(e instanceof ActivityIdentifierValidationException))
                throw e;
        }

        //IDS one valid (lowercase)
        id1.setType("doi");
        validator.validatePeerReview(ids);
        
        //IDS two valid
        ExternalID id2 = new ExternalID();
        id2.setRelationship(Relationship.SELF);
        id2.setType("source-work-id");
        id2.setValue("value2");
        id2.setUrl(new Url("http://value1.com"));        
        ids.getExternalIdentifier().add(id2);
        validator.validatePeerReview(ids);
        
        //IDS one invalid, one valid
        id2.setType("not-a-type");
        try{
            validator.validatePeerReview(ids);
            fail("no exception thrown for invalid type");
        }catch(Exception e){
            if (!(e instanceof ActivityIdentifierValidationException))
                throw e;
        }    
    }
    
    @Test
    public void testValidateWork() {
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("doi");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));
        
        ExternalIDs externalIds = new ExternalIDs();
        externalIds.getExternalIdentifier().add(id1);
        validator.validateWork(externalIds);

        try {
            id1.setType("invalid");
            validator.validateWork(externalIds);
            fail("no exception thrown for invalid type");
        } catch (Exception e) {
            if (!(e instanceof ActivityIdentifierValidationException))
                throw e;
        }

        try {
            id1.setType(null);
            validator.validateWork(externalIds);
            fail("no exception thrown for invalid type");
        } catch (Exception e) {
            if (!(e instanceof ActivityIdentifierValidationException))
                throw e;
        }

        id1.setType("doi");
        
        ExternalID id2 = new ExternalID();
        id2.setRelationship(Relationship.SELF);
        id2.setType("source-work-id");
        id2.setValue("value2");
        id2.setUrl(new Url("http://value1.com"));
        externalIds.getExternalIdentifier().add(id2);
        validator.validateWork(externalIds);

        // IDS one invalid, one valid
        id2.setType("not-a-type");
        try {
            validator.validateWork(externalIds);
            fail("no exception thrown for invalid type");
        } catch (Exception e) {
            if (!(e instanceof ActivityIdentifierValidationException))
                throw e;
        }

        // missing 'self' external id
        id1.setRelationship(Relationship.PART_OF);
        id2.setRelationship(Relationship.PART_OF);
        try {
            validator.validateWork(externalIds);
            fail("no exception thrown for invalid type");
        } catch (Exception e) {
            if (!(e instanceof ActivityIdentifierValidationException))
                throw e;
        }
    }

    @Test(expected = ActivityIdentifierValidationException.class)
    public void testEmptyRelationshipOnSingleExternalId_flagOn() {
        validator.setRequireRelationshipOnExternalIdentifier(true);
        ExternalID id1 = new ExternalID();
        id1.setType("doi");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));
        validator.validateWorkOrPeerReview(id1);
                
        //empty relationship        
        id1.setRelationship(null);
        validator.validateWorkOrPeerReview(id1);
        fail("no exception thrown for invalid type");        
    }
    
    @Test
    public void testEmptyRelationshipOnSingleExternalId_flagOff() {
        ExternalID id1 = new ExternalID();
        id1.setType("doi");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));
        validator.validateWorkOrPeerReview(id1);
                
        //empty relationship        
        id1.setRelationship(null);
        validator.validateWorkOrPeerReview(id1);               
    }
    
    @Test(expected = ActivityIdentifierValidationException.class)
    public void testEmptyRelationshipOnExternalIds_flagOn() {
        validator.setRequireRelationshipOnExternalIdentifier(true);
        ExternalIDs extIds = new ExternalIDs();
        
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("doi");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));
                
        ExternalID id2 = new ExternalID();
        id2.setRelationship(null);
        id2.setType("doi");
        id2.setValue("value1");
        id2.setUrl(new Url("http://value1.com"));
        
        ExternalID id3 = new ExternalID();
        id3.setRelationship(Relationship.SELF);
        id3.setType("doi");
        id3.setValue("value1");
        id3.setUrl(new Url("http://value1.com"));
        
        extIds.getExternalIdentifier().add(id1);
        extIds.getExternalIdentifier().add(id2);
        extIds.getExternalIdentifier().add(id3);
        
        validator.validatePeerReview(extIds);
        fail("no exception thrown for invalid type");
    }
    
    @Test
    public void testEmptyRelationshipOnExternalIds_flagOff() {        
        ExternalIDs extIds = new ExternalIDs();
        
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("doi");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));
                
        ExternalID id2 = new ExternalID();
        id2.setRelationship(null);
        id2.setType("doi");
        id2.setValue("value1");
        id2.setUrl(new Url("http://value1.com"));
        
        ExternalID id3 = new ExternalID();
        id3.setRelationship(Relationship.SELF);
        id3.setType("doi");
        id3.setValue("value1");
        id3.setUrl(new Url("http://value1.com"));
        
        extIds.getExternalIdentifier().add(id1);
        extIds.getExternalIdentifier().add(id2);
        extIds.getExternalIdentifier().add(id3);
        
        validator.validatePeerReview(extIds);
    }
    
    @Test
    public void testValidExtIdsWorksFine_flagOn() {
        validator.setRequireRelationshipOnExternalIdentifier(true);
        ExternalIDs extIds = new ExternalIDs();
        
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("doi");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));
                
        ExternalID id2 = new ExternalID();
        id2.setRelationship(Relationship.SELF);
        id2.setType("doi");
        id2.setValue("value1");
        id2.setUrl(new Url("http://value1.com"));
        
        ExternalID id3 = new ExternalID();
        id3.setRelationship(Relationship.SELF);
        id3.setType("doi");
        id3.setValue("value1");
        id3.setUrl(new Url("http://value1.com"));
        
        extIds.getExternalIdentifier().add(id1);
        extIds.getExternalIdentifier().add(id2);
        extIds.getExternalIdentifier().add(id3);
        
        validator.validatePeerReview(extIds);
    }
    
    @Test
    public void testValidExtIdsWorksFine_flagOff() {
        ExternalIDs extIds = new ExternalIDs();
        
        ExternalID id1 = new ExternalID();
        id1.setRelationship(null);
        id1.setType("doi");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));
                
        ExternalID id2 = new ExternalID();
        id2.setRelationship(null);
        id2.setType("doi");
        id2.setValue("value1");
        id2.setUrl(new Url("http://value1.com"));
        
        ExternalID id3 = new ExternalID();
        id3.setRelationship(null);
        id3.setType("doi");
        id3.setValue("value1");
        id3.setUrl(new Url("http://value1.com"));
        
        extIds.getExternalIdentifier().add(id1);
        extIds.getExternalIdentifier().add(id2);
        extIds.getExternalIdentifier().add(id3);
        
        validator.validatePeerReview(extIds);
    }
    
    @Test
    public void testValidateFunding(){
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("grant_number");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));
        ExternalIDs ids = new ExternalIDs();
        ids.getExternalIdentifier().add(id1);        
        validator.validateFunding(ids);

        ExternalID id2 = new ExternalID();
        id2.setRelationship(Relationship.SELF);
        id2.setType("INVALID");
        id2.setValue("value2");
        id2.setUrl(new Url("http://value1.com"));        
        ids.getExternalIdentifier().add(id2);

        //IDS one valid, one invalid
        try{
            validator.validateFunding(ids);
            fail("no exception thrown for invalid type");
        }catch(Exception e){
            if (!(e instanceof ActivityIdentifierValidationException))
                throw e;
        }

        //both valid
        id2.setType("grant_number");
        validator.validateFunding(ids);
        
        //IDS one valid, one invalid due to null
        id2.setType(null);
        try{
            validator.validateFunding(ids);
            fail("no exception thrown for invalid type");
        }catch(Exception e){
            if (!(e instanceof ActivityIdentifierValidationException))
                throw e;
        }
    }
    
    @Test(expected = ActivityIdentifierValidationException.class)
    public void testEmptyRelationshipOnFundingExternalIds_flagOn() {
        validator.setRequireRelationshipOnExternalIdentifier(true);
        ExternalID id1 = new ExternalID();
        id1.setRelationship(null);
        id1.setType("grant_number");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));
        ExternalIDs ids = new ExternalIDs();
        ids.getExternalIdentifier().add(id1);        
        validator.validateFunding(ids);
        fail("no exception thrown for invalid type");
    }

    @Test
    public void testEmptyRelationshipOnFundingExternalIds_flagOff() {
        ExternalID id1 = new ExternalID();
        id1.setRelationship(null);
        id1.setType("grant_number");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));
        ExternalIDs ids = new ExternalIDs();
        ids.getExternalIdentifier().add(id1);        
        validator.validateFunding(ids);
    }
    
    @Test
    public void testValidateNotificationItems(){
        Item i = new Item();
        Item i2 = new Item();
        Items items = new Items();  
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("doi");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));
        ExternalID id2 = new ExternalID();
        id2.setRelationship(Relationship.SELF);
        id2.setType("source-work-id");
        id2.setValue("value2");
        id2.setUrl(new Url("http://value1.com"));  
        
        i.setExternalIdentifier(id1);
        i2.setExternalIdentifier(id2);
        items.getItems().add(i);
        items.getItems().add(i2);
        
        //both valid
        validator.validateNotificationItems(items);
        
        //IDS one valid, one invalid
        id2.setType("blah");
        try{
            validator.validateNotificationItems(items);
            fail("no exception thrown for invalid type");
        }catch(Exception e){
            if (!(e instanceof ActivityIdentifierValidationException))
                throw e;
        }
        
        //IDS one valid, one VALID due to null (at least we have to do this if we want other tests to pass!)
        id2.setType(null);
        validator.validateNotificationItems(items);        
    }
    
    @Test(expected = ActivityIdentifierValidationException.class)
    public void testEmptyRelationshipOnNotificationItemExternalIds_flagOn() {
        validator.setRequireRelationshipOnExternalIdentifier(true);
        
        Item i = new Item();
        Item i2 = new Item();
        Items items = new Items();  
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("doi");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));
        ExternalID id2 = new ExternalID();
        id2.setRelationship(null);
        id2.setType("source-work-id");
        id2.setValue("value2");
        id2.setUrl(new Url("http://value1.com"));  
        
        i.setExternalIdentifier(id1);
        i2.setExternalIdentifier(id2);
        items.getItems().add(i);
        items.getItems().add(i2);
        
        //both valid
        validator.validateNotificationItems(items);
        fail("no exception thrown for invalid type");
    }
    
    @Test
    public void testEmptyRelationshipOnNotificationItemExternalIds_flagOff() {        
        Item i = new Item();
        Item i2 = new Item();
        Items items = new Items();  
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("doi");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));
        ExternalID id2 = new ExternalID();
        id2.setRelationship(null);
        id2.setType("source-work-id");
        id2.setValue("value2");
        id2.setUrl(new Url("http://value1.com"));  
        
        i.setExternalIdentifier(id1);
        i2.setExternalIdentifier(id2);
        items.getItems().add(i);
        items.getItems().add(i2);
        
        //both valid
        validator.validateNotificationItems(items);
    }
}
