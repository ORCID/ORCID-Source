package org.orcid.core.utils.v3.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.junit.Test;
import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;

public class WorkComparatorsTest {
    
    @Test
    public void testSortWorkSummariesWithDifferentDisplayIndexes() throws DatatypeConfigurationException {
        List<WorkSummary> summaries = getWorkSummariesWithOrderedDisplayIndexes(0);
        assertEquals(10, summaries.size()); // for reasons stated in below comment
        
        Collections.shuffle(summaries);
        summaries.sort(WorkComparators.WORKS_WITHIN_GROUP);
        
        int highestDisplayIndex = 10; // assumes list is size 10, see comment above
        for (WorkSummary summary : summaries) {
            assertTrue(Integer.valueOf(summary.getDisplayIndex()) < highestDisplayIndex);
            highestDisplayIndex = Integer.valueOf(summary.getDisplayIndex());
        }
    }
    
    @Test
    public void testSortWorkSummariesWithEqualDisplayIndexes() throws DatatypeConfigurationException {
        List<WorkSummary> summaries = getWorkSummariesWithEqualDisplayIndexes(0);
        assertEquals(10, summaries.size()); // for reasons stated in below comment
        
        Collections.shuffle(summaries);
        summaries.sort(WorkComparators.WORKS_WITHIN_GROUP);
        
        GregorianCalendar yesterday = new GregorianCalendar();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        CreatedDate compare = new CreatedDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(yesterday));// assumes list is size 10, see comment above
        for (WorkSummary summary : summaries) {
            System.out.println(compare.getValue().toGregorianCalendar() + " should be before " + summary.getCreatedDate().getValue().toGregorianCalendar());
            assertTrue(compare.getValue().toGregorianCalendar().before(summary.getCreatedDate().getValue().toGregorianCalendar()));
            compare = summary.getCreatedDate();
        }
    }
    
    @Test
    public void testSortWorkGroupsWithWorkSummariesWithDifferentTitles() throws DatatypeConfigurationException {
        List<WorkGroup> groups = getWorkGroupsWithSummariesWithOrderedTitles();
        
        Collections.shuffle(groups);
        groups.sort(WorkComparators.GROUP);
        
        String lowestTitle = "summary"; // all titles should be 'summary' + int
        for (WorkGroup group : groups) {
            WorkSummary summary = group.getWorkSummary().get(0);
            assertTrue(Integer.valueOf(lowestTitle.compareTo(summary.getTitle().getTitle().getContent())) < 0);
            lowestTitle = summary.getTitle().getTitle().getContent();
        }
    }
    
    private List<WorkGroup> getWorkGroupsWithSummariesWithOrderedTitles() throws DatatypeConfigurationException {
        List<WorkGroup> groups = new ArrayList<>();
        for (int i = 0; i < 100; i+= 10) {
            WorkGroup workGroup = new WorkGroup();
            List<WorkSummary> summaries = getWorkSummariesWithOrderedDisplayIndexes(i);
            summaries.forEach(workGroup.getWorkSummary()::add);
            groups.add(workGroup);
        }
        return groups;
    }

    private List<WorkSummary> getWorkSummariesWithEqualDisplayIndexes(int offset) throws DatatypeConfigurationException {
        List<WorkSummary> summaries = new ArrayList<>();
        for (int i = offset; i < offset + 10; i++) {
            summaries.add(getWorkSummary(0));
        }
        return summaries;
    }

    private List<WorkSummary> getWorkSummariesWithOrderedDisplayIndexes(int offset) throws DatatypeConfigurationException {
        List<WorkSummary> summaries = new ArrayList<>();
        for (int i = offset; i < offset + 10; i++) {
            summaries.add(getWorkSummary(i));
        }
        return summaries;
    }

    private WorkSummary getWorkSummary(int displayIndex) throws DatatypeConfigurationException {
        WorkSummary summary = new WorkSummary();
        summary.setDisplayIndex(Integer.toString(displayIndex));
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("summary" + displayIndex));
        summary.setTitle(title);
        waitABit();
        summary.setCreatedDate(new CreatedDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar())));
        return summary;
    }

    private void waitABit() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException("Thread shouldn't be interrupted", e);
        }
    }

}
