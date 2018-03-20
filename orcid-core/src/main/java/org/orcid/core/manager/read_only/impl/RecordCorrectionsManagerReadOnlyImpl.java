package org.orcid.core.manager.read_only.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbInvalidRecordDataChangeAdapter;
import org.orcid.core.manager.read_only.RecordCorrectionsManagerReadOnly;
import org.orcid.model.record_correction.RecordCorrection;
import org.orcid.model.record_correction.RecordCorrectionsPage;
import org.orcid.persistence.dao.InvalidRecordDataChangeDao;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

public class RecordCorrectionsManagerReadOnlyImpl implements RecordCorrectionsManagerReadOnly {

    @Resource
    private InvalidRecordDataChangeDao dao;
    
    @Resource
    private JpaJaxbInvalidRecordDataChangeAdapter adapter;
    
    private static final Boolean DESCENDING = true;
    private static final Boolean ASCENDING = false;
    
    @Override
    @Cacheable(value = "invalid-record-data-change-page-desc", key = "(#lastElement == null ? 'none' : #lastElement.toString()).concat('-').concat(#pageSize.toString())")
    public RecordCorrectionsPage getInvalidRecordDataChangesDescending(Long lastElement, Long pageSize) {
        List<InvalidRecordDataChangeEntity> entities = dao.getByDateCreated(lastElement, pageSize, DESCENDING);
        if(entities == null || entities.isEmpty()) {
            throw new IllegalArgumentException("Unable to find a page with the following params: lastElement=" + lastElement + " pageSize: " + pageSize + " descending order");
        }
        List<RecordCorrection> elements = adapter.toInvalidRecordDataChanges(entities);
        Long first = null;
        Long last = null;
        for(RecordCorrection element: elements) {
            if(first == null || element.getSequence() > first) {
                first = element.getSequence();
            }
            if(last == null || element.getSequence() < last) {
                last = element.getSequence();
            }
        }
        Boolean haveNext = dao.haveNext(last, DESCENDING);
        Boolean havePrevious = dao.havePrevious(first, DESCENDING);
        RecordCorrectionsPage page = new RecordCorrectionsPage();
        page.setFirstElementId(first);
        page.setLastElementId(last);
        page.setHaveNext(haveNext);
        page.setHavePrevious(havePrevious);
        page.setRecordCorrections(elements);
        return page;
    }

    @Override
    @Cacheable(value = "invalid-record-data-change-page-asc", key = "(#lastElement == null ? 'none' : #lastElement.toString()).concat('-').concat(#pageSize.toString())")
    public RecordCorrectionsPage getInvalidRecordDataChangesAscending(Long lastElement, Long pageSize) {
        List<InvalidRecordDataChangeEntity> entities = dao.getByDateCreated(lastElement, pageSize, ASCENDING);
        if(entities == null || entities.isEmpty()) {
            throw new IllegalArgumentException("Unable to find a page with the following params: lastElement=" + lastElement + " pageSize: " + pageSize + " ascending order");
        }
        
        List<RecordCorrection> elements = adapter.toInvalidRecordDataChanges(entities);
        Long first = null;
        Long last = null;
        for(RecordCorrection element: elements) {
            if(first == null || element.getSequence() < first) {
                first = element.getSequence();
            }
            if(last == null || element.getSequence() > last) {
                last = element.getSequence();
            }
        }
        Boolean haveNext = dao.haveNext(last, ASCENDING);
        Boolean havePrevious = dao.havePrevious(first, ASCENDING);
        RecordCorrectionsPage page = new RecordCorrectionsPage();
        page.setFirstElementId(first);
        page.setLastElementId(last);
        page.setHaveNext(haveNext);
        page.setHavePrevious(havePrevious);
        page.setRecordCorrections(elements);
        return page;
    }

    @Override
    @CacheEvict(value = { "invalid-record-data-change-page-desc", "invalid-record-data-change-page-asc" }, allEntries = true)
    public void cacheEvict() {
        return;
    }
}
