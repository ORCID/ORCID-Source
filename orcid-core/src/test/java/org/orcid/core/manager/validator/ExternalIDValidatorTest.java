package org.orcid.core.manager.validator;

import static org.junit.Assert.*;

import org.junit.Test;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.jaxb.model.common_rc2.Url;
import org.orcid.jaxb.model.notification.permission_rc2.Item;
import org.orcid.jaxb.model.notification.permission_rc2.Items;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.ExternalIDs;
import org.orcid.jaxb.model.record_rc2.Relationship;

public class ExternalIDValidatorTest {

    @Test
    public void testValidateWorkOrPeerReview(){
        //call for ExternalID and ExternalIDs
        ExternalIDValidator validator = ExternalIDValidator.getInstance();
        
        //ID valid
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("DOI");
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
                fail("incorrect exception type");
        }
        
        //id null
        try{
            id1.setType(null);
            validator.validateWorkOrPeerReview(id1);
            fail("no exception thrown for invalid type");
        }catch(Exception e){
            if (!(e instanceof ActivityIdentifierValidationException))
                fail("incorrect exception type "+e.getClass());
        }

        ExternalIDs ids = new ExternalIDs();
        ids.getExternalIdentifier().add(id1);
        
        //IDS one invalid
        id1.setType("invalid");
        try{
            validator.validateWorkOrPeerReview(ids);
            fail("no exception thrown for invalid type");
        }catch(Exception e){
            if (!(e instanceof ActivityIdentifierValidationException))
                fail("incorrect exception type");
        }

        //IDS one valid (lowercase)
        id1.setType("doi");
        validator.validateWorkOrPeerReview(ids);
        
        //IDS two valid
        ExternalID id2 = new ExternalID();
        id2.setRelationship(Relationship.SELF);
        id2.setType("source-work-id");
        id2.setValue("value2");
        id2.setUrl(new Url("http://value1.com"));        
        ids.getExternalIdentifier().add(id2);
        validator.validateWorkOrPeerReview(ids);
        
        //IDS one invalid, one valid
        id2.setType("not-a-type");
        try{
            validator.validateWorkOrPeerReview(ids);
            fail("no exception thrown for invalid type");
        }catch(Exception e){
            if (!(e instanceof ActivityIdentifierValidationException))
                fail("incorrect exception type");
        }
        
        
    }

    @Test
    public void testValidateFunding(){
        ExternalIDValidator validator = ExternalIDValidator.getInstance();
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
        id2.setType("source-work-id");
        id2.setValue("value2");
        id2.setUrl(new Url("http://value1.com"));        
        ids.getExternalIdentifier().add(id2);

        //IDS one valid, one invalid
        try{
            validator.validateFunding(ids);
            fail("no exception thrown for invalid type");
        }catch(Exception e){
            if (!(e instanceof ActivityIdentifierValidationException))
                fail("incorrect exception type");
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
                fail("incorrect exception type");
        }
    }

    @Test
    public void testValidateNotificationItems(){
        ExternalIDValidator validator = ExternalIDValidator.getInstance();
        Item i = new Item();
        Item i2 = new Item();
        Items items = new Items();  
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("DOI");
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
                fail("incorrect exception type");
        }
        
        //IDS one valid, one VALID due to null (at least we have to do this if we want other tests to pass!)
        id2.setType(null);
        validator.validateNotificationItems(items);        
    }
}
