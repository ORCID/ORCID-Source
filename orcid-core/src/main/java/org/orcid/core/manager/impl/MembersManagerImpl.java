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

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.constants.DefaultPreferences;
import org.orcid.core.exception.OrcidClientGroupManagementException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.ClientManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.MembersManager;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.core.manager.OrcidGenerationManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ThirdPartyLinkManager;
import org.orcid.core.manager.read_only.ClientManagerReadOnly;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.jaxb.model.client_v2.Client;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.common_v2.OrcidType;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.OrcidGrantedAuthority;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.pojo.ajaxForm.Member;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.Text;
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
    
    @Resource
    private OrcidGenerationManager orcidGenerationManager;
    
    @Override
    public Member createMember(String memberName, String email, String salesforceId, ) {
        String memberOrcidId = createMemberProfile(memberName, email, salesforceId); 
        Member member = new Member();
        member.setEmail(Text.valueOf(email));
        member.setGroupName(Text.valueOf(memberName));
        member.setGroupOrcid(Text.valueOf(memberOrcidId));
        
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
    
    
    
    
    
    
    private String createMemberProfile(String memberName, String email, String salesforceId) {
        Date now = new Date();
        String orcid = orcidGenerationManager.createNewOrcid();
        ProfileEntity newRecord = new ProfileEntity();
        newRecord.setId(orcid);
        
        try {
            newRecord.setHashedOrcid(encryptionManager.sha256Hash(orcid));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        
        newRecord.setOrcidType(OrcidType.GROUP);
        newRecord.setDateCreated(now);
        newRecord.setLastModified(now);
        newRecord.setSubmissionDate(now);
        newRecord.setClaimed(true);
        newRecord.setEnableDeveloperTools(false);
        newRecord.setRecordLocked(false);
        newRecord.setReviewed(false);
        newRecord.setEnableNotifications(DefaultPreferences.NOTIFICATIONS_ENABLED);
        newRecord.setUsedRecaptchaOnRegistration(false);
        newRecord.setSendEmailFrequencyDays(Float.valueOf(DefaultPreferences.SEND_EMAIL_FREQUENCY_DAYS));
        newRecord.setSendMemberUpdateRequests(DefaultPreferences.SEND_MEMBER_UPDATE_REQUESTS);
        
        newRecord.setCreationMethod(CreationMethod.DIRECT.value());
        newRecord.setSendChangeNotifications(false);
        newRecord.setSendOrcidNews(false);
        newRecord.setLocale(org.orcid.jaxb.model.common_v2.Locale.EN);
        // Visibility defaults
        newRecord.setActivitiesVisibilityDefault(Visibility.PRIVATE);
        
        // No password
        newRecord.setEncryptedPassword(null);

        // Set primary email
        EmailEntity emailEntity = new EmailEntity();
        emailEntity.setId(email);
        emailEntity.setProfile(newRecord);
        emailEntity.setPrimary(true);
        emailEntity.setCurrent(true);
        emailEntity.setVerified(true);
        // Email is private by default
        emailEntity.setVisibility(Visibility.PRIVATE);
        emailEntity.setSourceId(orcid);
        Set<EmailEntity> emails = new HashSet<>();
        emails.add(emailEntity);
        //Add all emails to record
        newRecord.setEmails(emails);

        // Set the name
        RecordNameEntity recordNameEntity = new RecordNameEntity();
        recordNameEntity.setDateCreated(now);
        recordNameEntity.setLastModified(now);
        recordNameEntity.setProfile(newRecord);
        // Name is public by default
        recordNameEntity.setVisibility(Visibility.PUBLIC);
        recordNameEntity.setCreditName(memberName);
        newRecord.setRecordNameEntity(recordNameEntity);

        // Set authority
        OrcidGrantedAuthority authority = new OrcidGrantedAuthority();
        authority.setProfileEntity(newRecord);
        authority.setAuthority(OrcidWebRole.ROLE_GROUP.getAuthority());
        Set<OrcidGrantedAuthority> authorities = new HashSet<OrcidGrantedAuthority>(1);
        authorities.add(authority);
        newRecord.setAuthorities(authorities);

        profileDao.persist(newRecord);
        profileDao.flush();
        return orcid;
    }
}
