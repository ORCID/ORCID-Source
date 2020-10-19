package org.orcid.core.cli;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.orgs.extId.normalizer.impl.ISNIOrgDisambiguatedExternalIdNormalizer;
import org.orcid.persistence.dao.OrgDisambiguatedExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class NormalizeISNIs {

    private static final Logger LOGGER = LoggerFactory.getLogger(NormalizeISNIs.class);

    private static final int BATCH_SIZE = 200;

    @Resource
    private OrgDisambiguatedExternalIdentifierDao orgDisambiguatedExternalIdentifierDao;

    @Resource
    private ISNIOrgDisambiguatedExternalIdNormalizer normalizer;

    public static void main(String[] args) {
        NormalizeISNIs normalizeISNIs = new NormalizeISNIs();
        normalizeISNIs.init();
        normalizeISNIs.normalizeISNIs();
        System.exit(0);
    }

    void normalizeISNIs() {
        List<OrgDisambiguatedExternalIdentifierEntity> isnis = orgDisambiguatedExternalIdentifierDao.findISNIsOfIncorrectLength(BATCH_SIZE);
        while (isnis != null && !isnis.isEmpty()) {
            LOGGER.info("Normalizing {} ISNIs", isnis.size());
            isnis.forEach(e -> {
                e.setIdentifier(normalizer.normalize(e.getIdentifier()));
                orgDisambiguatedExternalIdentifierDao.merge(e);
            });
            isnis = orgDisambiguatedExternalIdentifierDao.findISNIsOfIncorrectLength(BATCH_SIZE);
        }
        LOGGER.info("ISNI normalization complete");
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orgDisambiguatedExternalIdentifierDao = (OrgDisambiguatedExternalIdentifierDao) context.getBean("orgDisambiguatedExternalIdentifierDao");
        normalizer = (ISNIOrgDisambiguatedExternalIdNormalizer) context.getBean(ISNIOrgDisambiguatedExternalIdNormalizer.class);
    }

}
