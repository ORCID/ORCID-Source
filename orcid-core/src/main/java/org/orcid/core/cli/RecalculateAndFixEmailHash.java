package org.orcid.core.cli;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.pojo.ajaxForm.PojoUtil;
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
public class RecalculateAndFixEmailHash {
    private static Logger LOG = LoggerFactory.getLogger(RecalculateAndFixEmailHash.class);
    private EmailDao emailDao;
    private EncryptionManager encryptionManager;
    private TransactionTemplate transactionTemplate;

    @Option(name = "-o", usage = "Offset")
    private Integer customOffset;
    
    @Option(name = "-b", usage = "Batch size")
    private Integer batchSize;

    @Option(name = "-f", usage = "Should this fix the problems?")
    private Boolean fixErrors;
    
    
    
    public static void main(String... args) throws NoSuchAlgorithmException {
        RecalculateAndFixEmailHash element = new RecalculateAndFixEmailHash();
        CmdLineParser parser = new CmdLineParser(element);
        try {
            parser.parseArgument(args);
            if(element.customOffset == null) {
                element.customOffset = 0;
            }
            
            // If it is null or too big
            if (element.batchSize == null || element.batchSize > 1000000) {
                element.batchSize = 50000;
            }
            
            if(element.fixErrors == null) {
                element.fixErrors = false;
            }
        } catch (CmdLineException e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
            System.exit(1);
        }
        element.migrate();
    }

    private void migrate() throws NoSuchAlgorithmException {
        init();
        process();
        System.exit(0);
    }

    private void process() throws NoSuchAlgorithmException {
        LOG.info("Looking at the emails");
        int iteration = 0;
        if(customOffset > 0) {
            iteration = customOffset;
        }
        List emails = Collections.emptyList();
        do {
            LOG.info("Iteration: " + iteration);
            emails = emailDao.getEmailAndHash(iteration, batchSize);
            iteration++;
            for(Iterator it = emails.iterator(); it.hasNext(); ) {
                Object[] element = (Object[]) it.next();
                String orcid = (String) element[0];
                String email = (String) element[1];
                String hash = (String) element[2];
                
                if(!PojoUtil.isEmpty(email)) {
                    String correctedEmailHash = encryptionManager.getEmailHash(email);
                    
                    if(!correctedEmailHash.equals(hash)) {
                        LOG.info(orcid + " - invalid '" + hash + "' valid '" + correctedEmailHash + "'");
                        if(fixErrors != null && fixErrors == true) {
                            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                                @Override
                                protected void doInTransactionWithoutResult(TransactionStatus status) {                                    
                                    emailDao.populateEmailHash(email, correctedEmailHash);
                                    LOG.info("Fixed: " + orcid + " " + correctedEmailHash);
                                }
                            });
                        }
                    }
                }                
            }
        } while (emails != null && !emails.isEmpty());
        LOG.info("Finished");
    }    
    
    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        emailDao = (EmailDao) context.getBean("emailDao");
        encryptionManager = (EncryptionManager) context.getBean("encryptionManager");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }
}
