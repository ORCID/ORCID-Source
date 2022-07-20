package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.ehcache.Cache;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.persistence.dao.IdentityProviderDao;
import org.orcid.persistence.jpa.entities.IdentityProviderEntity;
import org.orcid.persistence.jpa.entities.IdentityProviderNameEntity;
import org.orcid.utils.ReleaseNameUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * 
 * @author Will Simpson
 *
 */
public class IdentityProviderManagerImpl implements IdentityProviderManager {

    @Value("${org.orcid.core.idpMetadataUrlsSpaceSeparated:http://www.testshib.org/metadata/testshib-providers.xml https://engine.surfconext.nl/authentication/idp/metadata}")
    private String metadataUrlsString;

    @Resource
    private IdentityProviderDao identityProviderDao;

    @Resource
    private LocaleManager localeManager;

    @Resource(name = "identityProviderNameCache")
    private Cache<IdentityProviderNameCacheKey, String> identityProviderNameCache;

    private String releaseName = ReleaseNameUtils.getReleaseName();

    @Override
    public String retrieveIdentitifyProviderName(String providerid) {
        return retrieveIdentitifyProviderName(providerid, localeManager.getLocale());
    }

    @Override
    public String retrieveIdentitifyProviderName(String providerid, Locale locale) {
        return identityProviderNameCache.get(new IdentityProviderNameCacheKey(providerid, locale, releaseName));
    }

    @Override
    @Transactional
    public String retrieveFreshIdentitifyProviderName(String providerid, Locale locale) {
        IdentityProviderEntity idp = identityProviderDao.findByProviderid(providerid);
        List<IdentityProviderNameEntity> names = idp.getNames();
        if (names != null) {
            Optional<IdentityProviderNameEntity> idpNameEntity = names.stream().filter(n -> n.getLang().equals(locale.getLanguage())).findFirst();
            if (idpNameEntity.isPresent()) {
                return idpNameEntity.get().getDisplayName();
            }
        }
        return idp.getDisplayName();
    }

    @Override
    public String retrieveContactEmailByProviderid(String providerid) {
        IdentityProviderEntity idp = identityProviderDao.findByProviderid(providerid);
        if (idp == null) {
            return null;
        }
        String supportEmail = idp.getSupportEmail();
        if (supportEmail != null) {
            return supportEmail;
        }
        List<String> otherEmails = new ArrayList<>(2);
        otherEmails.add(idp.getAdminEmail());
        otherEmails.add(idp.getTechEmail());
        return String.join(";", otherEmails.stream().filter(e -> e != null).collect(Collectors.toList()));
    }   

    @Override
    public void incrementFailedCount(String shibIdentityProvider) {
        identityProviderDao.incrementFailedCount(shibIdentityProvider);
    }

}
