package org.uberfire.backend.server.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.backend.server.IOWatchServiceAllImpl;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;
import org.uberfire.preferences.PreferenceStorage;
import org.uberfire.preferences.PreferenceStore;
import org.uberfire.preferences.impl.DefaultResolutionStrategy;
import org.uberfire.preferences.impl.PreferenceStoreImpl;

import static org.uberfire.preferences.DefaultScopeTypes.*;

@Startup(StartupType.BOOTSTRAP)
@ApplicationScoped
public class ApplicationScopedProducer {

    @Inject
    private PreferenceStorage preferenceStorage;

    @Inject
    private DefaultResolutionStrategy defaultResolutionStrategy;

    @Inject
    private IOWatchServiceAllImpl watchService;

    private IOService ioService;

    private PreferenceStore preferenceStore;

    @PostConstruct
    public void setup() {
        ioService = new IOServiceNio2WrapperImpl( "1", watchService );
        preferenceStore = new PreferenceStoreImpl( preferenceStorage, USER.toScope(), defaultResolutionStrategy);
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @ApplicationScoped
    public PreferenceStore preferenceStore() {
        return preferenceStore;
    }

}
