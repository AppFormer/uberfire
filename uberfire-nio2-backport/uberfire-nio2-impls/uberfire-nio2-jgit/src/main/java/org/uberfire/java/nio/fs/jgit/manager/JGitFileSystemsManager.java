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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemImpl;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.ws.JGitFileSystemsEventsManager;

import static org.eclipse.jgit.lib.Constants.DOT_GIT_EXT;

public class JGitFileSystemsManager {

    private final Set<String> closedFileSystems = new HashSet<>();

    private final Set<String> fileSystemsRoot = new HashSet<>();

    private JGitFileSystemProvider jGitFileSystemProvider;

    private final JGitFileSystemsCache fsCache;

    public JGitFileSystemsManager(JGitFileSystemProvider jGitFileSystemProvider,
                                  JGitFileSystemProviderConfiguration config) {
        this.jGitFileSystemProvider = jGitFileSystemProvider;
        fsCache = new JGitFileSystemsCache(config);
    }

    public void newFileSystem(Supplier<Map<String, String>> fullHostNames,
                              Supplier<Git> git,
                              Supplier<String> fsName,
                              Supplier<CredentialsProvider> credential,
                              Supplier<JGitFileSystemsEventsManager> fsManager) {

        Supplier<JGitFileSystem> fsSupplier = createFileSystemSupplier(fullHostNames,
                                                                       git,
                                                                       fsName,
                                                                       credential,
                                                                       fsManager);

        fsCache.addSupplier(fsName.get(),
                            fsSupplier);
        fileSystemsRoot.addAll(parseFSRoots(fsName.get()));
    }

    List<String> parseFSRoots(String fsKey) {
        List<String> roots = new ArrayList<>();
        fsKey = cleanupFsName(fsKey);
        int index = fsKey.indexOf("/");
        while (index >= 0) {
            roots.add(fsKey.substring(0,
                                      index));
            index = fsKey.indexOf("/",
                                  index + 1);
        }
        roots.add(fsKey);
        return roots;
    }

    private String cleanupFsName(String fsKey) {
        if (fsKey.indexOf("/") == 0) {
            fsKey = fsKey.substring(1);
        }
        if (fsKey.lastIndexOf("/") == fsKey.length() - 1) {
            fsKey = fsKey.substring(0,
                                    fsKey.length() - 1);
        }

        return fsKey;
    }

    private Supplier<JGitFileSystem> createFileSystemSupplier(Supplier<Map<String, String>> fullHostNames,
                                                              Supplier<Git> git,
                                                              Supplier<String> fsName,
                                                              Supplier<CredentialsProvider> credential,
                                                              Supplier<JGitFileSystemsEventsManager> fsManager) {

        return () -> newFileSystem(fullHostNames.get(),
                                   git.get(),
                                   fsName.get(),
                                   credential.get(),
                                   fsManager.get());
    }

    private JGitFileSystem newFileSystem(Map<String, String> fullHostNames,
                                         Git git,
                                         String fsName,
                                         CredentialsProvider credential,
                                         JGitFileSystemsEventsManager fsEventsManager) {
        final JGitFileSystem fs = new JGitFileSystemImpl(jGitFileSystemProvider,
                                                         fullHostNames,
                                                         git,
                                                         fsName,
                                                         credential,
                                                         fsEventsManager);

        fs.getGit().gc();

        return fs;
    }

    public void remove(String realFSKey) {
        fsCache.remove(realFSKey);
        closedFileSystems.remove(realFSKey);
    }

    public JGitFileSystem get(String fsName) {
        return fsCache.get(fsName);
    }

    public void clear() {
        fsCache.clear();
        closedFileSystems.clear();
        fileSystemsRoot.clear();
    }

    public boolean containsKey(String fsName) {

        return fsCache.getFileSystems().contains(fsName) && !closedFileSystems.contains(fsName);
    }

    public boolean containsRoot(String fsName) {
        return fileSystemsRoot.contains(fsName);
    }

    public void addClosedFileSystems(JGitFileSystem fileSystem) {
        String realFSKey = fileSystem.getName();
        closedFileSystems.add(realFSKey);
        List<String> roots = parseFSRoots(fileSystem.getName());
        fileSystemsRoot.removeAll(roots);
    }

    public boolean allTheFSAreClosed() {
        return closedFileSystems.size() == fsCache.getFileSystems().size();
    }

    public JGitFileSystem get(Repository db) {
        String key = extractFSNameFromRepo(db);
        return fsCache.get(key);
    }

    public Set<JGitFileSystem> getOpenFileSystems() {
        return fsCache.getFileSystems().stream().filter(fsName -> !closedFileSystems.contains(fsName))
                .map(fsName -> get(fsName)).collect(Collectors.toSet());
    }

    public JGitFileSystemsCache getFsCache() {
        return fsCache;
    }

    private String extractFSNameFromRepo(Repository db) {
        final String nioGitPath = ".niogit/";

        String fullPath = db.getDirectory().getPath();

        fullPath = fullPath.substring((fullPath.indexOf(nioGitPath) + nioGitPath.length()),
                                      fullPath.length());
        String fsName = fullPath.substring(0,
                                           fullPath.indexOf(DOT_GIT_EXT));
        return fsName;
    }
}
