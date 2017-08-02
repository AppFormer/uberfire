package org.uberfire.backend.server.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
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
        System.setProperty("org.uberfire.nio.jgit.cache.instances",
                           "2");
        fsUtils.setup(false);
    }

    @After
    public void cleanupFileSystem() {
        fsUtils.cleanup();
    }

    @Test
    public void basicCache() throws IOException {
        String repoName = "amend-repo-test";
        Path firstWrite = fsUtils.getIoService().get(URI.create("git://" + repoName + "/init1.file"));

        String content = "dora!";

        Path secondWrite = fsUtils.getIoService().get(URI.create("git://" + repoName + "/init2.file"));

        fsUtils.getIoService().write(firstWrite,
                                     content);

        String jgitcontent = fsUtils.getIoService().readAllString(firstWrite);
        assertEquals(content,
                     jgitcontent);

        fsUtils.getIoService().write(secondWrite,
                                     content);
        JGitFileSystemProxy fileSystem = (JGitFileSystemProxy) firstWrite.getFileSystem();
        JGitFileSystemProxy fileSystem1 = (JGitFileSystemProxy) secondWrite.getFileSystem();
        assertEquals(fileSystem,
                     fileSystem1);
    }

    @Test
    public void regenerateFSCache() throws IOException {
        assertTrue(true);

        String defaultRepo = "git://amend-repo-test";

        Path firstWriteFS1 = fsUtils.getIoService().get(URI.create(defaultRepo + "/init1.file"));

        FileSystem fileSystem1Instance1 = firstWriteFS1.getFileSystem();

        String dora1 = "dora1";
        String dora2 = "dora2";
        fsUtils.getIoService().write(firstWriteFS1,
                                     dora1);

        fsUtils.setupJGitRepository(defaultRepo + "2",
                                    false);
        Path writeFS2 = fsUtils.getIoService().get(URI.create(defaultRepo + "2" + "/init1.file"));
        fsUtils.getIoService().write(writeFS2,
                                     dora1);


        fsUtils.setupJGitRepository(defaultRepo + "3",
                                    false);
        Path writeFS3 = fsUtils.getIoService().get(URI.create(defaultRepo + "3" + "/init1.file"));
        fsUtils.getIoService().write(writeFS3,
                                     dora1);

        //memoized cache size == 2 , so

        fsUtils.setupJGitRepository(defaultRepo,
                                    false);
        Path secondWriteFS1 = fsUtils.getIoService().get(URI.create(defaultRepo + "/init2.file"));
        fsUtils.getIoService().write(secondWriteFS1,
                                     dora2);

        FileSystem fileSystem1Instance2 = secondWriteFS1.getFileSystem();

        //not equals because we have to regenerate, but still represent the same FS
        assertNotEquals(fileSystem1Instance1, fileSystem1Instance2);
        System.out.println(fileSystem1Instance1.equals(fileSystem1Instance2));

        //let's remove fs1 again from cache
        fsUtils.setupJGitRepository(defaultRepo + "5",
                                    false);
        fsUtils.setupJGitRepository(defaultRepo + "6",
                                    false);

        //read to see if all the writes are fine on fs1

        String actual1 = fsUtils.getIoService().readAllString(fsUtils.getIoService().get(URI.create(defaultRepo + "/init1.file")));
        String actual2 = fsUtils.getIoService().readAllString(fsUtils.getIoService().get(URI.create(defaultRepo + "/init2.file")));

        assertEquals(dora1, actual2);
        assertEquals(dora1, actual2);
    }
}
