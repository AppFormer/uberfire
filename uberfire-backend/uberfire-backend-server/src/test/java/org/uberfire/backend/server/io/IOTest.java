package org.uberfire.backend.server.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.file.Path;
import org.uberfire.mocks.FileSystemTestingUtils;

public class IOTest {

    private static FileSystemTestingUtils fsUtils = new FileSystemTestingUtils();

    static {
        System.out.println("Working Dir: " + new File("").getAbsoluteFile().getAbsolutePath());
    }

    @Before
    public void setup() throws IOException {
        fsUtils.setup();
    }

    @After
    public void cleanupFileSystem() {
        fsUtils.cleanup();
    }

    @Test
    public void bla() {
        Path init = fsUtils.getIoService().get(URI.create("git://amend-repo-test/init2.file"));
        fsUtils.getIoService().write(init,
                                     "setupFS!");
        System.out.println("");
    }
}
