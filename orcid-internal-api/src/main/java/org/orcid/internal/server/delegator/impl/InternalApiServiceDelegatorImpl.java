package org.orcid.internal.server.delegator.impl;

import static org.orcid.core.api.OrcidApiConstants.STATUS_OK_MESSAGE;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.persistence.NoResultException;
import jakarta.ws.rs.core.Response;

import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.LockedException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.MembersManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.cache.redis.PasswordResetTokenEntry;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.internal.server.delegator.InternalApiServiceDelegator;
import org.orcid.internal.util.AccountRecoveryMatchRequest;
import org.orcid.internal.util.AccountRecoveryMatchResponse;
import org.orcid.internal.util.AccountRecoveryMatchResponse.RecordStatus;
import org.orcid.internal.util.AccountRecoveryResetLinkRequest;
import org.orcid.internal.util.AccountRecoveryResetLinkResponse;
import org.orcid.internal.util.EmailResponse;
import org.orcid.internal.util.LastModifiedResponse;
import org.orcid.internal.util.MemberInfo;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.ajaxForm.Member;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.ExpiringLinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@Component
public class InternalApiServiceDelegatorImpl implements InternalApiServiceDelegator {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternalApiServiceDelegatorImpl.class);

    @Resource(name = "membersManagerV3")
    private MembersManager memberManager;
    
    @Resource(name = "profileEntityManagerReadOnlyV3")
    private ProfileEntityManagerReadOnly profileEntityManagerReadOnly;

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;

    @Resource(name = "orcidSecurityManagerV3")
    protected OrcidSecurityManager orcidSecurityManager;

    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;

    @Resource
    private ExpiringLinkService expiringLinkService;

    @Resource
    private RedisClient redisClient;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    /** Set from the Spring context; shares the expiry configured for admin generated reset links. */
    private long resetLinkExpirationInMinutes;

    public void setResetLinkExpirationInMinutes(long resetLinkExpirationInMinutes) {
        this.resetLinkExpirationInMinutes = resetLinkExpirationInMinutes;
    }

    @Override
    public Response viewStatusText() {
        
        return Response.ok(STATUS_OK_MESSAGE).build();
    }

    @Override
    public Response viewPersonLastModified(String orcid) {
        orcidSecurityManager.checkScopes(ScopePathType.INTERNAL_PERSON_LAST_MODIFIED);
        Date lastModified = profileEntityManagerReadOnly.getLastModifiedDate(orcid);
        LastModifiedResponse obj = new LastModifiedResponse(orcid, lastModified.toString());        
        Response response = Response.ok(obj).build(); 
        return response;
    }
    
    @Override
    public Response viewMemberInfo(String memberIdOrName){
        Member member = memberManager.getMember(memberIdOrName); 
        if(member == null || (member.getErrors() != null && !member.getErrors().isEmpty())) {
            OrcidError orcidError = new OrcidError();
            orcidError.setResponseCode(404);
            orcidError.setErrorCode(0);
            orcidError.setMoreInfo("Unable to find member info for: " + memberIdOrName);
            orcidError.setDeveloperMessage("Member id or name not found for: " + memberIdOrName);
            orcidError.setUserMessage("Unable to find member info for: " + memberIdOrName);
            return Response.status(Response.Status.NOT_FOUND).entity(orcidError).build();
        }
        MemberInfo memberInfo = MemberInfo.fromMember(member);
        return Response.ok(memberInfo).build();        
    }

    @Override
    public Response viewTogglz() {
        Map<Features, Boolean> featuresMap = new HashMap<>();
        for(Features feature : Features.values()) {
            featuresMap.put(feature, feature.isActive());
        }
        return Response.ok(featuresMap).build();
    }

    @Override
    public Response findOrcidByEmail(String email) {
        orcidSecurityManager.checkScopes(ScopePathType.INTERNAL);
        if (email != null && !email.isEmpty()) {
            if (emailManagerReadOnly.emailExists(email)) {
                String orcid = null;

                try {
                    orcid = emailManagerReadOnly.findOrcidByVerifiedEmail(email);
                } catch (NoResultException e) {
                    return Response.ok(new EmailResponse("", email, HttpStatus.NOT_FOUND)).build();
                }

                try {
                    orcidSecurityManager.checkProfile(orcid);
                } catch (LockedException | DeactivatedException | OrcidNotClaimedException | OrcidDeprecatedException e) {
                    return Response.ok(new EmailResponse("", email, HttpStatus.NOT_FOUND)).build();
                }

                return Response.ok(new EmailResponse(orcid, email, HttpStatus.FOUND)).build();
            } else {
                return Response.ok(new EmailResponse("", email, HttpStatus.NOT_FOUND)).build();
            }
        } else {
            return Response.noContent().build();
        }
    }

    @Override
    public Response accountRecoveryMatch(AccountRecoveryMatchRequest request) {
        orcidSecurityManager.checkScopes(ScopePathType.INTERNAL_ACCOUNT_RECOVERY);

        if (request == null || PojoUtil.isEmpty(request.getOrcid()) || PojoUtil.isEmpty(request.getEmail())) {
            return Response.noContent().build();
        }

        // Deliberately unfiltered: recovery requests routinely come from records the verified only
        // lookups refuse to resolve. The pair check below is what protects the record.
        String orcidForEmail;
        try {
            orcidForEmail = emailManagerReadOnly.findOrcidIdByEmail(request.getEmail());
        } catch (NoResultException e) {
            return Response.ok(AccountRecoveryMatchResponse.noMatch()).build();
        }

        if (orcidForEmail == null || !orcidForEmail.equals(request.getOrcid())) {
            // One answer for every kind of non match, so the caller learns nothing about which part
            // was wrong, or whether the email is registered at all.
            return Response.ok(AccountRecoveryMatchResponse.noMatch()).build();
        }

        return Response.ok(AccountRecoveryMatchResponse.match(recordStatusOf(orcidForEmail))).build();
    }

    private RecordStatus recordStatusOf(String orcid) {
        try {
            orcidSecurityManager.checkProfile(orcid);
        } catch (OrcidDeprecatedException e) {
            return RecordStatus.DEPRECATED;
        } catch (OrcidNotClaimedException e) {
            return RecordStatus.UNCLAIMED;
        } catch (LockedException e) {
            return RecordStatus.LOCKED;
        } catch (DeactivatedException e) {
            return RecordStatus.DEACTIVATED;
        }
        return RecordStatus.ACTIVE;
    }

    @Override
    public Response accountRecoveryResetLink(AccountRecoveryResetLinkRequest request) {
        orcidSecurityManager.checkScopes(ScopePathType.INTERNAL_ACCOUNT_RECOVERY);

        if (request == null || PojoUtil.isEmpty(request.getOrcid())) {
            return Response.noContent().build();
        }

        String orcid = request.getOrcid();
        if (!profileEntityManager.orcidExists(orcid)) {
            OrcidError orcidError = new OrcidError();
            orcidError.setResponseCode(404);
            orcidError.setErrorCode(0);
            orcidError.setMoreInfo("Unable to find a record for: " + orcid);
            orcidError.setDeveloperMessage("No record found for: " + orcid);
            orcidError.setUserMessage("Unable to find a record for: " + orcid);
            return Response.status(Response.Status.NOT_FOUND).entity(orcidError).build();
        }

        String token;
        try {
            token = expiringLinkService.generateExpiringToken(orcid, resetLinkExpirationInMinutes, ExpiringLinkService.ExpiringLinkType.PASSWORD_RESET);
        } catch (JOSEException e) {
            LOGGER.error("Failed to generate an account recovery password reset token", e);
            return Response.serverError().build();
        }

        // Overwrites any link issued earlier for this record, so only the newest one can be redeemed.
        redisClient.set(PasswordResetTokenEntry.redisKey(orcid), new PasswordResetTokenEntry(token, false).serialize(),
                (int) (resetLinkExpirationInMinutes * 60));

        Date issueDate = new Date();
        Calendar expiry = Calendar.getInstance();
        expiry.setTime(issueDate);
        expiry.add(Calendar.MINUTE, (int) resetLinkExpirationInMinutes);

        // Recorded so a recovery can be traced back to the client that requested it. The link itself
        // is never logged.
        LOGGER.info("Account recovery reset link issued for {} by client {}", orcid, orcidSecurityManager.getClientIdFromAPIRequest());

        String resetLink = orcidUrlManager.getBaseUrl() + "/reset-password-email/" + token;
        return Response.ok(new AccountRecoveryResetLinkResponse(resetLink, issueDate, expiry.getTime())).build();
    }
}
