package org.orcid.core.manager.v3.validator;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.jaxb.model.v3.release.notification.permission.Items;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.pojo.IdentifierType;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ExternalIDValidatorTest {

    /**
     * The identifier types that really exist in the shipped identifier_type reference data.
     * That those rows exist is asserted by the database backed
     * org.orcid.core.manager.IdentifierTypeManagerTest; here they are supplied explicitly.
     */
    private static final List<String> KNOWN_TYPES = Arrays.asList("doi", "source-work-id", "grant_number");

    @Mock
    private IdentifierTypeManager identifierTypeManager;

    @InjectMocks
    private ExternalIDValidator validator;

    @Before
    public void before() {
        Map<String, IdentifierType> types = new HashMap<String, IdentifierType>();
        for (String name : KNOWN_TYPES) {
            IdentifierType type = new IdentifierType();
            type.setName(name);
            types.put(name, type);
        }
        // production always passes a literal null Locale
        when(identifierTypeManager.fetchIdentifierTypesByAPITypeName((Locale) null)).thenReturn(types);
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
        validator.validateWork(externalIds, false);

        try {
            id1.setType("invalid");
            validator.validateWork(externalIds, false);
            fail("no exception thrown for invalid type");
        } catch (Exception e) {
            if (!(e instanceof ActivityIdentifierValidationException))
                throw e;
        }

        try {
            id1.setType(null);
            validator.validateWork(externalIds, false);
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
        validator.validateWork(externalIds, false);

        // IDS one invalid, one valid
        id2.setType("not-a-type");
        try {
            validator.validateWork(externalIds, false);
            fail("no exception thrown for invalid type");
        } catch (Exception e) {
            if (!(e instanceof ActivityIdentifierValidationException))
                throw e;
        }

        // missing 'self' external id
        id1.setRelationship(Relationship.PART_OF);
        id2.setRelationship(Relationship.PART_OF);
        try {
            validator.validateWork(externalIds, true);
            fail("no exception thrown for invalid type");
        } catch (Exception e) {
            if (!(e instanceof ActivityIdentifierValidationException))
                throw e;
        }
    }

    @Test(expected = ActivityIdentifierValidationException.class)
    public void testEmptyRelationshipOnSingleExternalId() {
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
    
    @Test(expected = ActivityIdentifierValidationException.class)
    public void testEmptyRelationshipOnExternalIds() {
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
    public void testValidExtIdsWorksFine() {
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
    public void testEmptyRelationshipOnFundingExternalIds() {
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
    public void testEmptyRelationshipOnNotificationItemExternalIds() {
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

    /**
     * The "external id with an empty value is rejected" rule. It used to be proven only
     * indirectly through ActivityValidator; it is proven here, at the class that enforces it.
     */
    @Test(expected = ActivityIdentifierValidationException.class)
    public void testEmptyValueOnSingleExternalId() {
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("doi");
        id1.setValue("");
        id1.setUrl(new Url("http://value1.com"));
        validator.validateWorkOrPeerReview(id1);
        fail("no exception thrown for empty value");
    }

    @Test(expected = ActivityIdentifierValidationException.class)
    public void testEmptyValueOnWorkExternalIds() {
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("doi");
        id1.setValue("");
        id1.setUrl(new Url("http://value1.com"));

        ExternalIDs externalIds = new ExternalIDs();
        externalIds.getExternalIdentifier().add(id1);
        validator.validateWork(externalIds, false);
        fail("no exception thrown for empty value");
    }

    @Test(expected = ActivityIdentifierValidationException.class)
    public void testEmptyValueOnPeerReviewExternalIds() {
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("doi");
        id1.setValue(null);
        id1.setUrl(new Url("http://value1.com"));

        ExternalIDs externalIds = new ExternalIDs();
        externalIds.getExternalIdentifier().add(id1);
        validator.validatePeerReview(externalIds);
        fail("no exception thrown for empty value");
    }

    @Test(expected = ActivityIdentifierValidationException.class)
    public void testEmptyValueOnFundingExternalIds() {
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("grant_number");
        id1.setValue("");
        id1.setUrl(new Url("http://value1.com"));

        ExternalIDs externalIds = new ExternalIDs();
        externalIds.getExternalIdentifier().add(id1);
        validator.validateFunding(externalIds);
        fail("no exception thrown for empty value");
    }

    @Test(expected = ActivityIdentifierValidationException.class)
    public void testVersionOfWithoutSelfOnPeerReviewExternalIds() {
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.VERSION_OF);
        id1.setType("doi");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));

        ExternalIDs externalIds = new ExternalIDs();
        externalIds.getExternalIdentifier().add(id1);
        validator.validatePeerReview(externalIds);
        fail("no exception thrown for version-of without self");
    }

    @Test
    public void testVersionOfWithSelfOnPeerReviewExternalIds() {
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.VERSION_OF);
        id1.setType("doi");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));

        ExternalID id2 = new ExternalID();
        id2.setRelationship(Relationship.SELF);
        id2.setType("doi");
        id2.setValue("value2");
        id2.setUrl(new Url("http://value2.com"));

        ExternalIDs externalIds = new ExternalIDs();
        externalIds.getExternalIdentifier().add(id1);
        externalIds.getExternalIdentifier().add(id2);
        validator.validatePeerReview(externalIds);
    }
}
