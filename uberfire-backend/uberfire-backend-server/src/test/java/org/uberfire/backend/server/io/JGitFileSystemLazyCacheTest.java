package org.uberfire.backend.server.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProxy;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.junit.Assert.*;

public class JGitFileSystemLazyCacheTest {

    private static FileSystemTestingUtils fsUtils = new FileSystemTestingUtils();

    static {
        System.out.println("Working Dir: " + new File("").getAbsoluteFile().getAbsolutePath());
    }

    @Before
    public void setup() throws IOException {
        fsUtils.setup(false);
    }

    @After
    public void cleanupFileSystem() {
        fsUtils.cleanup();
    }

    @Test
    public void bla() throws IOException {
        String repoName = "amend-repo-test";
        Path firstWrite = fsUtils.getIoService().get(URI.create("git://" + repoName + "/init1.file"));

        String content = "yay!";
        assertEquals(0,
                     fsUtils.getFSCacheInfo().fileSystemsCacheSize());

        Path secondWrite = fsUtils.getIoService().get(URI.create("git://" + repoName + "/init2.file"));

        System.out.println("----------END OF GET--------");
        fsUtils.getIoService().write(firstWrite,
                                     content);

        String jgitcontent = fsUtils.getIoService().readAllString(firstWrite);
        assertEquals(content,
                     jgitcontent);


        fsUtils.getIoService().write(secondWrite,
                                     content);
        JGitFileSystemProxy fileSystem = (JGitFileSystemProxy) firstWrite.getFileSystem();
        JGitFileSystemProxy fileSystem1 = (JGitFileSystemProxy) secondWrite.getFileSystem();
        assertEquals(fileSystem.getRealJGitFileSystem(),
                     fileSystem1.getRealJGitFileSystem());
    }
}
