package org.orcid.scheduler.loader.source.cli;


import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.scheduler.loader.manager.OrgLoadManager;
import org.orcid.scheduler.loader.source.OrgLoadSource;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LoadDataForOrganizationSource {
    private static final Logger LOG = LoggerFactory.getLogger(LoadDataForOrganizationSource.class);
    private  OrgLoadManager orgLoadManager;
    

    private OrgLoadSource rorOrgSource;
    private OrgLoadSource fundrefOrgSource;

    private static final String ROR_TYPE="ROR";
    private static final String FUNDREF_TYPE="FUNDREF";
    private static final String RINGGOLD_TYPE="RINGGOLD";
    
    
    
    @Option(name = "-o", usage = "The organization type. Can be one of the following ROR, FUNDREF, RINGGOLD")
    private String orgType;
    
    /**
     * Setup our spring resources
     * 
     */
    @SuppressWarnings({ "resource" })
    @PostConstruct
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-scheduler-context.xml");
        orgLoadManager = (OrgLoadManager) context.getBean("orgLoadManager");
        rorOrgSource = (OrgLoadSource) context.getBean("rorOrgDataSource");
        fundrefOrgSource = (OrgLoadSource) context.getBean("fundrefOrgDataSource");
    }

    public static void main(String[] args) {
        LoadDataForOrganizationSource loadData  = new LoadDataForOrganizationSource();
        CmdLineParser parser = new CmdLineParser(loadData);
        // TODO Auto-generated method stub
        try {
            parser.parseArgument(args);
            loadData.validateParameters(parser);
            loadData.init();
            if(StringUtils.equalsIgnoreCase(loadData.orgType, FUNDREF_TYPE)) {
                LOG.info("Loading orgs from Fundref");
                loadData.orgLoadManager.loadOrg(loadData.fundrefOrgSource);
            } else { //default to ROR
                LOG.info("Loading orgs from ROR");
                loadData.orgLoadManager.loadOrg(loadData.rorOrgSource);
            }
  
        } catch (Exception e) {
            LOG.error("Exception when importing data for org type: " + loadData.orgType , e);
            System.err.println(e.getMessage());
        } finally {
            System.exit(0);
        }

    }
    
    public void validateParameters(CmdLineParser parser) throws CmdLineException {

        if (PojoUtil.isEmpty(orgType)) {
            throw new CmdLineException(parser, "-o parameter must not be null.");
            
        } else if(!orgType.toUpperCase().equals(ROR_TYPE) && !orgType.toUpperCase().equals(FUNDREF_TYPE)&& !orgType.toUpperCase().equals(RINGGOLD_TYPE)){
            throw new CmdLineException(parser, "-o parameter must be one of the following.");
        }
    }

}