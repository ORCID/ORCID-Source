package org.orcid.core.cli;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Camelia Dumitru
 * 
 */
public class SendWelcomeEmails {
    private static Logger LOG = LoggerFactory.getLogger(SendWelcomeEmails.class);
    private ProfileDao profileDao;
    private EmailDao emailDao;
    
    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Option(name = "-sd", usage = "Start Date")
    private String startDate;
    
    @Option(name = "-ed", usage = "End Date")
    private String endDate;

    public static void main(String... args) {
        SendWelcomeEmails element = new SendWelcomeEmails();
        CmdLineParser parser = new CmdLineParser(element);
        try {
            parser.parseArgument(args);
            // If it is null sets the default
            if (element.startDate == null) {
                //element.startDate = "2020-07-13 10:47:00";
                element.startDate = "2020-03-18 17:14:45";
            }
            
            if (element.endDate == null) {
                //element.endDate = "2020-07-13 15:56:00";
                element.endDate = "2020-03-18 17:14:46";
            }
            
            element.init();
            element.process(element.startDate, element.endDate);
            
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
            System.exit(1);
        } 
    }

    private void process(String startDate, String endDate) throws ParseException {
        
        long startTime = System.currentTimeMillis();
        LOG.debug("Starting sending welcome emails process: " + startTime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ProfileEntity> profiles = profileDao.registeredBetween(formatter.parse(startDate), formatter.parse(endDate));
        if(profiles != null) {
            for(ProfileEntity profile: profiles) {
                try {
                    notificationManager.sendWelcomeEmail(profile.getId(), emailDao.findNewestPrimaryEmail(profile.getId()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        long endTime = System.currentTimeMillis();
        String timeTaken = DurationFormatUtils.formatDurationHMS(endTime - startTime);
        LOG.debug("Finished sending welcome emails process: " + endTime + " time taken: " + timeTaken);
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        profileDao = (ProfileDao) context.getBean("profileDao");
        emailDao = (EmailDao) context.getBean("emailDao");
    }
}
