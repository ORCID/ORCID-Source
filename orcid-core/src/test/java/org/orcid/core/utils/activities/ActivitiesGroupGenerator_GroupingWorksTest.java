/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.utils.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
public class ActivitiesGroupGenerator_GroupingWorksTest {
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
        //work-8 -> No external identifiers  
        Work work8 = works.get("work-8");
        generator.group(work8);        
        groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(4, groups.size());
        //There should be one activity in each group         
        assertEquals(1, groups.get(0).getActivities().size());
        assertEquals(1, groups.get(1).getActivities().size());
        assertEquals(1, groups.get(2).getActivities().size());
        assertEquals(1, groups.get(3).getActivities().size());
        //There should be 3 ext ids in each group, except for one group that doesnt have any ext id
        boolean work8found = false;
        for(int i = 0; i < 4; i++) {
            if(groups.get(i).getExternalIdentifiers().size() == 0) {                                
                work8found = true;
            } else {
                assertEquals(3, groups.get(i).getExternalIdentifiers().size());
            }                                                        
        }
        assertTrue("Work without ext ids was not found", work8found);
        //Check work in groups
        checkWorkIsOnGroups(work8, groups);        
    }
        
    /**
     * Test grouping work-1 and work-2 
     * */
    @Test
    public void groupWorks_1GroupsOf2Works_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, Work> works = generateWorks();
        
        Work work1 = works.get("work-1");        
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
     * Test grouping (work-1 and work-2) and (work-6 and work-7) 
     * */
    @Test
    public void groupWorks_2GroupsOf2Works_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, Work> works = generateWorks();
        
        Work work1 = works.get("work-1");        
        Work work2 = works.get("work-2");
        Work work6 = works.get("work-6");
        Work work7 = works.get("work-7");
        
        generator.group(work1);
        generator.group(work2);
        generator.group(work6);
        generator.group(work7);
        
        List<ActivitiesGroup> groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(2, groups.size());
        //Check there are two activities in each group
        assertEquals(2, groups.get(0).getActivities().size());
        assertEquals(2, groups.get(1).getActivities().size());
        //Check there are five external ids in each group
        assertEquals(5, groups.get(0).getExternalIdentifiers().size());
        assertEquals(5, groups.get(1).getExternalIdentifiers().size());
        //Check each work
        checkWorkIsOnGroups(work1, groups);
        checkWorkIsOnGroups(work2, groups);
        checkWorkIsOnGroups(work6, groups);
        checkWorkIsOnGroups(work7, groups);
        
        //Check work1 and work2 are in the same group
        checkWorksBelongsToTheSameGroup(groups, work1, work2);
        //Check work6 and work7 are in the same group
        checkWorksBelongsToTheSameGroup(groups, work6, work7);
        //Check works are not mixed
        checkWorksDontBelongsToTheSameGroup(groups, work1, work6);
        checkWorksDontBelongsToTheSameGroup(groups, work1, work7);
        checkWorksDontBelongsToTheSameGroup(groups, work2, work6);
        checkWorksDontBelongsToTheSameGroup(groups, work2, work7);
    }
    
    /**
     * Test that two groups without ext ids dont get grouped
     * */
    @Test
    public void groupWorks_DontGroupWorksWithoutExtIds_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, Work> works = generateWorks();
        
        //Group the first group
        Work work8 = works.get("work-8");
        Work work9 = works.get("work-9");
        
        generator.group(work8);
        generator.group(work9);
        
        List<ActivitiesGroup> groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(2, groups.size());
        //Check there are two activities in each group
        assertEquals(1, groups.get(0).getActivities().size());
        assertEquals(1, groups.get(1).getActivities().size());
        //Check there are five external ids in each group
        assertEquals(0, groups.get(0).getExternalIdentifiers().size());
        assertEquals(0, groups.get(1).getExternalIdentifiers().size());
        
        checkWorkIsOnGroups(work8, groups);
        checkWorkIsOnGroups(work9, groups);
        
        checkWorksDontBelongsToTheSameGroup(groups, work8, work9);
    }
    
    /**
     * work-1 and work-3 will be in different groups
     * then work-2 will go to the same group as work-1
     * then work-4 contains ARG(Y) and ARG(B) so, the two groups should be merged
     * */
    @Test
    public void groupWorks_MergeTwoGroups_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, Work> works = generateWorks();
        
        //Group the first group
        Work work1 = works.get("work-1");
        Work work2 = works.get("work-2");
        Work work3 = works.get("work-3");
        Work work4 = works.get("work-4");
        
        generator.group(work1);
        generator.group(work2);
        generator.group(work3);

        /**
         * At this point there are two groups
         * G1 with work1 and work2
         * G2 with work3
         * */
        List<ActivitiesGroup> groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(2, groups.size());
        checkWorksBelongsToTheSameGroup(groups, work1, work2);
        checkWorksDontBelongsToTheSameGroup(groups, work1, work3);
        checkWorksDontBelongsToTheSameGroup(groups, work2, work3);
        
        //group work4, which should merge the two groups
        generator.group(work4);
        groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(1, groups.size());
        assertEquals(4, groups.get(0).getActivities().size());
        assertEquals(9, groups.get(0).getExternalIdentifiers().size());
        
        checkWorkIsOnGroups(work1, groups);
        checkWorkIsOnGroups(work2, groups);
        checkWorkIsOnGroups(work3, groups);
        checkWorkIsOnGroups(work4, groups);
        checkWorksBelongsToTheSameGroup(groups, work1, work2, work3, work4);
    }
    
    /**
     * work-1, work-3, work-5 and work-8 will be in separate groups
     * then work-4 will merge groups of work-1 and work-3
     * 
     * Check that after that, there are 3 groups, one with work-1, work-3 and work-4, one with work-5 and other with work-8
     * */
    @Test
    public void groupWorks_MergeGroupsDontAffectNotMergedGroups_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, Work> works = generateWorks();
        
        //Group the first group
        Work work1 = works.get("work-1");
        Work work3 = works.get("work-3");
        Work work4 = works.get("work-4");
        Work work5 = works.get("work-5");
        Work work8 = works.get("work-8");
        
        //Respect order
        generator.group(work1);
        generator.group(work3);
        generator.group(work5);
        generator.group(work8);
        generator.group(work4);
        
        List<ActivitiesGroup> groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(3, groups.size());
        //Check work1, work3 and work4 belongs to the same group
        checkWorksBelongsToTheSameGroup(groups, work1, work3, work4);
        //Check work1, work5 and work8 are all in different groups
        checkWorksDontBelongsToTheSameGroup(groups, work1, work5, work8);
        
        checkWorkIsOnGroups(work1, groups);
        checkWorkIsOnGroups(work3, groups);
        checkWorkIsOnGroups(work4, groups);
        checkWorkIsOnGroups(work5, groups);
        checkWorkIsOnGroups(work8, groups);
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
     * Check that the given works belongs to the same group in a list of given groups
     * */
    private void checkWorksBelongsToTheSameGroup(List<ActivitiesGroup> groups, Work ... works) {
        Work firstWork = works[0];
        
        assertNotNull(firstWork);
        
        ActivitiesGroup theGroup = getGroupThatContainsWork(groups, firstWork);
        
        assertNotNull(theGroup);
        
        for(Work work : works) {
            assertTrue(theGroup.belongsToGroup(work));
        }
    }
    
    
    /**
     * Check that the given works belongs to the same group in a list of given groups
     * */
    private void checkWorksDontBelongsToTheSameGroup(List<ActivitiesGroup> groups, Work ... works) {                
        for(int i = 0; i < works.length; i++) {
            Work w1 = works[i];
            ActivitiesGroup theGroup = getGroupThatContainsWork(groups, w1);
            for(int j = i+1; j < works.length; j++){
                assertFalse("work[" + i + "] and work["+ j + "] belongs to the same group", theGroup.belongsToGroup(works[j]));
            }
        }                                
    }        
    
    /**
     * Returns the group that contains the given work
     * */
    private ActivitiesGroup getGroupThatContainsWork(List<ActivitiesGroup> groups, Work work) {
        ActivitiesGroup theGroup = null;
        for(ActivitiesGroup group : groups) {
            if(group.belongsToGroup(work)) {
                theGroup = group;
                break;
            }
        }
        return theGroup;
    }
    
    /**
     * Checks that all the external identifiers in the work are contained in the group external identifiers
     * */
    private void checkWorkExternalIdentifiers(Work work, ActivitiesGroup group) {
        WorkExternalIdentifiers workExtIdsContainer = work.getExternalIdentifiers();
        List<WorkExternalIdentifier> workExtIds = workExtIdsContainer.getExternalIdentifier();
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
     * work-7 -> DOI(1), DOI(2), ARIXV(B)  
     * work-8 -> No external identifiers
     * work-9 -> No external identifiers  
     * */
    private Map<String, Work> generateWorks() {
        Map<String, Work> result = new HashMap<String, Work>();
        for(int i = 1; i < 10; i++) {
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
                WorkExternalIdentifier e2_1 = new WorkExternalIdentifier();
                e2_1.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e2_1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("C"));
                WorkExternalIdentifier e2_2 = new WorkExternalIdentifier();
                e2_2.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e2_2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("D"));
                WorkExternalIdentifier e2_3 = new WorkExternalIdentifier();
                e2_3.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e2_3.setWorkExternalIdentifierId(new WorkExternalIdentifierId("E"));
                wei.getExternalIdentifier().add(e2_1);
                wei.getExternalIdentifier().add(e2_2);
                wei.getExternalIdentifier().add(e2_3);
                break;
            case 3: 
                WorkExternalIdentifier e3_1 = new WorkExternalIdentifier();
                e3_1.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e3_1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("X"));
                WorkExternalIdentifier e3_2 = new WorkExternalIdentifier();
                e3_2.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e3_2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Y"));
                WorkExternalIdentifier e3_3 = new WorkExternalIdentifier();
                e3_3.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e3_3.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Z"));
                wei.getExternalIdentifier().add(e3_1);
                wei.getExternalIdentifier().add(e3_2);
                wei.getExternalIdentifier().add(e3_3);
                break;
            case 4: 
                WorkExternalIdentifier e4_1 = new WorkExternalIdentifier();
                e4_1.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e4_1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Y"));
                WorkExternalIdentifier e4_2 = new WorkExternalIdentifier();
                e4_2.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e4_2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("B"));
                WorkExternalIdentifier e4_3 = new WorkExternalIdentifier();
                e4_3.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e4_3.setWorkExternalIdentifierId(new WorkExternalIdentifierId("1"));
                wei.getExternalIdentifier().add(e4_1);
                wei.getExternalIdentifier().add(e4_2);
                wei.getExternalIdentifier().add(e4_3);
                break;
            case 5:
                WorkExternalIdentifier e5_1 = new WorkExternalIdentifier();
                e5_1.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e5_1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("M"));
                WorkExternalIdentifier e5_2 = new WorkExternalIdentifier();
                e5_2.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e5_2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("N"));
                WorkExternalIdentifier e5_3 = new WorkExternalIdentifier();
                e5_3.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e5_3.setWorkExternalIdentifierId(new WorkExternalIdentifierId("O"));
                wei.getExternalIdentifier().add(e5_1);
                wei.getExternalIdentifier().add(e5_2);
                wei.getExternalIdentifier().add(e5_3);
                break;
            case 6: 
                WorkExternalIdentifier e6_1 = new WorkExternalIdentifier();
                e6_1.setWorkExternalIdentifierType(WorkExternalIdentifierType.ARXIV);
                e6_1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("A"));
                WorkExternalIdentifier e6_2 = new WorkExternalIdentifier();
                e6_2.setWorkExternalIdentifierType(WorkExternalIdentifierType.ARXIV);
                e6_2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("B"));
                WorkExternalIdentifier e6_3 = new WorkExternalIdentifier();
                e6_3.setWorkExternalIdentifierType(WorkExternalIdentifierType.ARXIV);
                e6_3.setWorkExternalIdentifierId(new WorkExternalIdentifierId("C"));
                wei.getExternalIdentifier().add(e6_1);
                wei.getExternalIdentifier().add(e6_2);
                wei.getExternalIdentifier().add(e6_3);
                break;
            case 7:
                WorkExternalIdentifier e7_1 = new WorkExternalIdentifier();
                e7_1.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
                e7_1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("1"));
                WorkExternalIdentifier e7_2 = new WorkExternalIdentifier();
                e7_2.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
                e7_2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("2"));
                WorkExternalIdentifier e7_3 = new WorkExternalIdentifier();
                e7_3.setWorkExternalIdentifierType(WorkExternalIdentifierType.ARXIV);
                e7_3.setWorkExternalIdentifierId(new WorkExternalIdentifierId("B"));
                wei.getExternalIdentifier().add(e7_1);
                wei.getExternalIdentifier().add(e7_2);
                wei.getExternalIdentifier().add(e7_3);
                break;
            }
            work.setWorkExternalIdentifiers(wei);
            result.put(title, work);
        }
        return result;
    }        
}