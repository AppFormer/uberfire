/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.server.cdi;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.jboss.errai.bus.server.api.RpcContext;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.rpc.impl.SessionInfoImpl;
import org.uberfire.security.server.cdi.SecurityFactory;

public class UberFireGeneralFactory {

    static private ServletContext servletContext;

    public static void setServletContext( final ServletContext sContext ) {
        servletContext = sContext;
    }

    @Produces
    @Named("uf")
    public static ServletContext getServletContent() {
        return servletContext;
    }

    @Produces
    @RequestScoped
    @Default
    public static SessionInfo getSessionInfo() {
        return new SessionInfoImpl( RpcContext.getQueueSession().getSessionId(), SecurityFactory.getIdentity() );
    }

}
