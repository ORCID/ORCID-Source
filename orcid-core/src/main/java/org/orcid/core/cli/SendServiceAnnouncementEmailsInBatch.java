package org.orcid.core.cli;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.EmailMessageSender;
import org.orcid.core.profileEvent.EmailFrequencyServiceAnnouncement2018;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SendServiceAnnouncementEmailsInBatch {
    
    private static Logger LOG = LoggerFactory.getLogger(SendServiceAnnouncementEmailsInBatch.class);
    
    @Option(name = "-bs", usage = "Batch size: how many service announcements we want to send this time?")
    private Integer batchSize;
    
    EmailMessageSender emailMessageSender;
    
    public static void main(String... args) {
        SendServiceAnnouncementEmailsInBatch x = new SendServiceAnnouncementEmailsInBatch();

        CmdLineParser parser = new CmdLineParser(x);
        if (args == null) {
            parser.printUsage(System.err);
        }
        try {
            parser.parseArgument(args);
            x.init();
            x.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        }
        System.exit(0);
    }
    
    private void init() {
        // Lets use a top of 1.5M at a time
        if(this.batchSize == null || this.batchSize < 1) {
            this.batchSize = 10;
        } else if(this.batchSize > 1500000) {
            this.batchSize = 1500000;
        }
                     
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        emailMessageSender = (EmailMessageSender) context.getBean("emailMessageSender");     
    }
    
    private void execute() {
        long startTime = System.currentTimeMillis();
        emailMessageSender.sendServiceAnnouncements(batchSize);
        long endTime = System.currentTimeMillis();
        String timeTaken = DurationFormatUtils.formatDurationHMS(endTime - startTime);
        LOG.info("TimeTaken={} (H:m:s.S)", timeTaken);
    }
}
