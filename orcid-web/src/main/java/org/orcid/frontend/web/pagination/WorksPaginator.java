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
package org.orcid.frontend.web.pagination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.WorksCacheManager;
import org.orcid.jaxb.model.v3.dev1.common.Visibility;
import org.orcid.jaxb.model.v3.dev1.record.summary.Works;
import org.orcid.pojo.WorkGroup;

public class WorksPaginator {

    private static final int PAGE_SIZE = 50;

    private static final String TITLE_SORT_KEY = "title";

    private static final String DATE_SORT_KEY = "date";

    private static final String TYPE_SORT_KEY = "type";

    @Resource
    private WorksCacheManager worksCacheManager;

    public WorksPage getWorksPage(String orcid, int offset, boolean justPublic, String sort, boolean sortAsc) {
        Works works = worksCacheManager.getGroupedWorks(orcid);
        List<org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup> filteredGroups = filter(works, justPublic);
        filteredGroups = sort(filteredGroups, sort, sortAsc);
        
        WorksPage worksPage = new WorksPage();
        worksPage.setTotalGroups(filteredGroups.size());

        List<WorkGroup> workGroups = new ArrayList<>();
        for (int i = offset; i < Math.min(offset + PAGE_SIZE, filteredGroups.size()); i++) {
            org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup group = filteredGroups.get(i);
            workGroups.add(WorkGroup.valueOf(group, i));
        }
        worksPage.setWorkGroups(workGroups);
        worksPage.setNextOffset(offset + PAGE_SIZE);
        return worksPage;
    }

    public WorksPage refreshWorks(String orcid, int limit, String sort, boolean sortAsc) {
        Works works = worksCacheManager.getGroupedWorks(orcid);
        List<org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup> sortedGroups = sort(works.getWorkGroup(), sort, sortAsc);

        WorksPage worksPage = new WorksPage();
        worksPage.setTotalGroups(works.getWorkGroup().size());

        List<WorkGroup> workGroups = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup group = sortedGroups.get(i);
            workGroups.add(WorkGroup.valueOf(group, i));
        }

        worksPage.setWorkGroups(workGroups);
        worksPage.setNextOffset(limit);
        return worksPage;
    }

    private List<org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup> sort(List<org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup> list, String sort, boolean sortAsc) {
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

    private List<org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup> filter(Works works, boolean justPublic) {
        List<org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup> filteredGroups = new ArrayList<>();
        for (org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup workGroup : works.getWorkGroup()) {
            if (!justPublic || Visibility.PUBLIC.equals(workGroup.getWorkSummary().get(0).getVisibility())) {
                filteredGroups.add(workGroup);
            }
        }
        return filteredGroups;
    }

    public void setWorksCacheManager(WorksCacheManager worksCacheManager) {
        this.worksCacheManager = worksCacheManager;
    }

    private class DateComparator implements Comparator<org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup> {

        @Override
        public int compare(org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup o1, org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup o2) {
            return o1.getWorkSummary().get(0).getPublicationDate().compareTo(o2.getWorkSummary().get(0).getPublicationDate());
        }
    }
    
    private class TitleComparator implements Comparator<org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup> {

        @Override
        public int compare(org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup o1, org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup o2) {
            int comparison = o1.getWorkSummary().get(0).getTitle().getTitle().getContent().compareTo(o2.getWorkSummary().get(0).getTitle().getTitle().getContent());
            if (comparison == 0) {
                comparison = o1.getWorkSummary().get(0).getTitle().getSubtitle().getContent().compareTo(o2.getWorkSummary().get(0).getTitle().getSubtitle().getContent());
            }
            return comparison;
        }
    }
    
    private class TypeComparator implements Comparator<org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup> {

        @Override
        public int compare(org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup o1, org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup o2) {
            return o1.getWorkSummary().get(0).getType().name().compareTo(o2.getWorkSummary().get(0).getType().name());
        }
    }
}
