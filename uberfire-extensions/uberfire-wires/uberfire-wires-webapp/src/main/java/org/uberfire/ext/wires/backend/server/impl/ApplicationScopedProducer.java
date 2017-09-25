/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.wires.backend.server.impl;

import java.util.concurrent.ExecutorService;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSContext;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.backend.server.IOWatchServiceNonDotImpl;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.concurrent.Unmanaged;
import org.uberfire.ext.wires.backend.server.impl.jms.JMSBridge;
import org.uberfire.ext.wires.shared.social.ShowcaseSocialUserEvent;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.io.impl.cluster.IOServiceClusterImpl;

@ApplicationScoped
public class ApplicationScopedProducer {

    IOWatchServiceNonDotImpl watchService;

    private AuthenticationService authenticationService;

    private ClusterServiceFactory clusterServiceFactory;

    private IOService ioService;

    private ExecutorService executorService;

    @Inject
    JMSBridge jmsProducer;

    @Inject
    private JMSContext context;

    public ApplicationScopedProducer() {
    }

    @Inject
    public ApplicationScopedProducer(IOWatchServiceNonDotImpl watchService,
                                     AuthenticationService authenticationService,
                                     @Named("clusterServiceFactory") ClusterServiceFactory clusterServiceFactory,
                                     @Unmanaged ExecutorService executorService) {
        this.watchService = watchService;
        this.authenticationService = authenticationService;
        this.clusterServiceFactory = clusterServiceFactory;
        this.executorService = executorService;
    }

    @PostConstruct
    public void setup() {
        if (clusterServiceFactory == null) {
            ioService = new IOServiceDotFileImpl(watchService);
        } else {
            ioService = new IOServiceClusterImpl(new IOServiceDotFileImpl(watchService),
                                                 clusterServiceFactory,
                                                 executorService);
        }
    }

    public void onEvent(@Observes ShowcaseSocialUserEvent event) {
        jmsProducer.sendMessage(event.toString());
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @RequestScoped
    public User getIdentity() {
        return authenticationService.getUser();
    }
}
