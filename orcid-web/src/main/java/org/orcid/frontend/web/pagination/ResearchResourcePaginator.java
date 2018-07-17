package org.orcid.frontend.web.pagination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.ResearchResourceManager;
import org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResources;
import org.orcid.pojo.ResearchResourceGroupPojo;

public class ResearchResourcePaginator {

    static final int PAGE_SIZE = 50;
    public static final Comparator<ResearchResourceGroupPojo> START_DATE_COMPARITOR = new StartDateComparator();
    public static final Comparator<ResearchResourceGroupPojo> END_DATE_COMPARITOR = new EndDateComparator();
    public static final Comparator<ResearchResourceGroupPojo> TITLE_COMPARITOR = new TitleComparator();
    public static final String TITLE_SORT_KEY = "title";
    public static final String START_DATE_SORT_KEY = "startDate";
    public static final String END_DATE_SORT_KEY = "endDate";
    
    @Resource(name = "researchResourceManagerV3")
    ResearchResourceManager researchResourceManager;
    
    public Page<ResearchResourceGroupPojo> getPage(String orcid, int offset, boolean justPublic, String sort, boolean sortAsc) {
        List<ResearchResourceSummary> r = researchResourceManager.getResearchResourceSummaryList(orcid);
        ResearchResources rr = researchResourceManager.groupResearchResources(r, justPublic);
        Page<ResearchResourceGroupPojo> page = new Page<ResearchResourceGroupPojo>();
        page.setGroups(new ArrayList<ResearchResourceGroupPojo>());
        
        for (int i = offset; i < Math.min(offset + PAGE_SIZE, rr.getResearchResourceGroup().size()); i++) {
            org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceGroup group = rr.getResearchResourceGroup().get(i);
            page.getGroups().add(new ResearchResourceGroupPojo(group, i, orcid));
        }
        page.setTotalGroups(rr.getResearchResourceGroup().size());
        
        if (TITLE_SORT_KEY.equals(sort)) {
            Collections.sort(page.getGroups(),TITLE_COMPARITOR);
        } else if (END_DATE_SORT_KEY.equals(sort)) {
            Collections.sort(page.getGroups(),END_DATE_COMPARITOR);
        } else if (START_DATE_SORT_KEY.equals(sort)) {
            Collections.sort(page.getGroups(),START_DATE_COMPARITOR);
        }
        if (!sortAsc)
            Collections.reverse(page.getGroups());
        
        page.setNextOffset(offset+PAGE_SIZE);
        return page;
    }
    
    public int getPublicCount(String orcid) {
        List<ResearchResourceSummary> r = researchResourceManager.getResearchResourceSummaryList(orcid);
        ResearchResources publicResources = researchResourceManager.groupResearchResources(r, true);
        return publicResources.getResearchResourceGroup().size();
    }
    
    public static class StartDateComparator implements Comparator<ResearchResourceGroupPojo>{
        @Override
        public int compare(ResearchResourceGroupPojo g1, ResearchResourceGroupPojo g2) {
            if (g1.getDefaultActivity().getProposal().getStartDate() == null && g2.getDefaultActivity().getProposal().getStartDate() == null)
                return 0;
            if (g1.getDefaultActivity().getProposal().getStartDate() == null)
                return -1;
            if (g2.getDefaultActivity().getProposal().getStartDate() == null)
                return 1;
            return g1.getDefaultActivity().getProposal().getStartDate().compareTo(g2.getDefaultActivity().getProposal().getStartDate());
        }        
    }
    
    public static class EndDateComparator implements Comparator<ResearchResourceGroupPojo>{
        @Override
        public int compare(ResearchResourceGroupPojo g1, ResearchResourceGroupPojo g2) {
            if (g1.getDefaultActivity().getProposal().getEndDate() == null && g2.getDefaultActivity().getProposal().getEndDate() == null)
                return 0;
            if (g1.getDefaultActivity().getProposal().getEndDate() == null)
                return -1;
            if (g2.getDefaultActivity().getProposal().getEndDate() == null)
                return 1;
            return g1.getDefaultActivity().getProposal().getEndDate().compareTo(g2.getDefaultActivity().getProposal().getEndDate());
        }        
    }

    public static class TitleComparator implements Comparator<ResearchResourceGroupPojo>{
        @Override
        public int compare(ResearchResourceGroupPojo g1, ResearchResourceGroupPojo g2) {
            String title1 = (g1.getDefaultActivity().getProposal().getTitle() == null || g1.getDefaultActivity().getProposal().getTitle().getTitle() == null || g1.getDefaultActivity().getProposal().getTitle().getTitle().getContent() == null)?null:g1.getDefaultActivity().getProposal().getTitle().getTitle().getContent().toLowerCase();
            String title2 = (g2.getDefaultActivity().getProposal().getTitle() == null || g2.getDefaultActivity().getProposal().getTitle().getTitle() == null || g2.getDefaultActivity().getProposal().getTitle().getTitle().getContent() == null)?null:g2.getDefaultActivity().getProposal().getTitle().getTitle().getContent().toLowerCase();
            if (title1 == null && title2 == null)
                return 0;
            if (title1 == null)
                return -1;
            if (title2 == null)
                return 1;
            return title1.compareTo(title2);
        }        
    }

}
