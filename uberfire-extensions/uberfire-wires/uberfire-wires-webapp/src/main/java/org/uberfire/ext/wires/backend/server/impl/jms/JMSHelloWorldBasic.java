package org.uberfire.ext.wires.backend.server.impl.jms;

import java.io.Serializable;
import java.util.Hashtable;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.fs.jgit.ws.jms.MessageWrapper;

public class JMSHelloWorldBasic {

    public static void main(String[] args) throws Exception {
        try {
            InitialContext context = createContext();

            ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
            Destination queue = (Destination) context.lookup("source/topic");

            Connection connection = factory.createConnection(System.getProperty("admin"),
                                                             System.getProperty("admin"));
            connection.setExceptionListener(new MyExceptionListener());
            connection.start();

            Session session = connection.createSession(false,
                                                       Session.AUTO_ACKNOWLEDGE);

            MessageProducer messageProducer = session.createProducer(queue);
            MessageConsumer messageConsumer = session.createConsumer(queue);

//            TextMessage message = session.createTextMessage("Hello world!");
            MessageWrapper m = new MessageWrapper("1",
                                                  null);
            ObjectMessage message = session.createObjectMessage(m);
            messageProducer.send(message,
                                 DeliveryMode.NON_PERSISTENT,
                                 Message.DEFAULT_PRIORITY,
                                 Message.DEFAULT_TIME_TO_LIVE);

            messageConsumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message receivedMessage) {

                    if (receivedMessage instanceof ObjectMessage) {

                        ObjectMessage receivedMessage1 = (ObjectMessage) receivedMessage;
                        Serializable object = null;
                        try {
                            object = receivedMessage1.getObject();
                            if (object instanceof WatchEvent) {
                                WatchEvent ws = (WatchEvent) object;
                                WatchContext context1 = (WatchContext) ws.context();

                                System.out.println(context1.getMessage());
                                if (context1.getPath() != null) {
                                    System.out.println("");
                                }
                                if (context1.getOldPath() != null) {
                                    System.out.println("");
                                }
                                System.out.println(context1.getPath() != null ? context1.getPath().getFileSystem().getName() : "");
                                System.out.println(context1.getOldPath() != null ? context1.getOldPath().getFileSystem().getName() : "");
                            }
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                        System.out.println(object);
                    } else {
                        System.out.println(receivedMessage);
                    }
                }
            });
            boolean a = true;

            while (a) {
                Thread.sleep(1000);
                System.out.println(".");
            }

            connection.close();
        } catch (Exception exp) {
            System.out.println("Caught exception, exiting.");
            exp.printStackTrace(System.out);
            System.exit(1);
        }
    }

    private static Hashtable<String, String> createJndiParams() {
        Hashtable<String, String> jndiProps = new Hashtable<>();
        jndiProps.put("connectionFactory.ConnectionFactory",
                      "tcp://localhost:61616");
        jndiProps.put("java.naming.factory.initial",
                      "org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory");
        jndiProps.put("queue.target/queue",
                      "target");
        jndiProps.put("topic.source/topic",
                      "topic");
        return jndiProps;
    }

    private static InitialContext createContext() throws Exception {
        Hashtable<String, String> jndiProps = createJndiParams();
        return new InitialContext(jndiProps);
    }

    private static class MyExceptionListener implements ExceptionListener {

        @Override
        public void onException(JMSException e) {
            System.out.println();
        }
    }
}
