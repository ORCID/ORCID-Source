package org.orcid.core.utils.v3.activities;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.orcid.jaxb.model.v3.rc2.common.Title;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc2.record.Relationship;
import org.orcid.jaxb.model.v3.rc2.record.WorkTitle;
import org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public class WorkGroupAndGroupingSuggestionGeneratorTest {

    @Test
    public void testFourGroupsOneSuggestion() {
        WorkGroupAndGroupingSuggestionGenerator generator = new WorkGroupAndGroupingSuggestionGenerator();
        for (WorkSummary summary : getWorkSummariesFourGroupsOneSuggestion()) {
            generator.group(summary);
        }
        assertEquals(4, generator.getGroups().size());
        
        List<WorkGroupingSuggestion> suggestions = generator.getGroupingSuggestions("orcid");
        assertEquals(1, suggestions.size());
        assertEquals("4,7", suggestions.get(0).getPutCodesAsString());
        assertEquals("orcid", suggestions.get(0).getOrcid());
    }
    
    @Test
    public void testFiveGroupsTwoSuggestions() {
        WorkGroupAndGroupingSuggestionGenerator generator = new WorkGroupAndGroupingSuggestionGenerator();
        for (WorkSummary summary : getWorkSummariesFiveGroupsTwoSuggestions()) {
            generator.group(summary);
        }
        assertEquals(5, generator.getGroups().size());
        
        List<WorkGroupingSuggestion> suggestions = generator.getGroupingSuggestions("orcid");
        assertEquals(2, suggestions.size());
        assertEquals("4,7", suggestions.get(0).getPutCodesAsString());
        assertEquals("orcid", suggestions.get(0).getOrcid());
        assertEquals("1,2,3,8", suggestions.get(1).getPutCodesAsString());
        assertEquals("orcid", suggestions.get(1).getOrcid());
    }
    
    @Test
    public void testNoGroupableIDs() {
        WorkGroupAndGroupingSuggestionGenerator generator = new WorkGroupAndGroupingSuggestionGenerator();
        for (WorkSummary summary : getWorkSummariesNoGroupableIds()) {
            generator.group(summary);
        }
        List<WorkGroupingSuggestion> suggestions = generator.getGroupingSuggestions("orcid");
        assertEquals(0, suggestions.size());
    }

    private List<WorkSummary> getWorkSummariesFourGroupsOneSuggestion() {
        WorkSummary first = new WorkSummary();
        first.setPutCode(1L);
        first.setTitle(getWorkTitle("a title"));

        ExternalIDs firstIds = new ExternalIDs();
        firstIds.getExternalIdentifier().add(getExternalID("doi", "group1"));
        first.setExternalIdentifiers(firstIds);

        WorkSummary second = new WorkSummary();
        second.setPutCode(2L);
        second.setTitle(getWorkTitle("second title"));
        
        ExternalIDs secondIds = new ExternalIDs();
        secondIds.getExternalIdentifier().add(getExternalID("doi", "group1"));
        second.setExternalIdentifiers(secondIds);
        
        WorkSummary third = new WorkSummary();
        third.setPutCode(3L);
        third.setTitle(getWorkTitle("SECOND title"));
        
        ExternalIDs thirdIds = new ExternalIDs();
        thirdIds.getExternalIdentifier().add(getExternalID("doi", "group1"));
        third.setExternalIdentifiers(thirdIds);
        
        WorkSummary fourth = new WorkSummary();
        fourth.setPutCode(4L);
        fourth.setTitle(getWorkTitle("something totally different"));
        
        ExternalIDs fourthIds = new ExternalIDs();
        fourthIds.getExternalIdentifier().add(getExternalID("doi", "group2"));
        fourth.setExternalIdentifiers(fourthIds);
        
        WorkSummary fifth = new WorkSummary();
        fifth.setPutCode(5L);
        fifth.setTitle(getWorkTitle("third title"));
        
        ExternalIDs fifthIds = new ExternalIDs();
        fifthIds.getExternalIdentifier().add(getExternalID("doi", "group3"));
        fifth.setExternalIdentifiers(fifthIds);
        
        WorkSummary sixth = new WorkSummary();
        sixth.setPutCode(6L);
        sixth.setTitle(getWorkTitle("3rd title"));
        
        ExternalIDs sixthIds = new ExternalIDs();
        sixthIds.getExternalIdentifier().add(getExternalID("doi", "group3"));
        sixth.setExternalIdentifiers(sixthIds);
        
        WorkSummary seventh = new WorkSummary();
        seventh.setPutCode(7L);
        seventh.setTitle(getWorkTitle("SOMETHINGTOTALLYDIFFERENT"));
        
        ExternalIDs seventhIds = new ExternalIDs();
        seventhIds.getExternalIdentifier().add(getExternalID("doi", "group4"));
        seventh.setExternalIdentifiers(seventhIds);
        
        return Arrays.asList(first, second, third, fourth, fifth, sixth, seventh);
    }
    
    private List<WorkSummary> getWorkSummariesFiveGroupsTwoSuggestions() {
        WorkSummary first = new WorkSummary();
        first.setPutCode(1L);
        first.setTitle(getWorkTitle("a title"));

        ExternalIDs firstIds = new ExternalIDs();
        firstIds.getExternalIdentifier().add(getExternalID("doi", "group1"));
        first.setExternalIdentifiers(firstIds);

        WorkSummary second = new WorkSummary();
        second.setPutCode(2L);
        second.setTitle(getWorkTitle("second title"));
        
        ExternalIDs secondIds = new ExternalIDs();
        secondIds.getExternalIdentifier().add(getExternalID("doi", "group1"));
        second.setExternalIdentifiers(secondIds);
        
        WorkSummary third = new WorkSummary();
        third.setPutCode(3L);
        third.setTitle(getWorkTitle("SECOND title"));
        
        ExternalIDs thirdIds = new ExternalIDs();
        thirdIds.getExternalIdentifier().add(getExternalID("doi", "group1"));
        third.setExternalIdentifiers(thirdIds);
        
        WorkSummary fourth = new WorkSummary();
        fourth.setPutCode(4L);
        fourth.setTitle(getWorkTitle("something totally different"));
        
        ExternalIDs fourthIds = new ExternalIDs();
        fourthIds.getExternalIdentifier().add(getExternalID("doi", "group2"));
        fourth.setExternalIdentifiers(fourthIds);
        
        WorkSummary fifth = new WorkSummary();
        fifth.setPutCode(5L);
        fifth.setTitle(getWorkTitle("third title"));
        
        ExternalIDs fifthIds = new ExternalIDs();
        fifthIds.getExternalIdentifier().add(getExternalID("doi", "group3"));
        fifth.setExternalIdentifiers(fifthIds);
        
        WorkSummary sixth = new WorkSummary();
        sixth.setPutCode(6L);
        sixth.setTitle(getWorkTitle("3rd title"));
        
        ExternalIDs sixthIds = new ExternalIDs();
        sixthIds.getExternalIdentifier().add(getExternalID("doi", "group3"));
        sixth.setExternalIdentifiers(sixthIds);
        
        WorkSummary seventh = new WorkSummary();
        seventh.setPutCode(7L);
        seventh.setTitle(getWorkTitle("SOMETHINGTOTALLYDIFFERENT"));
        
        ExternalIDs seventhIds = new ExternalIDs();
        seventhIds.getExternalIdentifier().add(getExternalID("doi", "group4"));
        seventh.setExternalIdentifiers(seventhIds);
        
        WorkSummary eighth = new WorkSummary();
        eighth.setPutCode(8L);
        eighth.setTitle(getWorkTitle("ATITLE"));
        
        ExternalIDs eighthIds = new ExternalIDs();
        eighthIds.getExternalIdentifier().add(getExternalID("doi", "group5"));
        eighth.setExternalIdentifiers(eighthIds);
        
        return Arrays.asList(first, second, third, fourth, fifth, sixth, seventh, eighth);
    }
    
    private List<WorkSummary> getWorkSummariesNoGroupableIds() {
        WorkSummary first = new WorkSummary();
        first.setPutCode(1L);
        first.setTitle(getWorkTitle("a title"));

        ExternalIDs firstIds = new ExternalIDs();
        ExternalID externalID = getExternalID("issn", "1234-5678");
        externalID.setRelationship(Relationship.PART_OF);
        firstIds.getExternalIdentifier().add(externalID);
        first.setExternalIdentifiers(firstIds);

        WorkSummary second = new WorkSummary();
        second.setPutCode(2L);
        second.setTitle(getWorkTitle("second title"));
        
        ExternalIDs secondIds = new ExternalIDs();
        ExternalID secondExternalID = getExternalID("issn", "1234-5678");
        secondExternalID.setRelationship(Relationship.PART_OF);
        secondIds.getExternalIdentifier().add(secondExternalID);
        second.setExternalIdentifiers(secondIds);
        
        return Arrays.asList(first, second);
    }

    private WorkTitle getWorkTitle(String title) {
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title(title));
        return workTitle;
    }

    private ExternalID getExternalID(String type, String value) {
        ExternalID id = new ExternalID();
        id.setType(type);
        id.setValue(value);
        return id;
    }

}
