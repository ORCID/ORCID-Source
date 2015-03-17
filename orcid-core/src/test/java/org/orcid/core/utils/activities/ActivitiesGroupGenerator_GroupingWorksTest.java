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
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.orcid.jaxb.model.common.Title;
import org.orcid.jaxb.model.record.WorkExternalIdentifier;
import org.orcid.jaxb.model.record.WorkExternalIdentifierId;
import org.orcid.jaxb.model.record.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record.WorkExternalIdentifiers;
import org.orcid.jaxb.model.record.WorkTitle;
import org.orcid.jaxb.model.record.summary.WorkSummary;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class ActivitiesGroupGenerator_GroupingWorksTest extends ActivitiesGroupGeneratorBaseTest {
    @Test 
    public void groupWorks_4GroupsOf1Work_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, WorkSummary> works = generateWorks();
        
        //Group the first group
        //work-1 -> ARG(A), ARG(B), ARG(C)
        WorkSummary work1 = works.get("work-1");
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
        checkExternalIdentifiers(work1, g1);
        
        //Add another work to the groups
        //work-5 -> ARG(M), ARG(N), ARG(O)
        WorkSummary work5 = works.get("work-5");
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
        checkActivityIsOnGroups(work5, groups);
        
        //Add another work to the groups
        //work-6 -> ARXIV(A), ARXIV(B), ARXIV(C)
        WorkSummary work6 = works.get("work-6");
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
        checkActivityIsOnGroups(work6, groups);
        
        //Add another work to the groups
        //work-8 -> No external identifiers  
        WorkSummary work8 = works.get("work-8");
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
        checkActivityIsOnGroups(work8, groups);        
    }
        
    /**
     * Test grouping work-1 and work-2 
     * */
    @Test
    public void groupWorks_1GroupsOf2Works_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, WorkSummary> works = generateWorks();
        
        WorkSummary work1 = works.get("work-1");        
        WorkSummary work2 = works.get("work-2");
        
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
        checkExternalIdentifiers(work1, g1);
        checkExternalIdentifiers(work2, g1);
    }
    
    /**
     * Test grouping (work-1 and work-2) and (work-6 and work-7) 
     * */
    @Test
    public void groupWorks_2GroupsOf2Works_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, WorkSummary> works = generateWorks();
        
        WorkSummary work1 = works.get("work-1");        
        WorkSummary work2 = works.get("work-2");
        WorkSummary work6 = works.get("work-6");
        WorkSummary work7 = works.get("work-7");
        
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
        checkActivityIsOnGroups(work1, groups);
        checkActivityIsOnGroups(work2, groups);
        checkActivityIsOnGroups(work6, groups);
        checkActivityIsOnGroups(work7, groups);
        
        //Check work1 and work2 are in the same group
        checkActivitiesBelongsToTheSameGroup(groups, work1, work2);
        //Check work6 and work7 are in the same group
        checkActivitiesBelongsToTheSameGroup(groups, work6, work7);
        //Check works are not mixed
        checkActivitiesDontBelongsToTheSameGroup(groups, work1, work6);
        checkActivitiesDontBelongsToTheSameGroup(groups, work1, work7);
        checkActivitiesDontBelongsToTheSameGroup(groups, work2, work6);
        checkActivitiesDontBelongsToTheSameGroup(groups, work2, work7);
    }
    
    /**
     * Test that two groups without ext ids dont get grouped
     * */
    @Test
    public void groupWorks_DontGroupWorksWithoutExtIds_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, WorkSummary> works = generateWorks();
        
        //Group the first group
        WorkSummary work8 = works.get("work-8");
        WorkSummary work9 = works.get("work-9");
        
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
        
        checkActivityIsOnGroups(work8, groups);
        checkActivityIsOnGroups(work9, groups);
        
        checkActivitiesDontBelongsToTheSameGroup(groups, work8, work9);
    }
    
    /**
     * work-1 and work-3 will be in different groups
     * then work-2 will go to the same group as work-1
     * then work-4 contains ARG(Y) and ARG(B) so, the two groups should be merged
     * */
    @Test
    public void groupWorks_MergeTwoGroups_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, WorkSummary> works = generateWorks();
        
        //Group the first group
        WorkSummary work1 = works.get("work-1");
        WorkSummary work2 = works.get("work-2");
        WorkSummary work3 = works.get("work-3");
        WorkSummary work4 = works.get("work-4");
        
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
        checkActivitiesBelongsToTheSameGroup(groups, work1, work2);
        checkActivitiesDontBelongsToTheSameGroup(groups, work1, work3);
        checkActivitiesDontBelongsToTheSameGroup(groups, work2, work3);
        
        //group work4, which should merge the two groups
        generator.group(work4);
        groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(1, groups.size());
        assertEquals(4, groups.get(0).getActivities().size());
        assertEquals(9, groups.get(0).getExternalIdentifiers().size());
        
        checkActivityIsOnGroups(work1, groups);
        checkActivityIsOnGroups(work2, groups);
        checkActivityIsOnGroups(work3, groups);
        checkActivityIsOnGroups(work4, groups);
        checkActivitiesBelongsToTheSameGroup(groups, work1, work2, work3, work4);
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
        Map<String, WorkSummary> works = generateWorks();
        
        //Group the first group
        WorkSummary work1 = works.get("work-1");
        WorkSummary work3 = works.get("work-3");
        WorkSummary work4 = works.get("work-4");
        WorkSummary work5 = works.get("work-5");
        WorkSummary work8 = works.get("work-8");
        
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
        checkActivitiesBelongsToTheSameGroup(groups, work1, work3, work4);
        //Check work1, work5 and work8 are all in different groups
        checkActivitiesDontBelongsToTheSameGroup(groups, work1, work5, work8);
        
        checkActivityIsOnGroups(work1, groups);
        checkActivityIsOnGroups(work3, groups);
        checkActivityIsOnGroups(work4, groups);
        checkActivityIsOnGroups(work5, groups);
        checkActivityIsOnGroups(work8, groups);
    }       
        
    /**
     * Test that two groups are not gruped by ISNI 
     * */
    @Test
    public void groupWorks_DontGroupByISSN_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, WorkSummary> works = generateWorks();
        
        //Group the first group
        WorkSummary work10 = works.get("work-10");
        WorkSummary work11 = works.get("work-11");
        
        generator.group(work10);
        generator.group(work11);
        
        List<ActivitiesGroup> groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(2, groups.size());
        
        checkActivityIsOnGroups(work10, groups);
        checkActivityIsOnGroups(work11, groups);
        
        checkActivitiesDontBelongsToTheSameGroup(groups, work10, work11);
        
        //Check the groups dont have any ext id, since all of them should be ignored
        assertNotNull(groups.get(0).getExternalIdentifiers());
        assertTrue(groups.get(0).getExternalIdentifiers().isEmpty());
        assertNotNull(groups.get(1).getExternalIdentifiers());
        assertTrue(groups.get(1).getExternalIdentifiers().isEmpty());
    }
    
    /**
     * Test that two groups are not gruped by ISNI 
     * */
    @Test
    public void groupWorks_DontGroupByISSN_2_Test() {
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        Map<String, WorkSummary> works = generateWorks();
        
        //Group the first group
        WorkSummary work10 = works.get("work-10");
        WorkSummary work11 = works.get("work-11");
        WorkSummary work12 = works.get("work-12");
        WorkSummary work13 = works.get("work-13");
        
        generator.group(work10);
        generator.group(work11);
        generator.group(work12);
        generator.group(work13);
        
        
        List<ActivitiesGroup> groups = generator.getGroups();
        assertNotNull(groups);
        assertEquals(3, groups.size());
        
        checkActivityIsOnGroups(work10, groups);
        checkActivityIsOnGroups(work11, groups);
        checkActivityIsOnGroups(work12, groups);
        checkActivityIsOnGroups(work13, groups);                
        
        checkActivitiesBelongsToTheSameGroup(groups, work12, work13);
        
        checkActivitiesDontBelongsToTheSameGroup(groups, work10, work11);
        checkActivitiesDontBelongsToTheSameGroup(groups, work10, work12);
        checkActivitiesDontBelongsToTheSameGroup(groups, work11, work13);
        
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
     * work-10 -> ISSN(1), ISSN(2), ISSN(3)
     * work-11 -> ISSN(3), ISSN(4), ISSN(5)
     * work-12 -> DOI(1), ISSN(1)
     * work-13 -> DOI(1), ISSN(4)
     * */
    private Map<String, WorkSummary> generateWorks() {
        Map<String, WorkSummary> result = new HashMap<String, WorkSummary>();
        for(int i = 1; i < 14; i++) {
            String title = "work-" + i;
            WorkSummary work = new WorkSummary();
            //Set title
            WorkTitle workTitle = new WorkTitle();
            workTitle.setTitle(new Title(title));
            work.setTitle(workTitle);            
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
                WorkExternalIdentifier e4 = new WorkExternalIdentifier();
                e4.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e4.setWorkExternalIdentifierId(new WorkExternalIdentifierId("C"));
                WorkExternalIdentifier e5 = new WorkExternalIdentifier();
                e5.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e5.setWorkExternalIdentifierId(new WorkExternalIdentifierId("D"));
                WorkExternalIdentifier e6 = new WorkExternalIdentifier();
                e6.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e6.setWorkExternalIdentifierId(new WorkExternalIdentifierId("E"));
                wei.getExternalIdentifier().add(e4);
                wei.getExternalIdentifier().add(e5);
                wei.getExternalIdentifier().add(e6);
                break;
            case 3: 
                WorkExternalIdentifier e7 = new WorkExternalIdentifier();
                e7.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e7.setWorkExternalIdentifierId(new WorkExternalIdentifierId("X"));
                WorkExternalIdentifier e8 = new WorkExternalIdentifier();
                e8.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e8.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Y"));
                WorkExternalIdentifier e9 = new WorkExternalIdentifier();
                e9.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e9.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Z"));
                wei.getExternalIdentifier().add(e7);
                wei.getExternalIdentifier().add(e8);
                wei.getExternalIdentifier().add(e9);
                break;
            case 4: 
                WorkExternalIdentifier e10 = new WorkExternalIdentifier();
                e10.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e10.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Y"));
                WorkExternalIdentifier e11 = new WorkExternalIdentifier();
                e11.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e11.setWorkExternalIdentifierId(new WorkExternalIdentifierId("B"));
                WorkExternalIdentifier e12 = new WorkExternalIdentifier();
                e12.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e12.setWorkExternalIdentifierId(new WorkExternalIdentifierId("1"));
                wei.getExternalIdentifier().add(e10);
                wei.getExternalIdentifier().add(e11);
                wei.getExternalIdentifier().add(e12);
                break;
            case 5:
                WorkExternalIdentifier e13 = new WorkExternalIdentifier();
                e13.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e13.setWorkExternalIdentifierId(new WorkExternalIdentifierId("M"));
                WorkExternalIdentifier e14 = new WorkExternalIdentifier();
                e14.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e14.setWorkExternalIdentifierId(new WorkExternalIdentifierId("N"));
                WorkExternalIdentifier e15 = new WorkExternalIdentifier();
                e15.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
                e15.setWorkExternalIdentifierId(new WorkExternalIdentifierId("O"));
                wei.getExternalIdentifier().add(e13);
                wei.getExternalIdentifier().add(e14);
                wei.getExternalIdentifier().add(e15);
                break;
            case 6: 
                WorkExternalIdentifier e16 = new WorkExternalIdentifier();
                e16.setWorkExternalIdentifierType(WorkExternalIdentifierType.ARXIV);
                e16.setWorkExternalIdentifierId(new WorkExternalIdentifierId("A"));
                WorkExternalIdentifier e17 = new WorkExternalIdentifier();
                e17.setWorkExternalIdentifierType(WorkExternalIdentifierType.ARXIV);
                e17.setWorkExternalIdentifierId(new WorkExternalIdentifierId("B"));
                WorkExternalIdentifier e18 = new WorkExternalIdentifier();
                e18.setWorkExternalIdentifierType(WorkExternalIdentifierType.ARXIV);
                e18.setWorkExternalIdentifierId(new WorkExternalIdentifierId("C"));
                wei.getExternalIdentifier().add(e16);
                wei.getExternalIdentifier().add(e17);
                wei.getExternalIdentifier().add(e18);
                break;
            case 7:
                WorkExternalIdentifier e19 = new WorkExternalIdentifier();
                e19.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
                e19.setWorkExternalIdentifierId(new WorkExternalIdentifierId("1"));
                WorkExternalIdentifier e20 = new WorkExternalIdentifier();
                e20.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
                e20.setWorkExternalIdentifierId(new WorkExternalIdentifierId("2"));
                WorkExternalIdentifier e21 = new WorkExternalIdentifier();
                e21.setWorkExternalIdentifierType(WorkExternalIdentifierType.ARXIV);
                e21.setWorkExternalIdentifierId(new WorkExternalIdentifierId("B"));
                wei.getExternalIdentifier().add(e19);
                wei.getExternalIdentifier().add(e20);
                wei.getExternalIdentifier().add(e21);
                break;
            case 10:
                WorkExternalIdentifier e22 = new WorkExternalIdentifier();
                e22.setWorkExternalIdentifierType(WorkExternalIdentifierType.ISSN);
                e22.setWorkExternalIdentifierId(new WorkExternalIdentifierId("1"));
                WorkExternalIdentifier e23 = new WorkExternalIdentifier();
                e23.setWorkExternalIdentifierType(WorkExternalIdentifierType.ISSN);
                e23.setWorkExternalIdentifierId(new WorkExternalIdentifierId("2"));
                WorkExternalIdentifier e24 = new WorkExternalIdentifier();
                e24.setWorkExternalIdentifierType(WorkExternalIdentifierType.ISSN);
                e24.setWorkExternalIdentifierId(new WorkExternalIdentifierId("3"));
                wei.getExternalIdentifier().add(e22);
                wei.getExternalIdentifier().add(e23);
                wei.getExternalIdentifier().add(e24);
                break;
            case 11:
                WorkExternalIdentifier e25 = new WorkExternalIdentifier();
                e25.setWorkExternalIdentifierType(WorkExternalIdentifierType.ISSN);
                e25.setWorkExternalIdentifierId(new WorkExternalIdentifierId("3"));
                WorkExternalIdentifier e26 = new WorkExternalIdentifier();
                e26.setWorkExternalIdentifierType(WorkExternalIdentifierType.ISSN);
                e26.setWorkExternalIdentifierId(new WorkExternalIdentifierId("3"));
                WorkExternalIdentifier e27 = new WorkExternalIdentifier();
                e27.setWorkExternalIdentifierType(WorkExternalIdentifierType.ISSN);
                e27.setWorkExternalIdentifierId(new WorkExternalIdentifierId("3"));
                wei.getExternalIdentifier().add(e25);
                wei.getExternalIdentifier().add(e26);
                wei.getExternalIdentifier().add(e27);
                break;
            case 12: 
                WorkExternalIdentifier e28 = new WorkExternalIdentifier();
                e28.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
                e28.setWorkExternalIdentifierId(new WorkExternalIdentifierId("1"));
                WorkExternalIdentifier e29 = new WorkExternalIdentifier();
                e29.setWorkExternalIdentifierType(WorkExternalIdentifierType.ISSN);
                e29.setWorkExternalIdentifierId(new WorkExternalIdentifierId("1"));
                wei.getExternalIdentifier().add(e28);
                wei.getExternalIdentifier().add(e29);
                break;
            case 13:
                WorkExternalIdentifier e30 = new WorkExternalIdentifier();
                e30.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
                e30.setWorkExternalIdentifierId(new WorkExternalIdentifierId("1"));
                WorkExternalIdentifier e31 = new WorkExternalIdentifier();
                e31.setWorkExternalIdentifierType(WorkExternalIdentifierType.ISSN);
                e31.setWorkExternalIdentifierId(new WorkExternalIdentifierId("4"));
                wei.getExternalIdentifier().add(e30);
                wei.getExternalIdentifier().add(e31);
                break;
            }
            work.setExternalIdentifiers(wei);
            result.put(title, work);
        }
        return result;
    }        
}