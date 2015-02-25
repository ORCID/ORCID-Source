package org.orcid.core.utils.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.orcid.jaxb.model.record.ExternalIdentifier;
import org.orcid.jaxb.model.record.Title;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.WorkExternalIdentifier;
import org.orcid.jaxb.model.record.WorkExternalIdentifierId;
import org.orcid.jaxb.model.record.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record.WorkExternalIdentifiers;
import org.orcid.jaxb.model.record.WorkTitle;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class ActivitiesGroupGeneratorTest {
    @Test
    public void groupWorks_4GroupsOf1Work_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, Work> works = generateWorks();
        
        //Group the first group
        //work-1 -> ARG(A), ARG(B), ARG(C)
        Work work1 = works.get("work-1");
        generator.group(work1);
        //There should be one group, and the ext ids should be A, B and C
        List<ActivitiesGroup> groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(1, groups.size());
        ActivitiesGroup g1 = groups.get(0);
        assertNotNull(g1);
        assertNotNull(g1.getActivities());
        assertEquals(1, g1.getActivities().size());
        assertTrue(g1.getActivities().contains(work1));
        assertNotNull(g1.getExternalIdentifiers());
        assertEquals(3, g1.getExternalIdentifiers().size());
        checkWorkExternalIdentifiers(work1, g1);
        
        //Add another work to the groups
        //work-5 -> ARG(M), ARG(N), ARG(O)
        Work work5 = works.get("work-5");
        generator.group(work5);
        //There should be two groups, one for each work
        groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(2, groups.size());
        //There should be one activity in each group         
        assertEquals(1, groups.get(0).getActivities().size());
        assertEquals(1, groups.get(1).getActivities().size());
        //There should be 3 ext ids in each group
        assertEquals(3, groups.get(0).getExternalIdentifiers().size());
        assertEquals(3, groups.get(1).getExternalIdentifiers().size());                
        //Check work in groups
        checkWorkIsOnGroups(work5, groups);
        
        //Add another work to the groups
        //work-6 -> ARXIV(A), ARXIV(B), ARXIV(C)
        Work work6 = works.get("work-6");
        generator.group(work6);
        groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(3, groups.size());
        //There should be one activity in each group         
        assertEquals(1, groups.get(0).getActivities().size());
        assertEquals(1, groups.get(1).getActivities().size());
        assertEquals(1, groups.get(2).getActivities().size());
        //There should be 3 ext ids in each group
        assertEquals(3, groups.get(0).getExternalIdentifiers().size());
        assertEquals(3, groups.get(1).getExternalIdentifiers().size());                
        assertEquals(3, groups.get(2).getExternalIdentifiers().size());
        //Check work in groups
        checkWorkIsOnGroups(work6, groups);
        
        //Add another work to the groups
        //work-7 -> No external identifiers  
        Work work7 = works.get("work-7");
        generator.group(work7);        
        groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(4, groups.size());
        //There should be one activity in each group         
        assertEquals(1, groups.get(0).getActivities().size());
        assertEquals(1, groups.get(1).getActivities().size());
        assertEquals(1, groups.get(2).getActivities().size());
        assertEquals(1, groups.get(3).getActivities().size());
        //There should be 3 ext ids in each group, except for one group that doesnt have any ext id
        boolean work7found = false;
        for(int i = 0; i < 4; i++) {
            if(groups.get(i).getExternalIdentifiers().size() == 0) {                                
                work7found = true;
            } else {
                assertEquals(3, groups.get(i).getExternalIdentifiers().size());
            }                                                        
        }
        assertTrue("Work without ext ids was not found", work7found);
        //Check work in groups
        checkWorkIsOnGroups(work7, groups);        
    }
        
    
    
    
    
    @Test
    public void groupWorks_1GroupsOf2Works_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, Work> works = generateWorks();
        
        //Group the first group
        //work-1 -> ARG(A), ARG(B), ARG(C)
        Work work1 = works.get("work-1");        
        //work-2 -> ARG(C), ARG(D), ARG(E)
        Work work2 = works.get("work-2");
        generator.group(work1);
        generator.group(work2);
        List<ActivitiesGroup> groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(1, groups.size());
        ActivitiesGroup g1 = groups.get(0);
        assertNotNull(g1);
        assertNotNull(g1.getActivities());
        assertEquals(2, g1.getActivities().size());
        assertTrue(g1.getActivities().contains(work1));
        assertTrue(g1.getActivities().contains(work2));
        assertNotNull(g1.getExternalIdentifiers());
        assertEquals(5, g1.getExternalIdentifiers().size());
        checkWorkExternalIdentifiers(work1, g1);
        checkWorkExternalIdentifiers(work2, g1);
    }
    
    
    
    
    
    
    /**
     * Check that a work belongs to any of the given groups, and, check that all his ext ids also belongs to the group
     * */
    public void checkWorkIsOnGroups(Work work, List<ActivitiesGroup> groups) {
        int groupIndex = -1;
        for(int i = 0; i < groups.size(); i++) {
            ActivitiesGroup group = groups.get(i);
            assertNotNull(group.getActivities());
            if(group.getActivities().contains(work)) {
                groupIndex = i;
                break;
            }
        }
        
        //Check the work belongs to a group
        assertFalse("Work doesnt belong to any group", -1 == groupIndex);
        ActivitiesGroup group = groups.get(groupIndex);
        //Check the external ids are contained in the group ext ids
        checkWorkExternalIdentifiers(work, group);
    }
    
    /**
     * Checks that all the external identifiers in the work are contained in the group external identifiers
     * */
    private void checkWorkExternalIdentifiers(Work work, ActivitiesGroup group) {
        WorkExternalIdentifiers workExtIdsContainer = work.getExternalIdentifiers();
        List<ExternalIdentifier> workExtIds = workExtIdsContainer.getExternalIdentifier();
        Set<ExternalIdentifier> groupExtIds = group.getExternalIdentifiers();
        for(ExternalIdentifier workExtId : workExtIds) {
            assertTrue(groupExtIds.contains(workExtId));
        }
    }
    
    /**
     * work-1 -> ARG(A), ARG(B), ARG(C) 
     * work-2 -> ARG(C), ARG(D), ARG(E)
     * work-3 -> ARG(X), ARG(Y), ARG(Z)
     * work-4 -> ARG(Y), ARG(B), ARG(1)
     * work-5 -> ARG(M), ARG(N), ARG(O) 
     * work-6 -> ARXIV(A), ARXIV(B), ARXIV(C)  
     * work-7 -> No external identifiers  
     * */
    private Map<String, Work> generateWorks() {
        Map<String, Work> result = new HashMap<String, Work>();
        for(int i = 1; i < 8; i++) {
            String title = "work-" + i;
            Work work = new Work();
            //Set title
            WorkTitle workTitle = new WorkTitle();
            workTitle.setTitle(new Title(title));
            work.setWorkTitle(workTitle);            
            WorkExternalIdentifiers wei = new WorkExternalIdentifiers();
            switch (i) {
            case 1:
                WorkExternalIdentifier e1 = new WorkExternalIdentifier();
                e1.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("A"));
                WorkExternalIdentifier e2 = new WorkExternalIdentifier();
                e2.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("B"));
                WorkExternalIdentifier e3 = new WorkExternalIdentifier();
                e3.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e3.setWorkExternalIdentifierId(new WorkExternalIdentifierId("C"));
                wei.getExternalIdentifier().add(e1);
                wei.getExternalIdentifier().add(e2);
                wei.getExternalIdentifier().add(e3);
                break;
            case 2:
                WorkExternalIdentifier e11 = new WorkExternalIdentifier();
                e11.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e11.setWorkExternalIdentifierId(new WorkExternalIdentifierId("C"));
                WorkExternalIdentifier e21 = new WorkExternalIdentifier();
                e21.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e21.setWorkExternalIdentifierId(new WorkExternalIdentifierId("D"));
                WorkExternalIdentifier e31 = new WorkExternalIdentifier();
                e31.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e31.setWorkExternalIdentifierId(new WorkExternalIdentifierId("E"));
                wei.getExternalIdentifier().add(e11);
                wei.getExternalIdentifier().add(e21);
                wei.getExternalIdentifier().add(e31);
                break;
            case 3: 
                WorkExternalIdentifier e111 = new WorkExternalIdentifier();
                e111.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e111.setWorkExternalIdentifierId(new WorkExternalIdentifierId("X"));
                WorkExternalIdentifier e211 = new WorkExternalIdentifier();
                e211.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e211.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Y"));
                WorkExternalIdentifier e311 = new WorkExternalIdentifier();
                e311.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e311.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Z"));
                wei.getExternalIdentifier().add(e111);
                wei.getExternalIdentifier().add(e211);
                wei.getExternalIdentifier().add(e311);
                break;
            case 4: 
                WorkExternalIdentifier e1111 = new WorkExternalIdentifier();
                e1111.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e1111.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Y"));
                WorkExternalIdentifier e2111 = new WorkExternalIdentifier();
                e2111.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e2111.setWorkExternalIdentifierId(new WorkExternalIdentifierId("B"));
                WorkExternalIdentifier e3111 = new WorkExternalIdentifier();
                e3111.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e3111.setWorkExternalIdentifierId(new WorkExternalIdentifierId("1"));
                wei.getExternalIdentifier().add(e1111);
                wei.getExternalIdentifier().add(e2111);
                wei.getExternalIdentifier().add(e3111);
                break;
            case 5:
                WorkExternalIdentifier e11111 = new WorkExternalIdentifier();
                e11111.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e11111.setWorkExternalIdentifierId(new WorkExternalIdentifierId("M"));
                WorkExternalIdentifier e21111 = new WorkExternalIdentifier();
                e21111.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e21111.setWorkExternalIdentifierId(new WorkExternalIdentifierId("N"));
                WorkExternalIdentifier e31111 = new WorkExternalIdentifier();
                e31111.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e31111.setWorkExternalIdentifierId(new WorkExternalIdentifierId("O"));
                wei.getExternalIdentifier().add(e11111);
                wei.getExternalIdentifier().add(e21111);
                wei.getExternalIdentifier().add(e31111);
                break;
            case 6: 
                WorkExternalIdentifier e111111 = new WorkExternalIdentifier();
                e111111.setWorkExternalIdentifierType(WorkExternalIdentifierType.ARXIV);
                e111111.setWorkExternalIdentifierId(new WorkExternalIdentifierId("A"));
                WorkExternalIdentifier e211111 = new WorkExternalIdentifier();
                e211111.setWorkExternalIdentifierType(WorkExternalIdentifierType.ARXIV);
                e211111.setWorkExternalIdentifierId(new WorkExternalIdentifierId("B"));
                WorkExternalIdentifier e311111 = new WorkExternalIdentifier();
                e311111.setWorkExternalIdentifierType(WorkExternalIdentifierType.ARXIV);
                e311111.setWorkExternalIdentifierId(new WorkExternalIdentifierId("C"));
                wei.getExternalIdentifier().add(e111111);
                wei.getExternalIdentifier().add(e211111);
                wei.getExternalIdentifier().add(e311111);
                break;
            }
            work.setWorkExternalIdentifiers(wei);
            result.put(title, work);
        }
        return result;
    }        
}

















