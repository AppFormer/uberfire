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
package org.uberfire.java.nio.fs.jgit.ws;

import org.eclipse.jgit.diff.DiffEntry;
import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent;

public class JGitWatchEvent implements WatchEvent {

    private final String sessionId;
    private final String userName;
    private final String message;
    private final DiffEntry diffEntry;
    private final Path oldPath;
    private final Path newPath;

    public JGitWatchEvent(String sessionId,
                          String userName,
                          String message,
                          DiffEntry diffEntry,
                          Path oldPath,
                          Path newPath) {

        this.sessionId = sessionId;
        this.userName = userName;
        this.message = message;
        this.diffEntry = diffEntry;
        this.oldPath = oldPath;
        this.newPath = newPath;
    }

    @Override
    public WatchEvent.Kind kind() {
        DiffEntry.ChangeType changeType = diffEntry.getChangeType();
        switch (changeType) {
            case ADD:
            case COPY:
                return StandardWatchEventKind.ENTRY_CREATE;
            case DELETE:
                return StandardWatchEventKind.ENTRY_DELETE;
            case MODIFY:
                return StandardWatchEventKind.ENTRY_MODIFY;
            case RENAME:
                return StandardWatchEventKind.ENTRY_RENAME;
            default:
                throw new RuntimeException("Unsupported change type: " + changeType);
        }
    }

    @Override
    public int count() {
        return 1;
    }

    @Override
    public Object context() {
        return new WatchContext() {

            @Override
            public Path getPath() {
                return newPath;
            }

            @Override
            public Path getOldPath() {
                return oldPath;
            }

            @Override
            public String getSessionId() {
                return sessionId;
            }

            @Override
            public String getMessage() {
                return message;
            }

            @Override
            public String getUser() {
                return userName;
            }
        };
    }

    @Override
    public String toString() {
        return "WatchEvent{" +
                "newPath=" + newPath +
                ", oldPath=" + oldPath +
                ", sessionId='" + sessionId + '\'' +
                ", userName='" + userName + '\'' +
                ", message='" + message + '\'' +
                ", changeType=" + diffEntry.getChangeType() +
                '}';
    }
}
