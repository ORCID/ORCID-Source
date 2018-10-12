package org.orcid.core.utils.v3.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.orcid.jaxb.model.v3.rc2.common.Title;
import org.orcid.jaxb.model.v3.rc2.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc2.record.WorkTitle;
import org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary;

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
        assertNotNull(g1.getGroupKeys());
        assertEquals(3, g1.getGroupKeys().size());
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
        assertEquals(3, groups.get(0).getGroupKeys().size());
        assertEquals(3, groups.get(1).getGroupKeys().size());                
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
        assertEquals(3, groups.get(0).getGroupKeys().size());
        assertEquals(3, groups.get(1).getGroupKeys().size());                
        assertEquals(3, groups.get(2).getGroupKeys().size());
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
            if(groups.get(i).getGroupKeys().size() == 0) {                                
                work8found = true;
            } else {
                assertEquals(3, groups.get(i).getGroupKeys().size());
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
        assertNotNull(g1.getGroupKeys());
        assertEquals(5, g1.getGroupKeys().size());
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
        assertEquals(5, groups.get(0).getGroupKeys().size());
        assertEquals(5, groups.get(1).getGroupKeys().size());
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
        assertEquals(0, groups.get(0).getGroupKeys().size());
        assertEquals(0, groups.get(1).getGroupKeys().size());
        
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
        assertEquals(9, groups.get(0).getGroupKeys().size());
        
        checkActivityIsOnGroups(work1, groups);
        checkActivityIsOnGroups(work2, groups);
        checkActivityIsOnGroups(work3, groups);
        checkActivityIsOnGroups(work4, groups);
        checkActivitiesBelongsToTheSameGroup(groups, work1, work2, work3, work4);
    }
    
    @Test
    public void testNormalizedGrouping(){
        ActivitiesGroupGenerator generator = new ActivitiesGroupGenerator();
        List<WorkSummary> sums = new ArrayList<WorkSummary>();
        for (int i=0;i<2;i++){
            String title = "work-" + i;
            WorkSummary work = new WorkSummary();
            //Set title
            WorkTitle workTitle = new WorkTitle();
            workTitle.setTitle(new Title(title));
            work.setTitle(workTitle);            
            ExternalIDs wei = new ExternalIDs();
            ExternalID e1 = new ExternalID();
            e1.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.DOI.value());
            e1.setValue("a");
            e1.setNormalized(new TransientNonEmptyString("a"));
            wei.getExternalIdentifier().add(e1);
            work.setExternalIdentifiers(wei);
            sums.add(work);
        }
        sums.get(0).getExternalIdentifiers().getExternalIdentifier().get(0).setValue("A");
        
        generator.group(sums.get(0));
        generator.group(sums.get(1));
        assertEquals(1,generator.getGroups().size());
        checkActivitiesBelongsToTheSameGroup(generator.getGroups(),sums.get(0),sums.get(1));
        
        for (int i=2;i<4;i++){
            String title = "work-" + i;
            WorkSummary work = new WorkSummary();
            //Set title
            WorkTitle workTitle = new WorkTitle();
            workTitle.setTitle(new Title(title));
            work.setTitle(workTitle);            
            ExternalIDs wei = new ExternalIDs();
            ExternalID e1 = new ExternalID();
            e1.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value());
            e1.setValue("https://dx.doi.org/10/UPPER");
            e1.setNormalized(new TransientNonEmptyString("10/upper"));
            wei.getExternalIdentifier().add(e1);
            work.setExternalIdentifiers(wei);
            sums.add(work);
        }
        sums.get(0).getExternalIdentifiers().getExternalIdentifier().get(0).setValue("http://doi.org/10/upper");

        generator.group(sums.get(2));
        generator.group(sums.get(3));
        assertEquals(2,generator.getGroups().size());
        checkActivitiesBelongsToTheSameGroup(generator.getGroups(),sums.get(0),sums.get(1));
        checkActivitiesBelongsToTheSameGroup(generator.getGroups(),sums.get(2),sums.get(3));
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
            ExternalIDs wei = new ExternalIDs();
            switch (i) {
            case 1:
                ExternalID e1 = new ExternalID();
                e1.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value());
                e1.setValue("A");
                ExternalID e2 = new ExternalID();
                e2.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value());
                e2.setValue("B");
                ExternalID e3 = new ExternalID();
                e3.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value());
                e3.setValue("C");
                wei.getExternalIdentifier().add(e1);
                wei.getExternalIdentifier().add(e2);
                wei.getExternalIdentifier().add(e3);
                break;
            case 2:
                ExternalID e4 = new ExternalID();
                e4.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value());
                e4.setValue("C");
                ExternalID e5 = new ExternalID();
                e5.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value());
                e5.setValue("D");
                ExternalID e6 = new ExternalID();
                e6.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value());
                e6.setValue("E");
                wei.getExternalIdentifier().add(e4);
                wei.getExternalIdentifier().add(e5);
                wei.getExternalIdentifier().add(e6);
                break;
            case 3: 
                ExternalID e7 = new ExternalID();
                e7.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value());
                e7.setValue("X");
                ExternalID e8 = new ExternalID();
                e8.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value());
                e8.setValue("Y");
                ExternalID e9 = new ExternalID();
                e9.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value());
                e9.setValue("Z");
                wei.getExternalIdentifier().add(e7);
                wei.getExternalIdentifier().add(e8);
                wei.getExternalIdentifier().add(e9);
                break;
            case 4: 
                ExternalID e10 = new ExternalID();
                e10.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value());
                e10.setValue("Y");
                ExternalID e11 = new ExternalID();
                e11.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value());
                e11.setValue("B");
                ExternalID e12 = new ExternalID();
                e12.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value());
                e12.setValue("1");
                wei.getExternalIdentifier().add(e10);
                wei.getExternalIdentifier().add(e11);
                wei.getExternalIdentifier().add(e12);
                break;
            case 5:
                ExternalID e13 = new ExternalID();
                e13.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value());
                e13.setValue("M");
                ExternalID e14 = new ExternalID();
                e14.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value());
                e14.setValue("N");
                ExternalID e15 = new ExternalID();
                e15.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value());
                e15.setValue("O");
                wei.getExternalIdentifier().add(e13);
                wei.getExternalIdentifier().add(e14);
                wei.getExternalIdentifier().add(e15);
                break;
            case 6: 
                ExternalID e16 = new ExternalID();
                e16.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.ARXIV.value());
                e16.setValue("A");
                ExternalID e17 = new ExternalID();
                e17.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.ARXIV.value());
                e17.setValue("B");
                ExternalID e18 = new ExternalID();
                e18.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.ARXIV.value());
                e18.setValue("C");
                wei.getExternalIdentifier().add(e16);
                wei.getExternalIdentifier().add(e17);
                wei.getExternalIdentifier().add(e18);
                break;
            case 7:
                ExternalID e19 = new ExternalID();
                e19.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.DOI.value());
                e19.setValue("1");
                ExternalID e20 = new ExternalID();
                e20.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.DOI.value());
                e20.setValue("2");
                ExternalID e21 = new ExternalID();
                e21.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.ARXIV.value());
                e21.setValue("B");
                wei.getExternalIdentifier().add(e19);
                wei.getExternalIdentifier().add(e20);
                wei.getExternalIdentifier().add(e21);
                break;
            case 10:
                ExternalID e22 = new ExternalID();
                e22.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISSN.value());
                e22.setValue("1");
                ExternalID e23 = new ExternalID();
                e23.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISSN.value());
                e23.setValue("2");
                ExternalID e24 = new ExternalID();
                e24.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISSN.value());
                e24.setValue("3");
                wei.getExternalIdentifier().add(e22);
                wei.getExternalIdentifier().add(e23);
                wei.getExternalIdentifier().add(e24);
                break;
            case 11:
                ExternalID e25 = new ExternalID();
                e25.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISSN.value());
                e25.setValue("3");
                ExternalID e26 = new ExternalID();
                e26.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISSN.value());
                e26.setValue("3");
                ExternalID e27 = new ExternalID();
                e27.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISSN.value());
                e27.setValue("3");
                wei.getExternalIdentifier().add(e25);
                wei.getExternalIdentifier().add(e26);
                wei.getExternalIdentifier().add(e27);
                break;
            case 12: 
                ExternalID e28 = new ExternalID();
                e28.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.DOI.value());
                e28.setValue("1");
                ExternalID e29 = new ExternalID();
                e29.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISSN.value());
                e29.setValue("1");
                wei.getExternalIdentifier().add(e28);
                wei.getExternalIdentifier().add(e29);
                break;
            case 13:
                ExternalID e30 = new ExternalID();
                e30.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.DOI.value());
                e30.setValue("1");
                ExternalID e31 = new ExternalID();
                e31.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISSN.value());
                e31.setValue("4");
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