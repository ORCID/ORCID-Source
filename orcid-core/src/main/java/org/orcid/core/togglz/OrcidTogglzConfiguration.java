package org.orcid.core.togglz;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.togglz.core.Feature;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.cache.CachingStateRepository;
import org.togglz.core.repository.jdbc.JDBCStateRepository;
import org.togglz.core.user.FeatureUser;
import org.togglz.core.user.SimpleFeatureUser;
import org.togglz.core.user.UserProvider;

@Component
public class OrcidTogglzConfiguration implements TogglzConfig {

    @Resource(name = "featuresDataSource")
    private DataSource dataSource;

    @Value("${org.orcid.persistence.togglz.cache.ttl:60000}")
    private Long cacheTTL;
    
    private StateRepository dbRepo;
    
    private Object lock = new Object();
    
    @Override
    public Class<? extends Feature> getFeatureClass() {
        return Features.class;
    }

    @Override
    public StateRepository getStateRepository() {
        if (dbRepo == null){
            synchronized(lock){
                if (dbRepo == null){
                    if (cacheTTL == 0l){
                        dbRepo = new JDBCStateRepository(dataSource);  
                    }else{
                        dbRepo = new CachingStateRepository(new JDBCStateRepository(dataSource),cacheTTL);                        
                    }
                }
            }
        }
        return dbRepo;
    }

    @Override
    public UserProvider getUserProvider() {
        return new UserProvider() {
            @Override
            public FeatureUser getCurrentUser() {
                boolean isAdmin = false;
                String userOrcid = null;
                SecurityContext context = SecurityContextHolder.getContext();
                if (context != null && context.getAuthentication() != null) {
                    Authentication authentication = context.getAuthentication();
                    if (authentication != null) {
                        Object principal = authentication.getDetails();
                        if (principal instanceof OrcidProfileUserDetails) {
                            OrcidProfileUserDetails userDetails = (OrcidProfileUserDetails) principal;
                            isAdmin = userDetails.getAuthorities().contains(OrcidWebRole.ROLE_ADMIN);
                            userOrcid = userDetails.getOrcid();
                        }
                    }
                }
                return new SimpleFeatureUser(userOrcid, isAdmin);
            }
        };
    }

}
