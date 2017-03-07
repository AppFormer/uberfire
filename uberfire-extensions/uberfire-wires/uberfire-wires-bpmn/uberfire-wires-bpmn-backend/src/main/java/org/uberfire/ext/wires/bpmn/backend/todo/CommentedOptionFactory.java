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
package org.uberfire.ext.wires.bpmn.backend.todo;

import java.util.Date;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;

/**
 * When Bpmn moves to KIE-WB this class can be removed.
 */
public class CommentedOptionFactory {

    public static CommentedOption makeCommentedOption(final User identity,
                                                      final SessionInfo sessionInfo,
                                                      final String commitMessage) {
        final String name = identity.getIdentifier();
        final Date when = new Date();
        final CommentedOption co = new CommentedOption(sessionInfo.getId(),
                                                       name,
                                                       null,
                                                       commitMessage,
                                                       when);
        return co;
    }
}
