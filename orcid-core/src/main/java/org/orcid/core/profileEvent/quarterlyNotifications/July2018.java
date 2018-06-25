package org.orcid.core.profileEvent.quarterlyNotifications;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class July2018 extends QuarterlyNotificationsManager {
    private static final String NOTIFICATION_FAMILY = "JULY_2018";
    
    private NotificationManager notificationManager;
    
    private TemplateManager templateManager;
    
    @Option(name = "-mt", usage = "How many pararel threads should run")
    private Integer maxThreads;
    
    @Option(name = "-s", usage = "Chunk size")
    private Integer chunkSize;
    
    public July2018() {
        super(ProfileEventType.JULY_2018_CREATED, ProfileEventType.JULY_2018_SKIPPED, ProfileEventType.JULY_2018_FAIL, NOTIFICATION_FAMILY, true, 10);
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        notificationManager = (NotificationManager) context.getBean("notificationManager");
        templateManager = (TemplateManager) context.getBean("templateManager");
    }
    
    public String getSubject(Locale locale) {
        return messages.getMessage("email.july_2018.subject", new Object[0], locale);
    }
    
    public Map<String, Object> generateTemplateParams(ProfileEntity profileEntity, Locale locale) {
        Map<String, Object> templateParams = new HashMap<String, Object>();
        String emailName = notificationManager.deriveEmailFriendlyName(profileEntity);
        templateParams.put("emailName", emailName);   
        return templateParams;
    }
    
    public String generatePlainTextNotification(Map<String, Object> templateParams) {
        return templateManager.processTemplate("july_2018.ftl", templateParams);        
    }
    
    public String generateHtmlNotification(Map<String, Object> templateParams) {
        return templateManager.processTemplate("july_2018_html.ftl", templateParams);
    }
    
    public static void main(String... args) {
        July2018 efsa = new July2018();
        CmdLineParser parser = new CmdLineParser(efsa);
        if (args == null) {
            parser.printUsage(System.err);
        }
        try {
            parser.parseArgument(args);
            efsa.execute(efsa.chunkSize, efsa.maxThreads);
        } catch (CmdLineException | InterruptedException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        }
        System.exit(0);
    }
}
