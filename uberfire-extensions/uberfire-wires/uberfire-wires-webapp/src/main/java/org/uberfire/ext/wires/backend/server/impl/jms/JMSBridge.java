package org.uberfire.ext.wires.backend.server.impl.jms;

import java.util.Hashtable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;

import org.apache.activemq.artemis.jms.bridge.QualityOfServiceMode;
import org.apache.activemq.artemis.jms.bridge.impl.JMSBridgeImpl;
import org.apache.activemq.artemis.jms.bridge.impl.JNDIConnectionFactoryFactory;
import org.apache.activemq.artemis.jms.bridge.impl.JNDIDestinationFactory;

@ApplicationScoped
public class JMSBridge {

    String sourceServer = "tcp://localhost:61616";
    String targetServer = "tcp://localhost:61616";

    InitialContext sourceContext;
    InitialContext targetContext;

    Hashtable<String, String> sourceJndiParams = createJndiParams(sourceServer);
    Hashtable<String, String> targetJndiParams = createJndiParams(targetServer);
    private org.apache.activemq.artemis.jms.bridge.JMSBridge jmsBridge;
    private Connection sourceConnection;
    private Connection targetConnection;
    private MessageProducer sourceProducer;
    private Session sourceSession;
    private Topic sourceTopic;

    @PostConstruct
    public void bla() {
        try {
            sourceContext = createContext(sourceServer);
            targetContext = createContext(targetServer);
            jmsBridge = new JMSBridgeImpl(new JNDIConnectionFactoryFactory(sourceJndiParams,
                                                                           "ConnectionFactory"),
                                          new JNDIConnectionFactoryFactory(targetJndiParams,
                                                                                                                            "ConnectionFactory"),
                                          new JNDIDestinationFactory(sourceJndiParams,
                                                                     "source/topic"),
                                          new JNDIDestinationFactory(targetJndiParams,
                                                                                                                      "target/queue"),
                                          "admin",
                                          "admin",
                                          "admin",
                                          "admin",
                                          null,
                                          5000,
                                          10,
                                          QualityOfServiceMode.DUPLICATES_OK,
                                          1,
                                          -1,
                                          null,
                                          null,
                                          true);

            sourceConnection = null;
            targetConnection = null;
            jmsBridge.start();
            // Step 3. Lookup the *source* JMS resources
            ConnectionFactory sourceConnectionFactory = (ConnectionFactory) sourceContext.lookup("ConnectionFactory");
            sourceTopic = (Topic) sourceContext.lookup("source/topic");

            // Step 4. Create a connection, a session and a message producer for the *source* topic
            sourceConnection = sourceConnectionFactory.createConnection("admin", "admin");
            sourceSession = sourceConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            sourceProducer = sourceSession.createProducer(sourceTopic);

            MessageConsumer consumer = sourceSession.createConsumer(sourceTopic);

            consumer.setMessageListener(new MessageListener() {

                @Override
                public void onMessage(Message message) {
                    System.out.println("1234");
                    try {
                        System.out.println(((TextMessage) message).getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
            sourceConnection.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void bla1(){
        // Step 6. Close the *source* connection
        try {
            sourceConnection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {

        TextMessage message = null;
        try {
            MessageConsumer consumer = sourceSession.createConsumer(sourceTopic);
            consumer.setMessageListener(new MessageListener() {

                @Override
                public void onMessage(Message message) {
                    System.out.println("12345");
                }
            });
            message = sourceSession.createTextMessage("this is a text message sent at " + System.currentTimeMillis() + msg);
            sourceProducer.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private Hashtable<String, String> createJndiParams(String server) {
        Hashtable<String, String> jndiProps = new Hashtable<>();
        jndiProps.put("connectionFactory.ConnectionFactory",
                      server);
        jndiProps.put("java.naming.factory.initial",
                      "org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory");
        jndiProps.put("queue.target/queue",
                      "target");
        jndiProps.put("topic.source/topic",
                      "topic");
        return jndiProps;
    }

    private InitialContext createContext(final String server) throws Exception {
        Hashtable<String, String> jndiProps = createJndiParams(server);
        return new InitialContext(jndiProps);
    }
}
