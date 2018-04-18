package org.orcid.core.cli;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class MigrateAddressData {
    private static Logger LOG = LoggerFactory.getLogger(MigrateAddressData.class);
    private AddressDao addressDao;
    private TransactionTemplate transactionTemplate;    

    public static void main(String... args) {
        new MigrateAddressData().migrate();
    }

    private void migrate() {
        init();
        migrateAddress();
        System.exit(0);
    }

    private void migrateAddress() {
        LOG.debug("Starting migration process");
        List<Object[]> addressElements = Collections.emptyList();
        
        do {
            addressElements = addressDao.findAddressesToMigrate();
            for(final Object[] addressElement : addressElements) {
                String orcid = (String) addressElement[0];
                String countryCode = (String) addressElement[1];
                String visibilityValue = (String) addressElement[2];                                
                LOG.info("Migrating address for profile: {}", orcid);
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        Visibility visibility = null;                        
                        try {
                            visibility = Visibility.fromValue(visibilityValue);
                        } catch(Exception e) {
                            visibility = Visibility.fromValue(OrcidVisibilityDefaults.COUNTRY_DEFAULT.getVisibility().value());
                        }
                        AddressEntity address = new AddressEntity();
                        address.setDateCreated(new Date());
                        address.setLastModified(new Date());                        
                        address.setUser(new ProfileEntity(orcid));
                        address.setIso2Country(countryCode);
                        address.setSourceId(orcid);
                        address.setVisibility(visibility.name());
                        addressDao.persist(address);
                    }
                });
            }
        } while (addressElements != null && !addressElements.isEmpty());
        
        LOG.debug("Finished migration process");
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-persistence-context.xml");
        addressDao = (AddressDao) context.getBean("addressDao");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }
}
