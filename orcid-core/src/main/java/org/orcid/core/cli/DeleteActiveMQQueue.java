package org.orcid.core.cli;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DeleteActiveMQQueue {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteActiveMQQueue.class);

    @SuppressWarnings("resource")
    public static void main(String[] args) throws JMSException {
        if (args.length == 0) {
            throw new RuntimeException("Queue name arg required");
        }

        String queueName = args[0];
        if (queueName == null || queueName.isEmpty()) {
            throw new RuntimeException("Queue name arg required");
        }

        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        ConnectionFactory connectionFactory = (ConnectionFactory) context.getBean("jmsConnectionFactory");
        ActiveMQConnection connection = (ActiveMQConnection) connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        ActiveMQDestination queue = (ActiveMQDestination) session.createQueue(queueName);
        connection.destroyDestination(queue);
        boolean deleted = connection.isDeleted(queue);
        if (!deleted) {
            LOG.error("Failed to delete queue {}", queueName);
            System.exit(1);
        }
        
        LOG.info("Queue {} deleted successfully.", queueName);
        System.exit(0);
    }

}
