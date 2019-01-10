package org.orcid.core.manager.v3.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.OrderColumn;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.orcid.core.adapter.v3.JpaJaxbResearchResourceAdapter;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.OrgManager;
import org.orcid.core.manager.v3.ResearchResourceManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.impl.ResearchResourceManagerReadOnlyImpl;
import org.orcid.core.manager.v3.validator.ActivityValidator;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
import org.orcid.core.utils.v3.SourceEntityUtils;
import org.orcid.jaxb.model.v3.rc2.common.Source;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.notification.amended.AmendedSection;
import org.orcid.jaxb.model.v3.rc2.notification.permission.Item;
import org.orcid.jaxb.model.v3.rc2.notification.permission.ItemType;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResource;
import org.orcid.persistence.dao.ResearchResourceDao;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

public class ResearchResourceManagerImpl extends ResearchResourceManagerReadOnlyImpl implements ResearchResourceManager {

    @Resource 
    private ResearchResourceDao rrDao;
    
    @Resource(name = "jpaJaxbResearchResourceAdapterV3")
    protected JpaJaxbResearchResourceAdapter jpaJaxbResearchResourceAdapter;
    
    @Resource(name = "orgManagerV3")
    private OrgManager orgManager;
    
    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;
    
    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;
    
    @Resource(name = "activityValidatorV3")
    private ActivityValidator activityValidator;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Override
    @Transactional
    public ResearchResource createResearchResource(String orcid, ResearchResource rr, boolean isApiRequest) {
        Source activeSource = sourceManager.retrieveActiveSource();
        activityValidator.validateResearchResource(rr, activeSource, true, isApiRequest, null);

        //Check for duplicates
        List<ResearchResourceEntity> existingRr = rrDao.getByUser(orcid, getLastModified(orcid));
        List<ResearchResource> rrs = jpaJaxbResearchResourceAdapter.toModels(existingRr);
        if(rrs != null && isApiRequest) {
            for(ResearchResource exstingR : rrs) {
                activityValidator.checkExternalIdentifiersForDuplicates(rr.getProposal().getExternalIdentifiers(),
                                exstingR.getProposal().getExternalIdentifiers(), exstingR.getSource(), activeSource);
            }
        }
                
        ResearchResourceEntity researchResourceEntity = jpaJaxbResearchResourceAdapter.toEntity(rr);
        
        List<OrgEntity> updatedOrganizations = orgManager.getOrgEntities(rr.getProposal().getHosts());
        researchResourceEntity.setHosts(updatedOrganizations);
        //set the orgs and parent for the items
        for (int i=0;i<rr.getResourceItems().size();i++){
            List<OrgEntity> itemOrganizations = orgManager.getOrgEntities(rr.getResourceItems().get(i).getHosts());
            researchResourceEntity.getResourceItems().get(i).setHosts(itemOrganizations);
            researchResourceEntity.getResourceItems().get(i).setResearchResourceEntity(researchResourceEntity);
            researchResourceEntity.getResourceItems().get(i).setItemOrder(i);
        }
        
        //Set the source
        SourceEntityUtils.populateSourceAwareEntityFromSource(activeSource, researchResourceEntity);
        
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);        
        researchResourceEntity.setProfile(profile);
        setIncomingPrivacy(researchResourceEntity, profile);        
        DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(researchResourceEntity, isApiRequest);        
        rrDao.persist(researchResourceEntity);
        rrDao.flush();
        if(isApiRequest) {
            notificationManager.sendAmendEmail(orcid, AmendedSection.RESEARCH_RESOURCE, createItemList(researchResourceEntity));
        }        
        return jpaJaxbResearchResourceAdapter.toModel(researchResourceEntity);
    }

    @Override
    public ResearchResource updateResearchResource(String orcid, ResearchResource rr, boolean isApiRequest) {
        Source activeSource = sourceManager.retrieveActiveSource();
        ResearchResourceEntity rre = rrDao.getResearchResource(orcid, rr.getPutCode());
        Visibility originalVisibility = Visibility.valueOf(rre.getVisibility());
        
        //Save the original source
        Source originalSource = SourceEntityUtils.extractSourceFromEntity(rre);
        
        activityValidator.validateResearchResource(rr, activeSource, false, isApiRequest, originalVisibility);
        if(!isApiRequest) {
            List<ResearchResourceEntity> existingFundings = rrDao.getByUser(orcid, getLastModified(orcid));
            for(ResearchResourceEntity existingFunding : existingFundings) {
                ResearchResource existing = jpaJaxbResearchResourceAdapter.toModel(existingFunding);
                if(!existing.getPutCode().equals(rr.getPutCode())) {
                    activityValidator.checkExternalIdentifiersForDuplicates(rr.getProposal().getExternalIdentifiers(),
                                     rr.getProposal().getExternalIdentifiers(), existing.getSource(), activeSource);
                }
            }
        }       
                
        orcidSecurityManager.checkSourceAndThrow(rre);
        
        jpaJaxbResearchResourceAdapter.toEntity(rr, rre);
        rre.setVisibility(originalVisibility.name());        
        
        //Be sure it doesn't overwrite the source
        SourceEntityUtils.populateSourceAwareEntityFromSource(originalSource, rre);
        
        //update orgs (ordering managed by @OrderColumn on lists)
        List<OrgEntity> updatedOrganizations = orgManager.getOrgEntities(rr.getProposal().getHosts());
        rre.setHosts(updatedOrganizations);
        for (int i=0;i<rr.getResourceItems().size();i++){
            rre.getResourceItems().get(i).setHosts(orgManager.getOrgEntities(rr.getResourceItems().get(i).getHosts()));
            rre.getResourceItems().get(i).setResearchResourceEntity(rre);
        }
        
        rre = rrDao.merge(rre);
        rrDao.flush();
        if(!isApiRequest) {
            notificationManager.sendAmendEmail(orcid, AmendedSection.RESEARCH_RESOURCE, createItemList(rre));
        }
        return jpaJaxbResearchResourceAdapter.toModel(rre);
    }

    @Override
    @Transactional
    public boolean checkSourceAndRemoveResearchResource(String orcid, Long researchResourceId) {
        ResearchResourceEntity rr = rrDao.getResearchResource(orcid, researchResourceId);
        orcidSecurityManager.checkSourceAndThrow(rr);        
        boolean result = rrDao.removeResearchResource(orcid, researchResourceId);
        notificationManager.sendAmendEmail(orcid, AmendedSection.RESEARCH_RESOURCE, createItemList(rr));
        return result;
    }

    @Override
    @Transactional
    public boolean updateToMaxDisplay(String orcid, Long researchResourceId) {
        return rrDao.updateToMaxDisplay(orcid,researchResourceId);
    }

    @Override
    public void removeAllResearchResources(String orcid) {
        rrDao.removeResearchResources(orcid);
    }
    
    private void setIncomingPrivacy(ResearchResourceEntity researchResourceEntity, ProfileEntity profile) {
        String incomingWorkVisibility = researchResourceEntity.getVisibility();
        String defaultWorkVisibility = profile.getActivitiesVisibilityDefault();
        if (profile.getClaimed()) {            
            researchResourceEntity.setVisibility(defaultWorkVisibility);            
        } else if (incomingWorkVisibility == null) {
            researchResourceEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        } 
    }
    
    private List<Item> createItemList(ResearchResourceEntity researchResourceEntity) {
        Item item = new Item();
        item.setItemName(researchResourceEntity.getTitle());
        item.setItemType(ItemType.RESEARCH_RESOURCE);
        item.setPutCode(String.valueOf(researchResourceEntity.getId()));
        return Arrays.asList(item);
    }

    @Override
    @Transactional
    public boolean updateVisibilities(String orcid, ArrayList<Long> researchResourceIds, Visibility visibility) {
        return rrDao.updateVisibilities(orcid,researchResourceIds,visibility.name());
    }

    @Override
    @Transactional
    public void removeResearchResources(String effectiveUserOrcid, ArrayList<Long> rrIds) {
        for (long id: rrIds)
            rrDao.removeResearchResource(effectiveUserOrcid, id);
    }

}
