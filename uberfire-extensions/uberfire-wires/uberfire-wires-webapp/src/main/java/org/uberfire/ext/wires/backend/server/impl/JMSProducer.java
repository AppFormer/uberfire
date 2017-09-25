package org.uberfire.ext.wires.backend.server.impl;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

@ApplicationScoped
public class JMSProducer {

    @Resource(lookup = "ConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(mappedName = "topic/APPFORMER.CLUSTER.MESSAGE")
    private Topic message;

    @Inject
    JMSContext context;

    public void sendMessage(String txt) {
        try {
//            Connection connection = connectionFactory.createConnection();
//            Session session = connection.createSession(false,
//                                                       Session.AUTO_ACKNOWLEDGE);
//
//            MessageProducer producer = session.createProducer(message);
//            TextMessage message = session.createTextMessage();
//            message.setText(txt);
//
//            producer.send(message);
            context.createProducer().send(message,txt );
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
