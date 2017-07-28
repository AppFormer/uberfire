package org.uberfire.java.nio.fs.jgit.manager;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;

public class JGitFileSystemsCache {

    private Map<String, Supplier<JGitFileSystem>> fileSystemsSuppliers = new ConcurrentHashMap<>();

    private Map<String, JGitFileSystem> fileSystemsCache;

    public JGitFileSystemsCache(JGitFileSystemProviderConfiguration config) {
        fileSystemsCache = new LinkedHashMap<String, JGitFileSystem>(config.getJgitFileSystemsInstancesCache() + 1,
                                                                     0.75f,
                                                                     true) {
            public boolean removeEldestEntry(Map.Entry eldest) {
                return size() > config.getJgitFileSystemsInstancesCache();
            }
        };
        fileSystemsCache = (Map) Collections.synchronizedMap(fileSystemsCache);
    }

    public void addSupplier(String fsKey,
                            Supplier<JGitFileSystem> fsSupplier) {
        PortablePreconditions.checkNotNull("fsKey",
                                           fsKey);
        PortablePreconditions.checkNotNull("fsSupplier",
                                           fsSupplier);

        fileSystemsSuppliers.put(fsKey,
                                 fsSupplier);
        setEntry(fsKey,
                 fsSupplier.get());
    }

    private void setEntry(String fsKey,
                          JGitFileSystem fs) {
        fileSystemsCache.put(fsKey,
                             fs);
    }

    public void remove(String fsName) {
        fileSystemsSuppliers.remove(fsName);
    }

    public JGitFileSystem get(String fsName) {

        if (fileSystemsCache.get(fsName) != null) {
            return fileSystemsCache.get(fsName);
        }
        if (fileSystemsSuppliers.get(fsName) != null) {
            JGitFileSystem jGitFileSystem = fileSystemsSuppliers.get(fsName).get();
            setEntry(fsName,
                     jGitFileSystem);
            return jGitFileSystem;
        }
        return null;
    }

    public void clear() {
        this.fileSystemsCache.clear();
        fileSystemsSuppliers.clear();
    }

    public boolean containsKey(String fsName) {
        return fileSystemsSuppliers.containsKey(fsName);
    }
}
