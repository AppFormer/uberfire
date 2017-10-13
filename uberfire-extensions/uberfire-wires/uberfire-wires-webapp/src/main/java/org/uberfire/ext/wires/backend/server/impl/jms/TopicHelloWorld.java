package org.uberfire.ext.wires.backend.server.impl.jms;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TransactionManagement(TransactionManagementType.BEAN)
@MessageDriven(name = "TopicHelloWorld", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationJndiName", propertyValue = "topic/TopicHelloWorld"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "topic/TopicHelloWorld"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")})
public class TopicHelloWorld implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicHelloWorld.class);

    @PostConstruct
    public void init() {

        System.out.println("----?----");
    }
    @Override
    public void onMessage(Message rcvMessage) {
        TextMessage msg = null;
        try {
            if (rcvMessage instanceof TextMessage) {
                msg = (TextMessage) rcvMessage;
                LOGGER.info("Received Message from topic: " + msg.getText());
            } else {
                LOGGER.debug("Message of wrong type: " + rcvMessage.getClass().getName());
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
