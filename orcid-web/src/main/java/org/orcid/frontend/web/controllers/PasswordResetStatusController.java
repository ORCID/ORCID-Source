package org.orcid.frontend.web.controllers;

import jakarta.annotation.Resource;
import jakarta.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.core.togglz.Features;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Tells the sign in forms, as soon as an email address or ORCID iD has been
 * entered, that the record behind it has to reset its password before it can
 * sign in with one (mandatory password reset, PD-5692).
 *
 * Anonymous by design: disclosing this state from the identifier alone is an
 * accepted product decision. The answer is identical for an unknown
 * identifier, a record that is not flagged, and the feature being off, so the
 * endpoint says nothing about whether an address or iD is registered.
 *
 * Reads go to the read only database and are never cached, because the flag
 * does not change the record's last modified date, which every profile cache
 * is keyed on. The sign in itself re-checks against the primary.
 */
@Controller
public class PasswordResetStatusController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetStatusController.class);

    /** Longer than any email address the registry accepts, so nothing real is refused */
    private static final int MAX_USERNAME_LENGTH = 320;

    @Resource(name = "profileEntityManagerReadOnlyV3")
    private ProfileEntityManagerReadOnly profileEntityManagerReadOnly;

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;

    @RequestMapping(value = "/signin/password-reset-status.json", method = RequestMethod.POST)
    public @ResponseBody PasswordResetStatus getPasswordResetStatus(@RequestBody PasswordResetStatusRequest request) {
        if (!Features.FORCE_PASSWORD_RESET.isActive() || request == null) {
            return new PasswordResetStatus(false);
        }
        String orcid = resolveOrcid(request.getUsername());
        if (orcid == null) {
            return new PasswordResetStatus(false);
        }
        boolean required = profileEntityManagerReadOnly.isPasswordResetRequired(orcid);
        if (required) {
            LOGGER.debug("Password reset required reported for {}", orcid);
        }
        return new PasswordResetStatus(required);
    }

    private String resolveOrcid(String username) {
        String value = StringUtils.trimToNull(username);
        if (value == null || value.length() > MAX_USERNAME_LENGTH) {
            return null;
        }
        if (OrcidStringUtils.isValidOrcid(value)) {
            return value;
        }
        if (!value.contains("@")) {
            return null;
        }
        try {
            return emailManagerReadOnly.findOrcidIdByEmail(value);
        } catch (NoResultException e) {
            return null;
        }
    }

    public static class PasswordResetStatusRequest {

        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    public static class PasswordResetStatus {

        private final boolean passwordResetRequired;

        public PasswordResetStatus(boolean passwordResetRequired) {
            this.passwordResetRequired = passwordResetRequired;
        }

        public boolean isPasswordResetRequired() {
            return passwordResetRequired;
        }
    }
}
