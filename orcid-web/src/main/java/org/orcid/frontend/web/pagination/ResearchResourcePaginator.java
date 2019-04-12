package org.orcid.frontend.web.pagination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.ResearchResourceManager;
import org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResourceGroup;
import org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResources;
import org.orcid.jaxb.model.v3.rc2.common.FuzzyDate;
import org.orcid.pojo.ResearchResourceGroupPojo;

public class ResearchResourcePaginator {

    static final int PAGE_SIZE = 50;
    public static final Comparator<ResearchResourceGroup> DATE_COMPARATOR = new DateComparator();
    public static final Comparator<ResearchResourceGroup> END_DATE_COMPARITOR = new EndDateComparator();
    public static final Comparator<ResearchResourceGroup> TITLE_COMPARITOR = new TitleComparator();
    public static final String DATE_SORT_KEY = "date";
    public static final String TITLE_SORT_KEY = "title";
    
    @Resource(name = "researchResourceManagerV3")
    ResearchResourceManager researchResourceManager;
    
    public Page<ResearchResourceGroupPojo> getPage(String orcid, int offset, boolean justPublic, String sort, boolean sortAsc) {
        List<ResearchResourceSummary> r = researchResourceManager.getResearchResourceSummaryList(orcid);
        ResearchResources rr = researchResourceManager.groupResearchResources(r, justPublic);
        List<ResearchResourceGroup> sortedGroups = sort(rr.getResearchResourceGroup(), sort, sortAsc);
        
        Page<ResearchResourceGroupPojo> page = new Page<ResearchResourceGroupPojo>();
        page.setGroups(new ArrayList<ResearchResourceGroupPojo>());
        
        for (int i = offset; i < Math.min(offset + PAGE_SIZE, sortedGroups.size()); i++) {
            org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResourceGroup group = sortedGroups.get(i);
            page.getGroups().add(ResearchResourceGroupPojo.valueOf(group, i, orcid));
        }
        page.setTotalGroups(sortedGroups.size());
        
        page.setNextOffset(offset+PAGE_SIZE);
        return page;
    }
    
    public int getPublicCount(String orcid) {
        List<ResearchResourceSummary> r = researchResourceManager.getResearchResourceSummaryList(orcid);
        ResearchResources publicResources = researchResourceManager.groupResearchResources(r, true);
        return publicResources.getResearchResourceGroup().size();
    }
    
    private List<ResearchResourceGroup> sort(List<ResearchResourceGroup> list, String sort, boolean sortAsc) {
        if (TITLE_SORT_KEY.equals(sort)) {
            Collections.sort(list,TITLE_COMPARITOR);
        } else if (DATE_SORT_KEY.equals(sort)) {
            Collections.sort(list, DATE_COMPARATOR);
        } 
        if (!sortAsc)
            Collections.reverse(list);
        return list;
    }
    
    public static class DateComparator implements Comparator<ResearchResourceGroup> {
        @Override
        public int compare(ResearchResourceGroup g1, ResearchResourceGroup g2) {
            FuzzyDate startDate1 = g1.getResearchResourceSummary().get(0).getProposal().getStartDate();
            FuzzyDate startDate2 = g2.getResearchResourceSummary().get(0).getProposal().getStartDate();
            if (startDate1 == null && startDate2 == null) {
                return TITLE_COMPARITOR.compare(g1, g2);
            }
            if (startDate1 == null) {
                return -1;
            }
            if (startDate2 == null) {
                return 1;
            }
            if (startDate1.compareTo(startDate2) == 0){
                return END_DATE_COMPARITOR.compare(g1, g2);
            }
            return g1.getResearchResourceSummary().get(0).getProposal().getStartDate().compareTo(g2.getResearchResourceSummary().get(0).getProposal().getStartDate());
        }
    }
    
    public static class EndDateComparator implements Comparator<ResearchResourceGroup>{
        @Override
        public int compare(ResearchResourceGroup g1, ResearchResourceGroup g2) {
            FuzzyDate endDate1 = g1.getResearchResourceSummary().get(0).getProposal().getEndDate();
            FuzzyDate endDate2 = g2.getResearchResourceSummary().get(0).getProposal().getEndDate();
            if (endDate1 == null && endDate2 == null)
                return TITLE_COMPARITOR.compare(g1, g2);
            //Null = to present and should sort first
            if (endDate1 == null)
                return 1;
            if (endDate2 == null)
                return -1;
            if (endDate1.compareTo(endDate2) == 0){
                return TITLE_COMPARITOR.compare(g1, g2);
            }
            return g1.getResearchResourceSummary().get(0).getProposal().getEndDate().compareTo(g2.getResearchResourceSummary().get(0).getProposal().getEndDate());
        }        
    }

    public static class TitleComparator implements Comparator<ResearchResourceGroup>{
        @Override
        public int compare(ResearchResourceGroup g1, ResearchResourceGroup g2) {
            String title1 = (g1.getResearchResourceSummary().get(0).getProposal().getTitle() == null || g1.getResearchResourceSummary().get(0).getProposal().getTitle().getTitle() == null || g1.getResearchResourceSummary().get(0).getProposal().getTitle().getTitle().getContent() == null)?null:g1.getResearchResourceSummary().get(0).getProposal().getTitle().getTitle().getContent().toLowerCase();
            String title2 = (g2.getResearchResourceSummary().get(0).getProposal().getTitle() == null || g2.getResearchResourceSummary().get(0).getProposal().getTitle().getTitle() == null || g2.getResearchResourceSummary().get(0).getProposal().getTitle().getTitle().getContent() == null)?null:g2.getResearchResourceSummary().get(0).getProposal().getTitle().getTitle().getContent().toLowerCase();
            if (title1 == null && title2 == null)
                return 0;
            if (title1 == null)
                return -1;
            if (title2 == null)
                return 1;
            return g1.getResearchResourceSummary().get(0).getProposal().getTitle().getTitle().getContent().toLowerCase().compareTo(g2.getResearchResourceSummary().get(0).getProposal().getTitle().getTitle().getContent().toLowerCase());
        }        
    }

}
