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
package org.uberfire.java.nio.fs.jgit.manager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;
import org.uberfire.java.nio.fs.jgit.util.Git;

import static org.eclipse.jgit.lib.Constants.DOT_GIT_EXT;

public class JGitFileSystemsManager {

    private static final Logger LOG = LoggerFactory.getLogger(JGitFileSystemsManager.class);

    private final Map<String, JGitFileSystem> fileSystems = new ConcurrentHashMap<>();

    private final Set<String> closedFileSystems = new HashSet<>();

    private JGitFileSystemProvider jGitFileSystemProvider;

    private JGitFileSystemProviderConfiguration config;

    public JGitFileSystemsManager(JGitFileSystemProvider jGitFileSystemProvider,
                                  JGitFileSystemProviderConfiguration config) {
        this.jGitFileSystemProvider = jGitFileSystemProvider;
        this.config = config;
    }

    public JGitFileSystem newFileSystem(Map<String, String> fullHostNames,
                                        Git git,
                                        String fsName,
                                        CredentialsProvider credential) {
        final JGitFileSystem fs = new JGitFileSystem(jGitFileSystemProvider,
                                                     fullHostNames,
                                                     git,
                                                     fsName,
                                                     credential);

        String key = resolveFsKey(fsName);

        fileSystems.put(key,
                        fs);
        LOG.debug("Running GIT GC on '" + fsName + "'");
        fs.getGit().gc();
        LOG.debug("Registering existing GIT filesystem '" + fsName + "' at " + git.getRepository().getDirectory());

        return fs;
    }

    public String resolveFsKey(String fsName) {
        String gitPath = config.getGitReposParentDir().getAbsolutePath();
        String fsKey = gitPath + fsName;
        return fsKey;
    }

    public void remove(String fsName) {
        String key = resolveFsKey(fsName);
        fileSystems.remove(key);
        closedFileSystems.remove(key);
    }

    public Collection<JGitFileSystem> getFileSystems() {
        return fileSystems.values();
    }

    public JGitFileSystem get(String name) {
        String key = resolveFsKey(name);
        return fileSystems.get(key);
    }

    public void clear() {
        fileSystems.clear();
    }

    public boolean containsKey(String name) {
        String key = resolveFsKey(name);
        return fileSystems.containsKey(key);
    }

    public void addClosedFileSystems(JGitFileSystem fileSystem) {
        String key = resolveFsKey(fileSystem.getName());
        closedFileSystems.add(key);
    }

    public Collection<JGitFileSystem> getClosedFS() {
        return closedFileSystems.stream().map(fs -> fileSystems.get(fs)).collect(Collectors.toList());
    }

    public boolean allTheFSAreClosed() {
        return closedFileSystems.size() == fileSystems.size();
    }

    public JGitFileSystem get(Repository db) {
        String key = extractFSNameFromRepo(db);
        return fileSystems.get(key);
    }

    private String extractFSNameFromRepo(Repository db) {
        String fsName = db.getDirectory().getName().substring(0,
                                                              db.getDirectory().getName().indexOf(DOT_GIT_EXT));
        return resolveFsKey(fsName);
    }

    public Set<JGitFileSystem> getOpenFileSystems() {
        Collection<JGitFileSystem> open = fileSystems.values();
        return open.stream().filter(fs -> {
            String fsKey = resolveFsKey(fs.getName());
            return !closedFileSystems.contains(fsKey);
        }).collect(Collectors.toSet());
    }
}
