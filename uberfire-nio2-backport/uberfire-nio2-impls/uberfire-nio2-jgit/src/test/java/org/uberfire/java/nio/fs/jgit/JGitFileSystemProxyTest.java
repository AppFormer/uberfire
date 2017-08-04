package org.uberfire.java.nio.fs.jgit;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;

public class JGitFileSystemProxyTest extends AbstractTestInfra {

    private int gitDaemonPort;

    @Override
    public Map<String, String> getGitPreferences() {
        Map<String, String> gitPrefs = super.getGitPreferences();
        gitPrefs.put("org.uberfire.nio.git.daemon.enabled",
                     "true");
        // use different port for every test -> easy to run tests in parallel
        gitDaemonPort = findFreePort();
        gitPrefs.put("org.uberfire.nio.git.daemon.port",
                     String.valueOf(gitDaemonPort));
        return gitPrefs;
    }

    @Test
    public void proxyTest() {
        final URI originRepo = URI.create("git://encoding-origin-name");

        final JGitFileSystem origin = (JGitFileSystem) provider.newFileSystem(originRepo,
                                                                              Collections.emptyMap());

        assertTrue(origin instanceof JGitFileSystemProxy);
        JGitFileSystemProxy proxy = (JGitFileSystemProxy) origin;
        JGitFileSystem realJGitFileSystem = proxy.getRealJGitFileSystem();
        assertTrue(realJGitFileSystem instanceof JGitFileSystemImpl);

        assertTrue(proxy.equals(realJGitFileSystem));
        assertTrue(realJGitFileSystem.equals(proxy));
    }
}