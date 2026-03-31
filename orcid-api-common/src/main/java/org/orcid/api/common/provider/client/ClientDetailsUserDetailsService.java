package org.orcid.api.common.provider.client;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.v3.read_only.ClientDetailsManagerReadOnly;
import org.orcid.core.userDetails.MultiSecretClient;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientDetailsUserDetailsService implements UserDetailsService {

    @Resource(name = "clientDetailsManagerReadOnlyV3")
    private ClientDetailsManagerReadOnly clientDetailsManagerReadOnly;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(clientDetailsManagerReadOnly.exists(username)) {
            ClientDetailsEntity client = clientDetailsManagerReadOnly.findByClientId(username);
            Set<ClientSecretEntity> secrets = client.getClientSecrets();
            List<MultiSecretClient.Secret> secretsList = secrets.stream().map(s -> new MultiSecretClient.Secret(s.getClientSecret(), s.isPrimary())).collect(Collectors.toList());
            return new MultiSecretClient(client.getClientId(), StringUtils.EMPTY, client.getAuthorities(), secretsList);
        } else {
            throw new UsernameNotFoundException("Client with id " + username + " not found");
        }
    }
}
