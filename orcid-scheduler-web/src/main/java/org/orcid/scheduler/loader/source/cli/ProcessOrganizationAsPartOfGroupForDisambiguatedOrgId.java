package org.orcid.scheduler.loader.source.cli;


import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.core.orgs.grouping.OrgGrouping;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.scheduler.loader.manager.OrgLoadManager;
import org.orcid.scheduler.loader.source.OrgLoadSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ProcessOrganizationAsPartOfGroupForDisambiguatedOrgId {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessOrganizationAsPartOfGroupForDisambiguatedOrgId.class);
    private OrgDisambiguatedManager orgDisambiguatedManager;
    private OrgDisambiguatedDao orgDisambiguatedDao;
   

    private static final String ROR_TYPE="ROR";
    private static final String FUNDREF_TYPE="FUNDREF";
    private static final String RINGGOLD_TYPE="RINGGOLD";
    
    
    
    @Option(name = "-oid", usage = "The disambiguated org id.")
    private String orgID;
    
    /**
     * Setup our spring resources
     * 
     */
    @SuppressWarnings({ "resource" })
    @PostConstruct
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-scheduler-context.xml");
        orgDisambiguatedManager = (OrgDisambiguatedManager) context.getBean("orgDisambiguatedManager");
        orgDisambiguatedDao = (OrgDisambiguatedDao) context.getBean("orgDisambiguatedDao");
    }

    public static void main(String[] args) {
        ProcessOrganizationAsPartOfGroupForDisambiguatedOrgId loadData  = new ProcessOrganizationAsPartOfGroupForDisambiguatedOrgId();
        CmdLineParser parser = new CmdLineParser(loadData);
        // TODO Auto-generated method stub
        try {
            parser.parseArgument(args);
            loadData.validateParameters(parser);

            loadData.init();
            OrgDisambiguatedEntity existingBySourceId = loadData.orgDisambiguatedDao.find(Long.valueOf(loadData.orgID));
            new OrgGrouping(existingBySourceId, loadData.orgDisambiguatedManager).markGroupForIndexing(loadData.orgDisambiguatedDao);

  
        } catch (Exception e) {
            LOG.error("Exception when importing data for org type: " + loadData.orgID , e);
            System.err.println(e.getMessage());
        } finally {
            System.exit(0);
        }

    }
    
    public void validateParameters(CmdLineParser parser) throws CmdLineException {

        if (PojoUtil.isEmpty(orgID)) {
            throw new CmdLineException(parser, "-oid parameter must not be null."); 
        } 
    }

}
