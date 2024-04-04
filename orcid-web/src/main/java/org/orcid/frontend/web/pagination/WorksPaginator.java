package org.orcid.frontend.web.pagination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.WorksCacheManager;
import org.orcid.core.manager.v3.WorksExtendedCacheManager;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.core.utils.comparators.DateComparator;
import org.orcid.core.utils.comparators.DateComparatorWorkGroupExtended;
import org.orcid.core.utils.comparators.TitleComparator;
import org.orcid.core.utils.comparators.TitleComparatorWorkGroupExtended;
import org.orcid.core.utils.comparators.TypeComparator;
import org.orcid.core.utils.comparators.TypeComparatorWorkGroupExtended;
import org.orcid.core.utils.v3.SourceUtils;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.pojo.WorkGroupExtended;
import org.orcid.pojo.WorkSummaryExtended;
import org.orcid.pojo.WorksExtended;
import org.orcid.pojo.grouping.WorkGroup;

public class WorksPaginator {
    public static final String TITLE_SORT_KEY = "title";

    public static final String DATE_SORT_KEY = "date";

    public static final String TYPE_SORT_KEY = "type";
    
    @Resource(name = "workManagerReadOnlyV3")
    private WorkManagerReadOnly workManagerReadOnly;

    @Resource
    private WorksCacheManager worksCacheManager;
    
    @Resource
    private WorksExtendedCacheManager worksExtendedCacheManager;
    
    public Page<WorkGroup> getWorksPage(String orcid, int offset, int pageSize, boolean justPublic, String sort, boolean sortAsc) {
        Works works = worksCacheManager.getGroupedWorks(orcid);
        Page<WorkGroup> worksPage = new Page<WorkGroup>();
        if (works != null) {
            List<org.orcid.jaxb.model.v3.release.record.summary.WorkGroup> filteredGroups = filter(works, justPublic);
            if ("source".equals(sort)) {
                filteredGroups = sortBySource(filteredGroups, sortAsc, orcid);
            } else {
                filteredGroups = sort(filteredGroups, sort, sortAsc);
            }

            worksPage.setTotalGroups(filteredGroups.size());

            List<WorkGroup> workGroups = new ArrayList<>();
            for (int i = offset; i < Math.min(offset + pageSize, filteredGroups.size()); i++) {
                org.orcid.jaxb.model.v3.release.record.summary.WorkGroup group = filteredGroups.get(i);
                workGroups.add(WorkGroup.valueOf(group, i, orcid));
            }
            worksPage.setGroups(workGroups);
            worksPage.setNextOffset(offset + pageSize);
        }
        return worksPage;
    }

    public Page<WorkGroup> getWorksExtendedPage(String orcid, int offset, int pageSize, boolean justPublic, String sort, boolean sortAsc) {
        WorksExtended works = worksExtendedCacheManager.getGroupedWorksExtended(orcid);
        Page<WorkGroup> worksPage = new Page<WorkGroup>();
        if (works != null) {
            List<WorkGroupExtended> filteredGroups = filterWorksExtended(works, justPublic);
            if ("source".equals(sort)) {
                filteredGroups = sortBySourceExtended(filteredGroups, sortAsc, orcid);
            } else {
                filteredGroups = sortExtended(filteredGroups, sort, sortAsc);
            }

            worksPage.setTotalGroups(filteredGroups.size());

            List<WorkGroup> workGroups = new ArrayList<>();
            for (int i = offset; i < Math.min(offset + pageSize, filteredGroups.size()); i++) {
                WorkGroupExtended group = filteredGroups.get(i);
                workGroups.add(WorkGroup.valueOf(group, i, orcid));
            }
            worksPage.setGroups(workGroups);
            worksPage.setNextOffset(offset + pageSize);
        }
        return worksPage;
    }

    public Page<WorkGroup> refreshWorks(String orcid, int limit, String sort, boolean sortAsc) {
        Works works = worksCacheManager.getGroupedWorks(orcid);
        List<org.orcid.jaxb.model.v3.release.record.summary.WorkGroup> sortedGroups = sort(works.getWorkGroup(), sort, sortAsc);

        Page<WorkGroup> worksPage = new Page<WorkGroup>();
        worksPage.setTotalGroups(sortedGroups.size());

        List<WorkGroup> workGroups = new ArrayList<>();
        for (int i = 0; i < limit && i < sortedGroups.size(); i++) {
            org.orcid.jaxb.model.v3.release.record.summary.WorkGroup group = sortedGroups.get(i);
            workGroups.add(WorkGroup.valueOf(group, i, orcid));
        }

        worksPage.setGroups(workGroups);
        worksPage.setNextOffset(limit);
        return worksPage;
    }
    
    public Page<WorkGroup> getAllWorks(String orcid, boolean justPublic, String sort, boolean sortAsc) {
        Works works = worksCacheManager.getGroupedWorks(orcid);
        Page<WorkGroup> worksPage = new Page<WorkGroup>();
        if (works != null) {
            List<org.orcid.jaxb.model.v3.release.record.summary.WorkGroup> filteredGroups = filter(works, justPublic);
            filteredGroups = sort(filteredGroups, sort, sortAsc);

            worksPage.setTotalGroups(filteredGroups.size());

            List<WorkGroup> workGroups = new ArrayList<>();
            for (int i = 0; i < filteredGroups.size(); i++) {
                org.orcid.jaxb.model.v3.release.record.summary.WorkGroup group = filteredGroups.get(i);
                workGroups.add(WorkGroup.valueOf(group, i, orcid));
            }

            worksPage.setGroups(workGroups);
            worksPage.setNextOffset(filteredGroups.size());
        }
        return worksPage;
    }

    public void setWorksCacheManager(WorksCacheManager worksCacheManager) {
        this.worksCacheManager = worksCacheManager;
    }

    public void setWorksExtendedCacheManager(WorksExtendedCacheManager worksExtendedCacheManager) {
        this.worksExtendedCacheManager = worksExtendedCacheManager;
    }        

    public List<org.orcid.jaxb.model.v3.release.record.summary.WorkGroup> sortBySource(List<org.orcid.jaxb.model.v3.release.record.summary.WorkGroup> workGroups, boolean sortAsc, String orcid) {
        List<org.orcid.jaxb.model.v3.release.record.summary.WorkGroup> selfAsserted = workGroups.stream()
                .filter(work -> SourceUtils.isSelfAsserted(work.getWorkSummary().get(0).getSource(), orcid))
                .collect(Collectors.toList());

        List<org.orcid.jaxb.model.v3.release.record.summary.WorkGroup> validated = workGroups.stream()
                .filter(work -> !SourceUtils.isSelfAsserted(work.getWorkSummary().get(0).getSource(), orcid))
                .collect(Collectors.toList());

        selfAsserted.sort(new TitleComparator());
        validated.sort(new TitleComparator());

        return (sortAsc ? Stream.concat(validated.stream(), selfAsserted.stream()) : Stream.concat(selfAsserted.stream(), validated.stream()))
                .collect(Collectors.toList());
    }

    public List<WorkGroupExtended> sortBySourceExtended(List<WorkGroupExtended> workGroups, boolean sortAsc, String orcid) {
        List<WorkGroupExtended> selfAsserted = workGroups.stream()
                .filter(work -> SourceUtils.isSelfAsserted(work.getWorkSummary().get(0).getSource(), orcid))
                .collect(Collectors.toList());

        List<WorkGroupExtended> validated = workGroups.stream()
                .filter(work -> !SourceUtils.isSelfAsserted(work.getWorkSummary().get(0).getSource(), orcid))
                .collect(Collectors.toList());

        selfAsserted.sort(new TitleComparatorWorkGroupExtended());
        validated.sort(new TitleComparatorWorkGroupExtended());

        return (sortAsc ? Stream.concat(validated.stream(), selfAsserted.stream()) : Stream.concat(selfAsserted.stream(), validated.stream()))
                .collect(Collectors.toList());
    }
        
    private List<org.orcid.jaxb.model.v3.release.record.summary.WorkGroup> filter(Works works, boolean justPublic) {
        List<org.orcid.jaxb.model.v3.release.record.summary.WorkGroup> filteredGroups = new ArrayList<>();
        for (org.orcid.jaxb.model.v3.release.record.summary.WorkGroup workGroup : works.getWorkGroup()) {
            
            Iterator<WorkSummary> summariesIt = workGroup.getWorkSummary().iterator();
            while(summariesIt.hasNext()) {
                WorkSummary w = summariesIt.next();
                if(justPublic && !Visibility.PUBLIC.equals(w.getVisibility())) {
                    summariesIt.remove();
                }
            }
            
            if(!workGroup.getWorkSummary().isEmpty()) {
                filteredGroups.add(workGroup);            
            }            
        }
        return filteredGroups;
    } 
    
    private List<org.orcid.jaxb.model.v3.release.record.summary.WorkGroup> sort(List<org.orcid.jaxb.model.v3.release.record.summary.WorkGroup> list, String sort, boolean sortAsc) {
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
    
    private List<WorkGroupExtended> sortExtended(List<WorkGroupExtended> list, String sort, boolean sortAsc) {
        if (TITLE_SORT_KEY.equals(sort)) {
            Collections.sort(list, new TitleComparatorWorkGroupExtended());
        } else if (DATE_SORT_KEY.equals(sort)) {
            Collections.sort(list, new DateComparatorWorkGroupExtended());
        } else if (TYPE_SORT_KEY.equals(sort)) {
            Collections.sort(list, new TypeComparatorWorkGroupExtended());
        }

        if (!sortAsc) {
            Collections.reverse(list);
        }
        return list;
    }
    
    private List<WorkGroupExtended> filterWorksExtended(WorksExtended works, boolean justPublic) {
        List<WorkGroupExtended> filteredGroups = new ArrayList<>();
        for (WorkGroupExtended workGroup : works.getWorkGroup()) {

            Iterator<WorkSummaryExtended> summariesIt = workGroup.getWorkSummary().iterator();
            while(summariesIt.hasNext()) {
                WorkSummaryExtended w = summariesIt.next();
                if(justPublic && !Visibility.PUBLIC.equals(w.getVisibility())) {
                    summariesIt.remove();
                }
            }

            if(!workGroup.getWorkSummary().isEmpty()) {
                filteredGroups.add(workGroup);
            }
        }
        return filteredGroups;
    }
}
