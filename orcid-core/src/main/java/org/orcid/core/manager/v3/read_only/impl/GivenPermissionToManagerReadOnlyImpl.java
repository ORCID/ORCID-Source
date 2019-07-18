package org.orcid.core.manager.v3.read_only.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.read_only.GivenPermissionToManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.utils.v3.OrcidIdentifierUtils;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.jpa.entities.GivenPermissionByEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.pojo.DelegateForm;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.DateUtils;
import org.springframework.cache.annotation.Cacheable;

public class GivenPermissionToManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements GivenPermissionToManagerReadOnly {

    @Resource
    private OrcidIdentifierUtils orcidIdentifierUtils;
    
    @Resource
    private GivenPermissionToDao givenPermissionToDaoReadOnly;
    
    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManagerReadOnlyV3;
    
    @Override
    @Cacheable(value = "delegates-by-giver", key = "#giverOrcid.concat('-').concat(#lastModified)")
    public List<DelegateForm> findByGiver(String giverOrcid, long lastModified) {
        List<DelegateForm> delegates = new ArrayList<DelegateForm>();
        List<GivenPermissionToEntity> list = givenPermissionToDaoReadOnly.findByGiver(giverOrcid);
        
        for(GivenPermissionToEntity element : list) {
            DelegateForm form = new DelegateForm();
            form.setApprovalDate(DateUtils.convertToXMLGregorianCalendar(element.getApprovalDate()));
            form.setGiverOrcid(orcidIdentifierUtils.buildOrcidIdentifier(element.getGiver()));
            
            String orcid = element.getReceiver().getId();
            form.setReceiverOrcid(orcidIdentifierUtils.buildOrcidIdentifier(orcid));
            form.setReceiverName(Text.valueOf(recordNameManagerReadOnlyV3.fetchDisplayableDisplayName(orcid)));
            delegates.add(form);
        }
        
        return delegates;
    }

    @Override
    @Cacheable(value = "delegates-by-receiver", key = "#receiverOrcid.concat('-').concat(#lastModified)")
    public List<DelegateForm> findByReceiver(String receiverOrcid, long lastModified) {
        List<DelegateForm> delegates = new ArrayList<DelegateForm>();
        List<GivenPermissionByEntity> list = givenPermissionToDaoReadOnly.findByReceiver(receiverOrcid);
        for(GivenPermissionByEntity element : list) {
            DelegateForm form = new DelegateForm();
            form.setApprovalDate(DateUtils.convertToXMLGregorianCalendar(element.getApprovalDate()));
            form.setLastModifiedDate(DateUtils.convertToXMLGregorianCalendar(element.getGiver().getLastModified()));
            form.setReceiverOrcid(orcidIdentifierUtils.buildOrcidIdentifier(element.getReceiver()));            
            
            String giverOrcid = element.getGiver().getId();
            form.setGiverName(Text.valueOf(recordNameManagerReadOnlyV3.fetchDisplayableDisplayName(giverOrcid)));
            form.setGiverOrcid(orcidIdentifierUtils.buildOrcidIdentifier(giverOrcid));
            delegates.add(form);
        }
        return delegates;
    }
    
    @Override
    public DelegateForm findByGiverAndReceiverOrcid(String giverOrcid, String receiverOrcid) {
        GivenPermissionToEntity entity = givenPermissionToDaoReadOnly.findByGiverAndReceiverOrcid(giverOrcid, receiverOrcid);
        if(entity != null) {
            DelegateForm form = new DelegateForm();
            form.setApprovalDate(DateUtils.convertToXMLGregorianCalendar(entity.getApprovalDate()));
            form.setGiverOrcid(orcidIdentifierUtils.buildOrcidIdentifier(entity.getGiver()));
            form.setReceiverOrcid(orcidIdentifierUtils.buildOrcidIdentifier(receiverOrcid));
            form.setReceiverName(Text.valueOf(recordNameManagerReadOnlyV3.fetchDisplayableDisplayName(receiverOrcid)));
            return form;
        }
        return null;
    }    
}
