package org.uberfire.java.nio.fs.jgit.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

public class JGitFileSystemsCache {

    private Map<String, Supplier<JGitFileSystem>> fileSystemsSuppliers = new ConcurrentHashMap<>();

    public JGitFileSystemsCache() {

    }

    public void addSupplier(String fsKey,
                            Supplier<JGitFileSystem> fsSupplier) {
        fileSystemsSuppliers.put(fsKey,
                                 fsSupplier);
    }

    public void remove(String fsName) {
        fileSystemsSuppliers.remove(fsName);
    }

    public JGitFileSystem get(String fsName) {

        //move this validation to maanger
        return fileSystemsSuppliers.get(fsName) != null ? fileSystemsSuppliers.get(fsName).get() : null;
    }

    public void clear() {
        fileSystemsSuppliers = new ConcurrentHashMap<>();
    }

    public boolean containsKey(String fsName) {
        return fileSystemsSuppliers.containsKey(fsName);
    }
}
