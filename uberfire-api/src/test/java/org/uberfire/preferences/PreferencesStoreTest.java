package org.uberfire.preferences;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.preferences.impl.PreferenceStoreImpl;

import static org.hamcrest.CoreMatchers.is;

public class PreferencesStoreTest {

    private File temp;
    private IOServiceDotFileImpl ioServiceConfig;
    private FileSystem fs;

    public static class SimplePojo {
        private String simpleProperty;

        public String getSimpleProperty() {
            return simpleProperty;
        }

        public void setSimpleProperty(String simpleProperty) {
            this.simpleProperty = simpleProperty;
        }
    }

    @Before
    public void setup() throws IOException {
        temp = File.createTempFile("qmx-jgit", Long.toString(System.nanoTime()));
        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        System.setProperty("org.uberfire.nio.git.dir", temp.getAbsolutePath());
        ioServiceConfig = new IOServiceDotFileImpl();
        fs = ioServiceConfig.newFileSystem(URI.create("git://config-store-repo"), new HashMap<String, Object>() {{
            put("init", true);
        }});
    }


    @Test
    public void obeysHierarchycalKeyResolution() {
        PreferenceStore store = new PreferenceStoreImpl(fs, ioServiceConfig, Scope.APP, Scope.USER, Scope.APP);
        PreferenceStore.ScopedPreferenceStore userPreferenceStore = store.forScope(Scope.USER);
        PreferenceStore.ScopedPreferenceStore appPreferenceStore = store.forScope(Scope.APP);

        SimplePojo pojo1 = new SimplePojo();
        pojo1.setSimpleProperty("meh");
        appPreferenceStore.put("mysettings", pojo1);
        SimplePojo pojo2 = new SimplePojo();
        pojo2.setSimpleProperty("ugh");
        userPreferenceStore.put("mysettings", pojo2);

        SimplePojo pojo = (SimplePojo) store.get("mysettings");

        Assert.assertThat(pojo.getSimpleProperty(), is("ugh"));
    }


}