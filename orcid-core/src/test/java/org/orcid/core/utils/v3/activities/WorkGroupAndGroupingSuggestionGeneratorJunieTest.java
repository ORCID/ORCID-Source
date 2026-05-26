package org.orcid.core.utils.v3.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;

public class WorkGroupAndGroupingSuggestionGeneratorJunieTest {

    @Test
    public void testFourGroupsOneSuggestion() {
        WorkGroupAndGroupingSuggestionGeneratorJunie generator = new WorkGroupAndGroupingSuggestionGeneratorJunie();
        for (WorkSummary summary : getWorkSummariesFourGroupsOneSuggestion()) {
            generator.group(summary);
        }
        assertEquals(4, generator.getGroups().size());
        
        List<WorkGroupingSuggestion> suggestions = generator.getGroupingSuggestions("orcid");
        assertEquals(1, suggestions.size());
        assertPutCodesEqual("4,7", suggestions.get(0).getPutCodesAsString());
        assertEquals("orcid", suggestions.get(0).getOrcid());
    }
    
    @Test
    public void testFiveGroupsTwoSuggestions() {
        WorkGroupAndGroupingSuggestionGeneratorJunie generator = new WorkGroupAndGroupingSuggestionGeneratorJunie();
        for (WorkSummary summary : getWorkSummariesFiveGroupsTwoSuggestions()) {
            generator.group(summary);
        }
        assertEquals(5, generator.getGroups().size());
        
        List<WorkGroupingSuggestion> suggestions = generator.getGroupingSuggestions("orcid");
        assertEquals(2, suggestions.size());
        
        List<String> suggestionPutCodes = suggestions.stream()
                .map(WorkGroupingSuggestion::getPutCodesAsString)
                .collect(Collectors.toList());
        
        assertTrue(containsPutCodes(suggestionPutCodes, "4,7"));
        assertTrue(containsPutCodes(suggestionPutCodes, "1,2,3,8"));
        
        for (WorkGroupingSuggestion suggestion : suggestions) {
            assertEquals("orcid", suggestion.getOrcid());
        }
    }
    
    @Test
    public void testNoGroupableIDs() {
        WorkGroupAndGroupingSuggestionGeneratorJunie generator = new WorkGroupAndGroupingSuggestionGeneratorJunie();
        for (WorkSummary summary : getWorkSummariesNoGroupableIds()) {
            generator.group(summary);
        }
        List<WorkGroupingSuggestion> suggestions = generator.getGroupingSuggestions("orcid");
        assertEquals(0, suggestions.size());
    }
    
    @Test
    public void testLargeNumberOfWorks() {
        int numWorks = 2000;
        List<WorkSummary> works = new ArrayList<>();
        for (int i = 0; i < numWorks; i++) {
            WorkSummary summary = new WorkSummary();
            summary.setPutCode((long) i);
            summary.setTitle(getWorkTitle("Title " + (i % 200))); // 200 unique titles
            
            ExternalIDs ids = new ExternalIDs();
            ids.getExternalIdentifier().add(getExternalID("doi", "doi-" + i)); // unique DOI
            summary.setExternalIdentifiers(ids);
            works.add(summary);
        }
        
        // Warmup
        for (int i=0; i<10; i++) {
            WorkGroupAndGroupingSuggestionGeneratorJunie generator = new WorkGroupAndGroupingSuggestionGeneratorJunie();
            for (WorkSummary summary : works) generator.group(summary);
            generator.getGroupingSuggestions("orcid");
            
            WorkGroupAndGroupingSuggestionGenerator original = new WorkGroupAndGroupingSuggestionGenerator();
            for (WorkSummary summary : works) original.group(summary);
            original.getGroupingSuggestions("orcid");
        }

        long start = System.nanoTime();
        WorkGroupAndGroupingSuggestionGeneratorJunie generator = new WorkGroupAndGroupingSuggestionGeneratorJunie();
        for (WorkSummary summary : works) {
            generator.group(summary);
        }
        generator.getGroupingSuggestions("orcid");
        long end = System.nanoTime();
        System.out.println("[DEBUG_LOG] Junie generator took " + (end - start) / 1000000.0 + "ms for " + numWorks + " works");
        
        start = System.nanoTime();
        WorkGroupAndGroupingSuggestionGenerator original = new WorkGroupAndGroupingSuggestionGenerator();
        for (WorkSummary summary : works) {
            original.group(summary);
        }
        original.getGroupingSuggestions("orcid");
        end = System.nanoTime();
        System.out.println("[DEBUG_LOG] Original generator took " + (end - start) / 1000000.0 + "ms for " + numWorks + " works");
    }

    private void assertPutCodesEqual(String expected, String actual) {
        List<String> expectedList = Arrays.asList(expected.split(","));
        List<String> actualList = Arrays.asList(actual.split(","));
        Collections.sort(expectedList);
        Collections.sort(actualList);
        assertEquals(expectedList, actualList);
    }
    
    private boolean containsPutCodes(List<String> suggestionPutCodes, String target) {
        List<String> targetList = Arrays.asList(target.split(","));
        Collections.sort(targetList);
        for (String s : suggestionPutCodes) {
            List<String> sList = Arrays.asList(s.split(","));
            Collections.sort(sList);
            if (sList.equals(targetList)) {
                return true;
            }
        }
        return false;
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
