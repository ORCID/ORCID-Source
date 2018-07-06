package org.orcid.core.manager.v3.impl;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidGenerationManager;
import org.orcid.core.manager.ThirdPartyLinkManager;
import org.orcid.core.manager.v3.ClientManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.MembersManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.ClientManagerReadOnly;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.v3.rc1.client.Client;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ClientScopeDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidGrantedAuthority;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.Member;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class MembersManagerImpl implements MembersManager {

    @Resource
    private LocaleManager localeManager;

    @Resource(name = "emailManagerV3")
    EmailManager emailManager;

    @Resource(name = "profileEntityManagerV3")
    ProfileEntityManager profileEntityManager;

    @Resource
    private ThirdPartyLinkManager thirdPartyLinkManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private ProfileDao profileDao;

    @Resource(name = "clientManagerV3")
    private ClientManager clientManager;

    @Resource(name = "clientManagerReadOnlyV3")
    private ClientManagerReadOnly clientManagerReadOnly;

    @Resource
    private OrcidGenerationManager orcidGenerationManager;
    
    @Resource
    private ClientDetailsDao clientDetailsDao;
    
    @Resource
    private ClientScopeDao clientScopeDao;
    
    @Resource
    private TransactionTemplate transactionTemplate;
    
    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;
    
    @Resource
    private EmailFrequencyManager emailFrequencyManager;

    @Override
    public Member createMember(Member member) throws IllegalArgumentException {
        if (emailManager.emailExists(member.getEmail().getValue())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Date now = new Date();
        String orcid = orcidGenerationManager.createNewOrcid();
        ProfileEntity newRecord = new ProfileEntity();
        newRecord.setId(orcid);
        newRecord.setOrcidType(org.orcid.jaxb.model.common_v2.OrcidType.GROUP.name());
        
        try {
            newRecord.setHashedOrcid(encryptionManager.sha256Hash(orcid));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        newRecord.setActivitiesVisibilityDefault(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        newRecord.setClaimed(true);
        newRecord.setCreationMethod(CreationMethod.DIRECT.value());
        newRecord.setDateCreated(now);
        newRecord.setEnableDeveloperTools(false);       
        newRecord.setEncryptedPassword(null);
        newRecord.setGroupType(MemberType.fromValue(member.getType().getValue()).name());
        newRecord.setLastModified(now);
        newRecord.setLocale(org.orcid.jaxb.model.common_v2.Locale.EN.name());
        newRecord.setRecordLocked(false);
        newRecord.setReviewed(false);
        newRecord.setSalesforeId(PojoUtil.isEmpty(member.getSalesforceId()) ? null : member.getSalesforceId().getValue());
        newRecord.setSubmissionDate(now);
        newRecord.setUsedRecaptchaOnRegistration(false);
        
        // Set primary email
        EmailEntity emailEntity = new EmailEntity();
        emailEntity.setId(member.getEmail().getValue());
        try {
            emailEntity.setEmailHash(encryptionManager.sha256Hash(member.getEmail().getValue().trim().toLowerCase()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        emailEntity.setProfile(newRecord);
        emailEntity.setPrimary(true);
        emailEntity.setCurrent(true);
        emailEntity.setVerified(true);
        // Email is private by default
        emailEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        String sourceId = sourceEntity.getSourceProfile().getId();
        emailEntity.setSourceId(sourceId);
        Set<EmailEntity> emails = new HashSet<>();
        emails.add(emailEntity);
        // Add all emails to record
        newRecord.setEmails(emails);

        // Set the name
        RecordNameEntity recordNameEntity = new RecordNameEntity();
        recordNameEntity.setDateCreated(now);
        recordNameEntity.setLastModified(now);
        recordNameEntity.setProfile(newRecord);
        recordNameEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.name());
        recordNameEntity.setCreditName(member.getGroupName().getValue());
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
        
        emailFrequencyManager.createOnRegister(orcid, SendEmailFrequency.WEEKLY, SendEmailFrequency.WEEKLY, SendEmailFrequency.WEEKLY, false);
        
        member.setGroupOrcid(Text.valueOf(orcid));
        return member;
    }

    @Override
    public Member updateMemeber(Member member) throws IllegalArgumentException {
        String memberId = member.getGroupOrcid().getValue();
        String name = member.getGroupName().getValue();
        String email = member.getEmail().getValue();
        String salesForceId = member.getSalesforceId() == null ? null : member.getSalesforceId().getValue();
        MemberType memberType = MemberType.fromValue(member.getType().getValue());

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                boolean memberChangedType = false;
                ProfileEntity memberEntity = profileDao.find(member.getGroupOrcid().getValue());
                memberEntity.setLastModified(new Date());
                memberEntity.setIndexingStatus(IndexingStatus.PENDING);
                memberEntity.getRecordNameEntity().setCreditName(name);
                memberEntity.setSalesforeId(salesForceId);

                if (!memberType.equals(memberEntity.getGroupType())) {
                    memberEntity.setGroupType(memberType.name());
                    memberChangedType = true;
                }

                EmailEntity primaryEmail = memberEntity.getPrimaryEmail();
                if (!email.equals(primaryEmail.getId())) {
                    if (emailManager.emailExists(email)) {
                        throw new IllegalArgumentException("Email already exists");
                    }
                    Date now = new Date();
                    EmailEntity newPrimaryEmail = new EmailEntity();
                    newPrimaryEmail.setLastModified(now);
                    newPrimaryEmail.setDateCreated(now);
                    newPrimaryEmail.setCurrent(true);
                    newPrimaryEmail.setId(email);
                    newPrimaryEmail.setPrimary(true);
                    newPrimaryEmail.setVerified(true);
                    newPrimaryEmail.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
                    memberEntity.setPrimaryEmail(newPrimaryEmail);
                }

                profileDao.merge(memberEntity);

                if (memberChangedType) {
                    updateClientTypeDueMemberTypeUpdate(memberId, memberType);
                }
            }
        });
        
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
            // Check if it is using the email
            if (ids != null && ids.containsKey(memberId)) {
                orcid = ids.get(memberId);
            } else {
                // Check if can find it by name
                try {
                    orcid = profileEntityManager.findByCreditName(memberId);
                } catch (Exception e) {
                    member.getErrors().add(getMessage("manage_member.email_not_found"));
                    orcid = null;
                }
            }
        }

        if (PojoUtil.isEmpty(orcid)) {
            member.getErrors().add(getMessage("manage_member.email_not_found"));
        } else {
            if (profileEntityManager.orcidExists(orcid)) {
                MemberType groupType = profileEntityManager.getGroupType(orcid);
                if (groupType != null) {
                    ProfileEntity memberProfile = profileDao.find(orcid);
                    member = Member.fromProfileEntity(memberProfile);
                    Set<Client> clients = clientManagerReadOnly.getClients(orcid);
                    List<org.orcid.pojo.ajaxForm.Client> clientsList = new ArrayList<org.orcid.pojo.ajaxForm.Client>();
                    clients.forEach(c -> {
                        clientsList.add(org.orcid.pojo.ajaxForm.Client.fromModelObject(c));
                    });
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

    private String getMessage(String message) {
        return localeManager.resolveMessage(message);
    }

    /**
     * Since the groups have changed, the cache version must be updated on
     * database and all caches have to be evicted.
     */
    @Override
    public void clearCache() {
        // Updates cache database version
        thirdPartyLinkManager.updateDatabaseCacheVersion();
        // Evict current cache
        thirdPartyLinkManager.evictAll();
    }    
    
    /**
     * Returns the client type based on the member type
     * 
     * @param memberType
     * @return the client type associated with the given member type
     */
    private ClientType getClientType(MemberType memberType) {
        ClientType clientType = null;
        switch (memberType) {
        case BASIC:
            clientType = ClientType.UPDATER;
            break;
        case PREMIUM:
            clientType = ClientType.PREMIUM_UPDATER;
            break;
        case BASIC_INSTITUTION:
            clientType = ClientType.CREATOR;
            break;
        case PREMIUM_INSTITUTION:
            clientType = ClientType.PREMIUM_CREATOR;
            break;
        }
        return clientType;
    }
    
    private void updateClientTypeDueMemberTypeUpdate(String memberId, MemberType memberType) {
        List<ClientDetailsEntity> clients = clientDetailsDao.findByGroupId(memberId);
        ClientType clientType = this.getClientType(memberType);
        Date now = new Date();
        for (ClientDetailsEntity client : clients) {
            Set<String> newSetOfScopes = ClientType.getScopes(clientType);
            Set<ClientScopeEntity> existingScopes = client.getClientScopes();
            Iterator<ClientScopeEntity> scopesIterator = existingScopes.iterator();
            while (scopesIterator.hasNext()) {
                ClientScopeEntity clientScopeEntity = scopesIterator.next();
                if (newSetOfScopes.contains(clientScopeEntity.getScopeType())) {
                    newSetOfScopes.remove(clientScopeEntity.getScopeType());
                } else {
                    clientScopeDao.deleteScope(client.getClientId(), clientScopeEntity.getScopeType());
                }
            }

            // Insert the new scopes
            for (String newScope : newSetOfScopes) {
                ClientScopeEntity clientScopeEntity = new ClientScopeEntity();
                clientScopeEntity.setClientDetailsEntity(client);
                clientScopeEntity.setScopeType(newScope);
                clientScopeEntity.setDateCreated(now);
                clientScopeEntity.setLastModified(now);
                clientScopeDao.persist(clientScopeEntity);
            }

            // Update client type
            clientDetailsDao.updateClientType(clientType.name(), client.getClientId());            
        }
    }
}
