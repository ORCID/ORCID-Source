package org.orcid.pojo.ajaxForm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.orcid.jaxb.model.v3.rc2.record.Affiliation;
import org.orcid.jaxb.model.v3.rc2.record.summary.AffiliationSummary;

public class AffiliationFormTest extends AffiliationFormTestBase {    
        
    @Test
    public void equalsTest() {
        AffiliationForm f1 = getAffiliationForm();
        AffiliationForm f2 = getAffiliationForm();
        assertTrue(f1.equals(f2));
    }
    
    @Test
    public void fromAffiliationSummaryTest() {
        AffiliationForm f1 = getAffiliationForm();
        AffiliationSummary s1 = getAffiliationSummary();
        // Summary doesn't have url
        f1.setUrl(new Text());
        assertEquals(f1, AffiliationForm.valueOf(s1));
    }
    
    @Test
    public void fromAffiliationTest() {
        AffiliationForm f1 = getAffiliationForm();
        Affiliation aff = getAffiliation();
        assertEquals(f1, AffiliationForm.valueOf(aff));
    }
    
    @Test
    public void toAffiliationTest() {
        AffiliationForm f1 = getAffiliationForm();
        Affiliation aff = getAffiliation();
        assertEquals(aff, f1.toAffiliation());
    }        
}
