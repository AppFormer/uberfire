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

/**
 *
 * Todo Tomorrow eder
 *
 * arrumar testes, implementar lock e estudar watch service como vai funcionar?
 *
 *
 *
 */
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
                            Supplier<JGitFileSystem> createFSSupplier) {
        PortablePreconditions.checkNotNull("fsKey",
                                           fsKey);
        PortablePreconditions.checkNotNull("fsSupplier",
                                           createFSSupplier);

        Supplier<JGitFileSystem> cachedSupplier = createCachedSupplier(fsKey,
                                                                       createFSSupplier);

        fileSystemsSuppliers.put(fsKey,
                                 cachedSupplier);
    }

    public void remove(String fsName) {
        fileSystemsSuppliers.remove(fsName);
    }

    public JGitFileSystem get(String fsName) {

//        if (fileSystemsCache.get(fsName) != null) {
//            return new JGitFileSystemProxy(fileSystemsSuppliers.get(fsName));
//        }
        if (fileSystemsSuppliers.get(fsName) != null) {

            return new JGitFileSystemProxy(fileSystemsSuppliers.get(fsName));
        }
        //if there is no cache, regenerate
        return null;
    }

    Supplier<JGitFileSystem> createCachedSupplier(String fsName,
                                                  Supplier<JGitFileSystem> jGitFileSystemSupplier) {

        return LazyFileSystemsSupplier.of(jGitFileSystemSupplier);
    }

    public void clear() {
        this.fileSystemsCache.clear();
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
            return fileSystemsCache.size();
        }

        public Set<String> fileSystemsCacheKeys() {
            return fileSystemsCache.keySet();
        }

        @Override
        public String toString() {
            return "JGitFileSystemsCacheInfo{fileSystemsCacheSize[" + fileSystemsCacheSize() + "], fileSystemsCacheKeys[" + fileSystemsCacheKeys() + "]}";
        }
    }
}
