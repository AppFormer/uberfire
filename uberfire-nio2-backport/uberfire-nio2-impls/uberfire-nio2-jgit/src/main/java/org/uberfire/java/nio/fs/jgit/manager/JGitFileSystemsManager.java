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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;
import org.uberfire.java.nio.fs.jgit.util.Git;

import static org.eclipse.jgit.lib.Constants.DOT_GIT_EXT;

public class JGitFileSystemsManager {

    private final Set<String> closedFileSystems = new HashSet<>();

    private final Set<String> fileSystems = new HashSet<>();

    private JGitFileSystemProvider jGitFileSystemProvider;

    private final JGitFileSystemsCache fsCache;

    private JGitFileSystemProviderConfiguration config;

    public JGitFileSystemsManager(JGitFileSystemProvider jGitFileSystemProvider,
                                  JGitFileSystemProviderConfiguration config
    ) {
        this.jGitFileSystemProvider = jGitFileSystemProvider;
        this.config = config;
        fsCache = new JGitFileSystemsCache(config);
    }

    public void newFileSystem(Supplier<Map<String, String>> fullHostNames,
                              Supplier<Git> git,
                              Supplier<String> fsName,
                              Supplier<CredentialsProvider> credential) {

        Supplier<JGitFileSystem> fsSupplier = createFileSystemSupplier(fullHostNames,
                                                                       git,
                                                                       fsName,
                                                                       credential);
        String realFSKey = resolveRealFsKey(fsName.get());

        fsCache.addSupplier(realFSKey,
                            fsSupplier);
        fileSystems.add(realFSKey);
    }

    private Supplier<JGitFileSystem> createFileSystemSupplier(Supplier<Map<String, String>> fullHostNames,
                                                              Supplier<Git> git,
                                                              Supplier<String> fsName,
                                                              Supplier<CredentialsProvider> credential) {
        String realFSKey = resolveRealFsKey(fsName.get());

        return () -> newFileSystem(fullHostNames.get(),
                                   git.get(),
                                   realFSKey,
                                   credential.get());
    }

    private JGitFileSystem newFileSystem(Map<String, String> fullHostNames,
                                         Git git,
                                         String fsName,
                                         CredentialsProvider credential) {
        final JGitFileSystem fs = new JGitFileSystem(jGitFileSystemProvider,
                                                     fullHostNames,
                                                     git,
                                                     fsName,
                                                     credential);

        fs.getGit().gc();


        return fs;
    }

    public void remove(String fsName) {
        String realFSKey = resolveRealFsKey(fsName);
        fsCache.remove(realFSKey);
        fileSystems.remove(realFSKey);
        closedFileSystems.remove(realFSKey);
    }

    public JGitFileSystem get(String fsName) {
        //return supplier
        String realFSKey = resolveRealFsKey(fsName);
        return fsCache.get(realFSKey);
    }

    public void clear() {
        fsCache.clear();
        fileSystems.clear();
        closedFileSystems.clear();
    }

    public boolean containsKey(String fsName) {
        String realFSKey = resolveRealFsKey(fsName);
        return fileSystems.contains(realFSKey);
    }

    public void addClosedFileSystems(JGitFileSystem fileSystem) {
        String realFSKey = resolveRealFsKey(fileSystem.getName());
        closedFileSystems.add(realFSKey);
    }

    public boolean allTheFSAreClosed() {
        return closedFileSystems.size() == fileSystems.size();
    }

    public JGitFileSystem get(Repository db) {
        String key = extractFSNameFromRepo(db);
        return fsCache.get(key);
    }

    private String extractFSNameFromRepo(Repository db) {
        String fsName = db.getDirectory().getName().substring(0,
                                                              db.getDirectory().getName().indexOf(DOT_GIT_EXT));
        return resolveRealFsKey(fsName);
    }

    public Set<JGitFileSystem> getOpenFileSystems() {
        return fileSystems.stream().filter(fsName -> !closedFileSystems.contains(fsName))
                .map(fsName -> get(fsName)).collect(Collectors.toSet());
    }

    public String resolveRealFsKey(String fsName) {
//        if(fsName.contains("/")){
//            return fsName;
//        }
//        String gitPath = config.getGitReposParentDir().getAbsolutePath();
//        String fsKey = gitPath + fsName;
        return fsName;
    }
}
