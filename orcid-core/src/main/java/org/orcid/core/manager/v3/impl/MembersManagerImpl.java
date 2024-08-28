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

import org.apache.commons.lang.StringUtils;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidGenerationManager;
import org.orcid.core.manager.v3.ClientManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.MembersManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.ClientManagerReadOnly;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.v3.release.client.Client;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ClientScopeDao;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidGrantedAuthority;
import org.orcid.persistence.jpa.entities.ProfileEntity;
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
    
    @Resource(name = "recordNameManagerV3")
    private RecordNameManager recordNameManager;
    
    @Resource
    private EmailDao emailDao;

    @Override
    public Member createMember(Member member) throws IllegalArgumentException {
        if (emailManager.emailExists(member.getEmail().getValue())) {
            throw new IllegalArgumentException("Email already exists");
        }

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
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
                newRecord.setEnableDeveloperTools(false);       
                newRecord.setEncryptedPassword(null);
                newRecord.setGroupType(MemberType.fromValue(member.getType().getValue()).name());
                newRecord.setLocale(org.orcid.jaxb.model.common_v2.Locale.EN.name());
                newRecord.setRecordLocked(false);
                newRecord.setReviewed(true);
                newRecord.setSalesforeId(PojoUtil.isEmpty(member.getSalesforceId()) ? null : member.getSalesforceId().getValue());
                newRecord.setSubmissionDate(now);
                newRecord.setUsedRecaptchaOnRegistration(false);
                
                // Set authority
                OrcidGrantedAuthority authority = new OrcidGrantedAuthority();
                authority.setOrcid(orcid);
                authority.setAuthority(OrcidWebRole.ROLE_GROUP.getAuthority());
                Set<OrcidGrantedAuthority> authorities = new HashSet<OrcidGrantedAuthority>(1);
                authorities.add(authority);
                newRecord.setAuthorities(authorities);

                profileDao.persist(newRecord);
                profileDao.flush();         
                
                // Set primary email
                EmailEntity emailEntity = new EmailEntity();
                
                Map<String, String> emailKeys = emailManager.getEmailKeys(member.getEmail().getValue());
                
                emailEntity.setEmail(emailKeys.get(EmailManager.FILTERED_EMAIL));
                emailEntity.setId(emailKeys.get(EmailManager.HASH));
                emailEntity.setOrcid(newRecord.getId());
                emailEntity.setPrimary(true);
                emailEntity.setCurrent(true);
                emailEntity.setVerified(true);
                // Email is private by default
                emailEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
                
                SourceEntity sourceEntity = sourceManager.retrieveActiveSourceEntity();
                String sourceId = sourceEntity.getSourceProfile().getId();
                emailEntity.setSourceId(sourceId);                
                emailDao.persist(emailEntity);
                
                // Set the name
                Name name = new Name();
                name.setCreditName(new CreditName(member.getGroupName().getValue()));
                name.setVisibility(org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC);
                recordNameManager.createRecordName(orcid, name);
                
                emailFrequencyManager.createOnRegister(orcid, SendEmailFrequency.WEEKLY, SendEmailFrequency.WEEKLY, SendEmailFrequency.WEEKLY, false);
                
                member.setGroupOrcid(Text.valueOf(orcid));
            }
        });
        
        return member;
    }

    @Override
    public Member updateMemeber(Member member) throws IllegalArgumentException {
        String memberId = member.getGroupOrcid().getValue();
        String name = member.getGroupName().getValue();
        String salesForceId = member.getSalesforceId() == null ? null : member.getSalesforceId().getValue();
        MemberType memberType = MemberType.fromValue(member.getType().getValue());
        Map<String, String> emailKeys = emailManager.getEmailKeys(member.getEmail().getValue());
        String newEmail = emailKeys.get(EmailManager.FILTERED_EMAIL);
        String hash = emailKeys.get(EmailManager.HASH);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                boolean memberChangedType = false;
                
                // Update member name
                Name recordName = recordNameManager.getRecordName(member.getGroupOrcid().getValue());
                if(recordName == null || recordName.getCreditName() == null || !StringUtils.equals(recordName.getCreditName().getContent(), name)) {
                    recordName.setCreditName(new CreditName(name));
                    recordNameManager.updateRecordName(member.getGroupOrcid().getValue(), recordName);
                }
                
                // Update member info
                ProfileEntity memberEntity = profileDao.find(member.getGroupOrcid().getValue());
                memberEntity.setIndexingStatus(IndexingStatus.PENDING);                
                memberEntity.setSalesforeId(salesForceId);

                if (!memberType.equals(memberEntity.getGroupType())) {
                    memberEntity.setGroupType(memberType.name());
                    memberChangedType = true;
                }
                profileDao.merge(memberEntity);
                
                // Update primary email if needed
                Email currentPrimaryEmail = emailManager.findPrimaryEmail(memberId);
                if (!newEmail.equals(currentPrimaryEmail.getEmail())) {
                    if (emailManager.emailExists(newEmail)) {
                        throw new IllegalArgumentException("Email already exists");
                    }
                    // Create new primary email 
                    EmailEntity newPrimaryEmail = new EmailEntity();
                    newPrimaryEmail.setOrcid(memberId);
                    newPrimaryEmail.setCurrent(true);
                    newPrimaryEmail.setEmail(newEmail);
                    newPrimaryEmail.setId(hash);
                    newPrimaryEmail.setPrimary(true);
                    newPrimaryEmail.setVerified(true);
                    newPrimaryEmail.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
                    emailDao.persist(newPrimaryEmail);
                    // Remove old primary email
                    emailDao.removeEmail(memberId, currentPrimaryEmail.getEmail());
                }                

                if (memberChangedType) {
                    updateClientTypeDueMemberTypeUpdate(memberId, memberType);
                }
            }
        });
                
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
                    Name recordName = recordNameManager.getRecordName(orcid);
                    member = new Member();
                    member.setGroupOrcid(Text.valueOf(orcid));
                    member.setSalesforceId(Text.valueOf(memberProfile.getSalesforeId()));
                    String creditName = (recordName == null || recordName.getCreditName() == null) ? null : recordName.getCreditName().getContent();
                    member.setGroupName(Text.valueOf(creditName));                                        
                    MemberType memberType = MemberType.valueOf(memberProfile.getGroupType());
                    member.setType(Text.valueOf(memberType.value()));
                    member.setEmail(Text.valueOf(emailManager.findPrimaryEmail(orcid).getEmail()));
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
                clientScopeDao.persist(clientScopeEntity);
            }

            // Update client type
            clientDetailsDao.updateClientType(clientType.name(), client.getClientId());            
        }
    }
}
