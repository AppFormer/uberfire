/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.java.nio.fs.jgit.ws.jms;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.file.WatchEvent;

public class JGitEventsBroadcaster {

    private Session session;
    private MessageProducer messageProducer;

    String nodeId = UUID.randomUUID().toString();

    public JGitEventsBroadcaster() {

        try {
            setupXYZ();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupXYZ() throws Exception {
        InitialContext context = createContext();
        ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
        Destination topic = (Destination) context.lookup("source/topic");

        Connection connection = factory.createConnection(System.getProperty("admin"),
                                                         System.getProperty("admin"));
        connection.setExceptionListener(new MyExceptionListener());
        connection.start();

        session = connection.createSession(false,
                                           Session.AUTO_ACKNOWLEDGE);

        messageProducer = session.createProducer(topic);

        MessageConsumer consumer = session.createConsumer(topic);

        consumer.setMessageListener(new MessageListener() {

            @Override
            public void onMessage(Message message) {
                System.out.println("Listener");
                try {
                    if (message instanceof ObjectMessage) {
                        ObjectMessage receivedMessage1 = (ObjectMessage) message;
                        Serializable object = null;
                        try {
                            object = receivedMessage1.getObject();
                            if (object instanceof MessageWrapper) {
                                MessageWrapper messageWrapper = (MessageWrapper) object;

                                if (!messageWrapper.getNodeId().equals(nodeId)) {

                                    WatchEvent ws = messageWrapper.getWatchEvent();
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
                                else{
                                    System.out.println("Watch event same id");
                                }
                            }
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
//                    System.out.println(((TextMessage) messa/ge).getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Hashtable<String, String> createJndiParams() {
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

    private InitialContext createContext() throws Exception {
        Hashtable<String, String> jndiProps = createJndiParams();
        return new InitialContext(jndiProps);
    }

    //TODO CLOSE CONNECFION

    public void sendMessageFIXMENAME(List<WatchEvent<?>> msg) {
        TextMessage message;
        try {
            for (WatchEvent watchEvent : msg) {
                MessageWrapper serializable = new MessageWrapper(nodeId,
                                                                 watchEvent);

                boolean teste = serializable instanceof Serializable;

                ObjectMessage objectMessage = session.createObjectMessage(serializable);
                messageProducer.send(objectMessage);
            }
//            message = session.createTextMessage("this is a text message sent at " + System.currentTimeMillis() + msg);
//            messageProducer.send(message);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private static class MyExceptionListener implements ExceptionListener {

        @Override
        public void onException(JMSException e) {
            System.out.println();
        }
    }
}
