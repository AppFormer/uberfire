package org.uberfire.java.nio.fs.jgit.manager;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProxy;

public class JGitFileSystemsCache {

    private Map<String, Supplier<JGitFileSystem>> fileSystemsSuppliers = new ConcurrentHashMap<>();

    private Map<String, Supplier<JGitFileSystem>> memoizedSuppliers;

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

    public void remove(String fsName) {
        fileSystemsSuppliers.remove(fsName);
        memoizedSuppliers.remove(fsName);
    }

    public JGitFileSystem get(String fsName) {

        if (memoizedSuppliers.get(fsName) != null) {
            return new JGitFileSystemProxy(memoizedSuppliers.get(fsName));
        }
        else if (fileSystemsSuppliers.get(fsName) != null) {
            createMemoizedSupplier(fsName,
                                   fileSystemsSuppliers.get(fsName));
            return new JGitFileSystemProxy(memoizedSuppliers.get(fsName));
        }
        //if there is no cache, regenerate
        return null;
    }

    Supplier<JGitFileSystem> createMemoizedSupplier(String fsKey,
                                                    Supplier<JGitFileSystem> createFSSupplier) {
        Supplier<JGitFileSystem> memoizedFSSupplier = MemoizedFileSystemsSupplier.of(createFSSupplier);
        memoizedSuppliers.putIfAbsent(fsKey,
                                      memoizedFSSupplier);
        return memoizedFSSupplier;
    }

    public void clear() {
        memoizedSuppliers.clear();
        fileSystemsSuppliers.clear();
    }

    public boolean containsKey(String fsName) {
        return fileSystemsSuppliers.containsKey(fsName);
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
