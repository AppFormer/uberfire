package org.uberfire.preferences.impl;

import com.thoughtworks.xstream.XStream;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.preferences.PreferenceStore;
import org.uberfire.preferences.Scope;

public class ScopedPreferenceStoreImpl implements PreferenceStore.ScopedPreferenceStore {

    private final FileSystem fileSystem;
    private final IOService ioService;
    private final Scope scope;
    private final XStream xs = new XStream();

    public ScopedPreferenceStoreImpl(FileSystem fileSystem, IOService ioService, Scope scope) {
        this.fileSystem = fileSystem;
        this.ioService = ioService;
        this.scope = scope;
    }

    @Override
    public void put(String key, Object value) {
        try {
            ioService.startBatch(fileSystem);
            Path path = fileSystem.getPath(buildStoragePath(key));
            ioService.write(path, xs.toXML(value));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            ioService.endBatch();
        }
    }

    @Override
    public Object get(String key) {
        Path path = fileSystem.getPath(buildStoragePath(key));
        try {
            if (ioService.exists(path)) {
                String content = ioService.readAllString(path);
                return xs.fromXML(content);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String buildStoragePath(String key) {
        return "/config/"+ scope.key() + "/" + key + ".preferences";
    }
}
