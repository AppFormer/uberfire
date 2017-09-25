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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchService;

public class JGitFileSystemsEventsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JGitFileSystemsEventsManager.class);

    private final Map<String, JGitFileSystemWatchServices> fsWatchServices = new ConcurrentHashMap<>();

    public WatchService newWatchService(String fsName)
            throws UnsupportedOperationException, IOException {
        fsWatchServices.putIfAbsent(fsName,
                                    new JGitFileSystemWatchServices());

        return fsWatchServices.get(fsName).newWatchService(fsName);
    }

    public void publishEvents(String fsName,
                              Path watchable,
                              List<WatchEvent<?>> elist) {

        JGitFileSystemWatchServices watchService = fsWatchServices.get(fsName);

        if (watchService == null) {
            return;
        }

        watchService.publishEvents(watchable,
                                   elist);
        //ederign publish cluster messages
    }

    public void close(String name) {

        JGitFileSystemWatchServices watchService = fsWatchServices.get(name);

        if (watchService != null) {
            try {
                watchService.close();
            } catch (final Exception ex) {
                LOGGER.error("Can't close watch service [" + toString() + "]",
                             ex);
            }
        }
    }
}
