package org.orcid.frontend.web.pagination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.WorksCacheManager;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Works;
import org.orcid.pojo.WorkGroup;

public class WorksPaginator {

    static final int PAGE_SIZE = 50;

    static final String TITLE_SORT_KEY = "title";

    static final String DATE_SORT_KEY = "date";

    static final String TYPE_SORT_KEY = "type";

    @Resource
    private WorksCacheManager worksCacheManager;
    
    public int getPublicWorksCount(String orcid) {
        Works works = worksCacheManager.getGroupedWorks(orcid);
        List<org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup> groups = filter(works, true);
        return groups.size();
    }

    public WorksPage getWorksPage(String orcid, int offset, boolean justPublic, String sort, boolean sortAsc) {
        Works works = worksCacheManager.getGroupedWorks(orcid);
        List<org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup> filteredGroups = filter(works, justPublic);
        filteredGroups = sort(filteredGroups, sort, sortAsc);
        
        WorksPage worksPage = new WorksPage();
        worksPage.setTotalGroups(filteredGroups.size());

        List<WorkGroup> workGroups = new ArrayList<>();
        for (int i = offset; i < Math.min(offset + PAGE_SIZE, filteredGroups.size()); i++) {
            org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup group = filteredGroups.get(i);
            workGroups.add(WorkGroup.valueOf(group, i, orcid));
        }
        worksPage.setWorkGroups(workGroups);
        worksPage.setNextOffset(offset + PAGE_SIZE);
        return worksPage;
    }

    public WorksPage refreshWorks(String orcid, int limit, String sort, boolean sortAsc) {
        Works works = worksCacheManager.getGroupedWorks(orcid);
        List<org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup> sortedGroups = sort(works.getWorkGroup(), sort, sortAsc);

        WorksPage worksPage = new WorksPage();
        worksPage.setTotalGroups(sortedGroups.size());

        List<WorkGroup> workGroups = new ArrayList<>();
        for (int i = 0; i < limit && i < sortedGroups.size(); i++) {
            org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup group = sortedGroups.get(i);
            workGroups.add(WorkGroup.valueOf(group, i, orcid));
        }

        worksPage.setWorkGroups(workGroups);
        worksPage.setNextOffset(limit);
        return worksPage;
    }
    
    public WorksPage getAllWorks(String orcid, String sort, boolean sortAsc) {
        Works works = worksCacheManager.getGroupedWorks(orcid);
        List<org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup> sortedGroups = sort(works.getWorkGroup(), sort, sortAsc);

        WorksPage worksPage = new WorksPage();
        worksPage.setTotalGroups(sortedGroups.size());

        List<WorkGroup> workGroups = new ArrayList<>();
        for (int i = 0; i < sortedGroups.size(); i++) {
            org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup group = sortedGroups.get(i);
            workGroups.add(WorkGroup.valueOf(group, i, orcid));
        }

        worksPage.setWorkGroups(workGroups);
        worksPage.setNextOffset(sortedGroups.size());
        return worksPage;
    }

    private List<org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup> sort(List<org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup> list, String sort, boolean sortAsc) {
        if (TITLE_SORT_KEY.equals(sort)) {
            Collections.sort(list, new TitleComparator());
        } else if (DATE_SORT_KEY.equals(sort)) {
            Collections.sort(list, new DateComparator());
        } else if (TYPE_SORT_KEY.equals(sort)) {
            Collections.sort(list, new TypeComparator());
        }
        
        if (!sortAsc) {
            Collections.reverse(list);
        }
        return list;
    }

    private List<org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup> filter(Works works, boolean justPublic) {
        List<org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup> filteredGroups = new ArrayList<>();
        for (org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup workGroup : works.getWorkGroup()) {
            if (!justPublic || Visibility.PUBLIC.equals(workGroup.getWorkSummary().get(0).getVisibility())) {
                filteredGroups.add(workGroup);
            }
        }
        return filteredGroups;
    }

    public void setWorksCacheManager(WorksCacheManager worksCacheManager) {
        this.worksCacheManager = worksCacheManager;
    }

    private class DateComparator implements Comparator<org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup> {

        @Override
        public int compare(org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup o1, org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup o2) {
            if (o1.getWorkSummary().get(0).getPublicationDate() == null && o2.getWorkSummary().get(0).getPublicationDate() == null) {
                return new TitleComparator().compare(o1, o2) * -1; // reverse secondary order
            }
            
            if (o1.getWorkSummary().get(0).getPublicationDate() == null) {
                return -1;
            }
            
            if (o2.getWorkSummary().get(0).getPublicationDate() == null) {
                return 1;
            }
            
            return o1.getWorkSummary().get(0).getPublicationDate().compareTo(o2.getWorkSummary().get(0).getPublicationDate());
        }
    }
    
    private class TitleComparator implements Comparator<org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup> {

        @Override
        public int compare(org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup o1, org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup o2) {
            String firstTitle = getTitle(o1.getWorkSummary().get(0));
            String secondTitle = getTitle(o2.getWorkSummary().get(0));
            
            if (firstTitle == null && secondTitle != null) {
                return -1;
            }
            
            if (secondTitle == null && firstTitle != null) {
                return 1;
            }
            
            int comparison = 0;
            if (firstTitle != null && secondTitle != null) {
                comparison = firstTitle.compareTo(secondTitle);
            }
            
            if (comparison == 0) {
                String firstSubtitle = getSubtitle(o1.getWorkSummary().get(0));
                String secondSubtitle = getSubtitle(o2.getWorkSummary().get(0));
                
                if (firstSubtitle == null && secondSubtitle == null) {
                    return 0;
                }
                
                if (firstSubtitle == null) {
                    return -1;
                }
                
                if (secondSubtitle == null) {
                    return 1;
                }
                
                comparison = firstSubtitle.compareTo(secondSubtitle);
            }
            return comparison;
        }
        
        private String getTitle(WorkSummary workSummary) {
            if (workSummary.getTitle() == null) {
                return null;
            }
            
            if (workSummary.getTitle().getTitle() == null) {
                return null;
            }
            
            if (workSummary.getTitle().getTitle().getContent() == null) {
                return null;
            }
            
            return workSummary.getTitle().getTitle().getContent().toLowerCase();
        }
        
        private String getSubtitle(WorkSummary workSummary) {
            if (workSummary.getTitle() == null) {
                return null;
            }
            
            if (workSummary.getTitle().getSubtitle() == null) {
                return null;
            }
            
            if (workSummary.getTitle().getSubtitle().getContent() == null) {
                return null;
            }
            
            return workSummary.getTitle().getSubtitle().getContent().toLowerCase();
        }
    }
    
    private class TypeComparator implements Comparator<org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup> {

        @Override
        public int compare(org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup o1, org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup o2) {
            if (o1.getWorkSummary().get(0).getType() == null && o2.getWorkSummary().get(0).getType() == null) {
                return 0;
            }
            
            if (o1.getWorkSummary().get(0).getType() == null) {
                return -1;
            }
            
            if (o2.getWorkSummary().get(0).getType() == null) {
                return 1;
            }
            
            return o1.getWorkSummary().get(0).getType().name().compareTo(o2.getWorkSummary().get(0).getType().name());
        }
    }

}
