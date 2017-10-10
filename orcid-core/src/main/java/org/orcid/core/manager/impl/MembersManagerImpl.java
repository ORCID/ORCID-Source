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
package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.exception.OrcidClientGroupManagementException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.ClientManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.MembersManager;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ThirdPartyLinkManager;
import org.orcid.core.manager.read_only.ClientManagerReadOnly;
import org.orcid.jaxb.model.client_v2.Client;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.Member;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class MembersManagerImpl implements MembersManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MembersManagerImpl.class);
    
    @Resource
    private LocaleManager localeManager;
    
    @Resource
    EmailManager emailManager;

    @Resource
    ProfileEntityManager profileEntityManager;

    @Resource
    OrcidClientGroupManager orcidClientGroupManager;

    @Resource
    ClientDetailsManager clientDetailsManager;
    
    @Resource(name = "profileEntityCacheManager")
    ProfileEntityCacheManager profileEntityCacheManager; 
    
    @Resource
    private ThirdPartyLinkManager thirdPartyLinkManager;
    
    @Resource
    private EncryptionManager encryptionManager;
    
    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private ClientManager clientManager;
    
    @Resource
    private ClientManagerReadOnly clientManagerReadOnly;
    
    @Override
    public Member createMember(String memberName, String email, String salesforceId) {
        //TODO: Refactor 
        OrcidClientGroup orcidClientGroup = newMember.toOrcidClientGroup();
        orcidClientGroup = orcidClientGroupManager.createGroup(orcidClientGroup);
        newMember.setGroupOrcid(Text.valueOf(orcidClientGroup.getGroupOrcid()));
        return newMember;
    }

    @Override
    public Member updateMemeber(Member member) {
        //TODO: Refactor
        OrcidClientGroup orcidClientGroup = member.toOrcidClientGroup();
        orcidClientGroupManager.updateGroup(orcidClientGroup);
        clearCache();
        return member;
    }

    @Override
    @Transactional
    public Member getMember(String memberId) {
        Member member = new Member();
        String orcid = memberId;
        if (!OrcidStringUtils.isValidOrcid(memberId)) {
            Map<String, String> ids = emailManager.findOricdIdsByCommaSeparatedEmails(memberId);
            //Check if it is using the email
            if (ids != null && ids.containsKey(memberId)) {
                orcid = ids.get(memberId);
            } else {
                //Check if can find it by name
                try {
                    orcid = profileEntityManager.findByCreditName(memberId);
                } catch (Exception e) {
                    member.getErrors().add(getMessage("manage_member.email_not_found"));
                    orcid = null;
                }                
            } 
        }

        if(PojoUtil.isEmpty(orcid)) {
            member.getErrors().add(getMessage("manage_member.email_not_found"));            
        } else {
            if (profileEntityManager.orcidExists(orcid)) {
                MemberType groupType = profileEntityManager.getGroupType(orcid);
                if (groupType != null) {
                    ProfileEntity memberProfile = profileDao.find(orcid);                    
                    member = Member.fromProfileEntity(memberProfile);
                    Set<Client> clients = clientManagerReadOnly.getClients(orcid);
                    List<org.orcid.pojo.ajaxForm.Client> clientsList = new ArrayList<org.orcid.pojo.ajaxForm.Client>();
                    clients.forEach(c -> {clientsList.add(org.orcid.pojo.ajaxForm.Client.fromModelObject(c));});
                    member.setClients(clientsList);
                } else {
                    member.getErrors().add(getMessage("manage_members.orcid_is_not_a_member"));
                }
            } else {
                member.getErrors().add(getMessage("manage_members.orcid_doesnt_exists"));
            }
        }
        return member;
    }

    @Override
    public Client getClient(String clientId) {
        return clientManagerReadOnly.get(clientId);                       
    }

    @Override
    public Client updateClient(Client client) {
        try {
            client = clientManager.edit(client);
            clearCache();
        } catch (OrcidClientGroupManagementException e) {
            LOGGER.error(e.getMessage());
            throw new IllegalArgumentException(e);
        }

        return client;
    }    
    
    private String getMessage(String message) {
        return localeManager.resolveMessage(message);
    }
    
    /**
     * Since the groups have changed, the cache version must be updated on
     * database and all caches have to be evicted.
     * */
    public void clearCache() {
        // Updates cache database version
        thirdPartyLinkManager.updateDatabaseCacheVersion();
        // Evict current cache
        thirdPartyLinkManager.evictAll();
    }
}
