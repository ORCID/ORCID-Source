package org.orcid.core.cron;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.orcid.persistence.dao.ClientSecretDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;

public class CleanOldKeysTest {
    
    @Resource
    private ClientSecretDao clientSecretDao;
    
    @Test
    public void testWhatever() {
        List<ClientSecretEntity> listo = clientSecretDao.getNonPrimaryKeys();
        for (ClientSecretEntity e : listo) {
            ClientDetailsEntity zhep = e.getClientDetailsEntity();
            System.out.println(zhep.getClientName());
        };
        
    }

}
