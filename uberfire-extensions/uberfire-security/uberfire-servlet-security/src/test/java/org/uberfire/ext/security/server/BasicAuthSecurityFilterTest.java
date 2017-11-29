/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.security.server;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BasicAuthSecurityFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private HttpSession httpSession;

    @Test
    public void testIndependentSessionInvalidated() throws Exception {

        SessionProvider sessionProvider = new SessionProvider(httpSession,
                                                              1);

        when(authenticationService.getUser()).thenReturn(new UserImpl("testUser"));
        when(request.getSession(anyBoolean())).then(new Answer<HttpSession>() {
            @Override
            public HttpSession answer(InvocationOnMock invocationOnMock) throws Throwable {
                return sessionProvider.provideSession();
            }
        });

        final BasicAuthSecurityFilter filter = new BasicAuthSecurityFilter();
        filter.authenticationService = authenticationService;
        filter.doFilter(request,
                        response,
                        chain);

        verify(httpSession,
               times(1)).invalidate();
    }

    @Test
    public void testExistingSessionNotInvalidated() throws Exception {

        SessionProvider sessionProvider = new SessionProvider(httpSession);

        when(authenticationService.getUser()).thenReturn(new UserImpl("testUser"));
        when(request.getSession(anyBoolean())).then(new Answer<HttpSession>() {
            @Override
            public HttpSession answer(InvocationOnMock invocationOnMock) throws Throwable {
                return sessionProvider.provideSession();
            }
        });

        final BasicAuthSecurityFilter filter = new BasicAuthSecurityFilter();
        filter.authenticationService = authenticationService;
        filter.doFilter(request,
                        response,
                        chain);

        verify(httpSession,
               never()).invalidate();
    }

    @Test
    public void testEmptyPassword() throws Exception {

        String username = "fakeUser";
        String password = "";

        String authData = username + ":" + password;
        String authEncoded = Base64.encodeBase64String(authData.getBytes());

        when(request.getHeader("Authorization")).thenReturn("Basic " + authEncoded);

        final BasicAuthSecurityFilter filter = new BasicAuthSecurityFilter();
        filter.authenticationService = authenticationService;
        filter.doFilter(request,
                        response,
                        chain);

        verify(authenticationService,
               times(1)).login(username,
                               password);
    }

    private class SessionProvider {

        private int counter = 0;
        private HttpSession httpSession;

        public SessionProvider(HttpSession httpSession) {
            this.httpSession = httpSession;
        }

        public SessionProvider(HttpSession httpSession,
                               int counter) {
            this.httpSession = httpSession;
            this.counter = counter;
        }

        public HttpSession provideSession() {
            if (counter == 0) {
                return httpSession;
            }
            counter--;
            return null;
        }
    }
}
