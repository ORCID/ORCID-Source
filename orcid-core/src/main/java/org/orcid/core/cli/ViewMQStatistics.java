package org.orcid.core.cli;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ViewMQStatistics {

    private static final Logger LOG = LoggerFactory.getLogger(ViewMQStatistics.class);

    @SuppressWarnings("rawtypes")
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
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        while (true) {
            Queue replyTo = session.createTemporaryQueue();
            MessageConsumer consumer = session.createConsumer(replyTo);
            Queue query = session.createQueue("ActiveMQ.Statistics.Destination." + queueName);
            MessageProducer producer = session.createProducer(query);
            Message msg = session.createMessage();
            msg.setJMSReplyTo(replyTo);
            producer.send(msg);

            MapMessage reply = (MapMessage) consumer.receive();
            StringBuilder builder = new StringBuilder("\nStats for queue ").append(queueName);
            for (Enumeration e = reply.getMapNames(); e.hasMoreElements();) {
                String name = e.nextElement().toString();
                builder.append("\n").append(name).append(" = ").append(reply.getObject(name));
            }
            LOG.info(builder.toString());
            try {
                Thread.sleep(5000l);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
