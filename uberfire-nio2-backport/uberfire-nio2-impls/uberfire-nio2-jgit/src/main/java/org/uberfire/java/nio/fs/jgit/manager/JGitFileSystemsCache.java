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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProxy;

public class JGitFileSystemsCache {

    //supplier for fs
    Map<String, Supplier<JGitFileSystem>> fileSystemsSuppliers = new ConcurrentHashMap<>();

    //limited ammount of real instances of FS
    Map<String, Supplier<JGitFileSystem>> memoizedSuppliers;

    public JGitFileSystemsCache(JGitFileSystemProviderConfiguration config) {
        memoizedSuppliers = new LinkedHashMap<String, Supplier<JGitFileSystem>>(config.getJgitFileSystemsInstancesCache() + 1,
                                                                                0.75f,
                                                                                true) {
            public boolean removeEldestEntry(Map.Entry eldest) {
                return size() > config.getJgitFileSystemsInstancesCache();
            }
        };
        memoizedSuppliers = (Map) Collections.synchronizedMap(memoizedSuppliers);
    }

    public void addSupplier(String fsKey,
                            Supplier<JGitFileSystem> createFSSupplier) {
        PortablePreconditions.checkNotNull("fsKey",
                                           fsKey);
        PortablePreconditions.checkNotNull("fsSupplier",
                                           createFSSupplier);

        fileSystemsSuppliers.putIfAbsent(fsKey,
                                         createFSSupplier);

        createMemoizedSupplier(fsKey,
                               createFSSupplier);
    }

    Supplier<JGitFileSystem> createMemoizedSupplier(String fsKey,
                                                    Supplier<JGitFileSystem> createFSSupplier) {
        Supplier<JGitFileSystem> memoizedFSSupplier = MemoizedFileSystemsSupplier.of(createFSSupplier);
        memoizedSuppliers.putIfAbsent(fsKey,
                                      memoizedFSSupplier);
        return memoizedFSSupplier;
    }

    public void remove(String fsName) {
        fileSystemsSuppliers.remove(fsName);
        memoizedSuppliers.remove(fsName);
    }

    public JGitFileSystem get(String fsName) {

        Supplier<JGitFileSystem> memoizedSupplier = memoizedSuppliers.get(fsName);
        if (memoizedSupplier != null) {
            return new JGitFileSystemProxy(fsName,
                                           memoizedSupplier);
        } else if (fileSystemsSuppliers.get(fsName) != null) {
            Supplier<JGitFileSystem> newMemoizedSupplier = createMemoizedSupplier(fsName,
                                                                                  fileSystemsSuppliers.get(fsName));
            return new JGitFileSystemProxy(fsName,
                                           newMemoizedSupplier);
        }
        return null;
    }

    public void clear() {
        memoizedSuppliers.clear();
        fileSystemsSuppliers.clear();
    }

    public boolean containsKey(String fsName) {
        return fileSystemsSuppliers.containsKey(fsName);
    }

    public Collection<String> getFileSystems() {
        return fileSystemsSuppliers.keySet();
    }

    public JGitFileSystemsCacheInfo getCacheInfo() {
        return new JGitFileSystemsCacheInfo();
    }

    public class JGitFileSystemsCacheInfo {

        public int fileSystemsCacheSize() {
            return memoizedSuppliers.size();
        }

        public Set<String> fileSystemsCacheKeys() {
            return memoizedSuppliers.keySet();
        }

        @Override
        public String toString() {
            return "JGitFileSystemsCacheInfo{fileSystemsCacheSize[" + fileSystemsCacheSize() + "], fileSystemsCacheKeys[" + fileSystemsCacheKeys() + "]}";
        }
    }
}
