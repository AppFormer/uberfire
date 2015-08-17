package org.uberfire.preferences.impl;

import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.preferences.Scope;
import org.uberfire.preferences.PreferenceStore;

public class PreferenceStoreImpl implements PreferenceStore {

    private final FileSystem fileSystem;
    private final IOService ioService;
    private final Scope[] resolutionOrder;
    private final Scope defaultScope;


    public PreferenceStoreImpl(FileSystem fileSystem, IOService ioService, Scope defaultScope, Scope... resolutionOrder) {
        this.fileSystem = fileSystem;
        this.ioService = ioService;
        this.defaultScope = defaultScope;
        this.resolutionOrder = resolutionOrder;
    }

    @Override
    public ScopedPreferenceStore forScope(Scope scope) {
        return new ScopedPreferenceStoreImpl(fileSystem, ioService, scope);
    }

    public Object get(String key) {
        for (Scope scope : resolutionOrder) {
            Object result = forScope(scope).get(key);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    @Override
    public void put(String key, Object value) {
        forScope(this.defaultScope).put(key, value);
    }
}
