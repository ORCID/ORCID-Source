package org.orcid.core.userDetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

public class MultiSecretClient extends User {
    private List<Secret> secrets;

    public MultiSecretClient(String username, String password, Collection<? extends GrantedAuthority> authorities, List<Secret> secrets) {
        super(username, password, authorities);
        this.secrets = secrets;
    }

    public List<Secret> getSecrets() {
        return secrets;
    }

    public static class Secret {
        private final String encryptedSecret;
        private final boolean primary;

        public Secret(String encryptedSecret, boolean primary) {
            this.encryptedSecret = encryptedSecret;
            this.primary = primary;
        }

        public String getEncryptedSecret() {
            return encryptedSecret;
        }

        public boolean isPrimary() {
            return primary;
        }
    }
}
