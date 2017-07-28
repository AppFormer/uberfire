/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.java.nio.fs.jgit.daemon.ssh;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.Channel;
import org.apache.sshd.common.Cipher;
import org.apache.sshd.common.Compression;
import org.apache.sshd.common.KeyExchange;
import org.apache.sshd.common.Mac;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.RequestHandler;
import org.apache.sshd.common.Signature;
import org.apache.sshd.common.cipher.AES128CTR;
import org.apache.sshd.common.cipher.AES192CBC;
import org.apache.sshd.common.cipher.AES256CBC;
import org.apache.sshd.common.cipher.AES256CTR;
import org.apache.sshd.common.cipher.ARCFOUR128;
import org.apache.sshd.common.cipher.ARCFOUR256;
import org.apache.sshd.common.compression.CompressionNone;
import org.apache.sshd.common.file.nativefs.NativeFileSystemFactory;
import org.apache.sshd.common.forward.DefaultTcpipForwarderFactory;
import org.apache.sshd.common.forward.TcpipServerChannel;
import org.apache.sshd.common.mac.HMACMD5;
import org.apache.sshd.common.mac.HMACMD596;
import org.apache.sshd.common.mac.HMACSHA1;
import org.apache.sshd.common.mac.HMACSHA196;
import org.apache.sshd.common.mac.HMACSHA256;
import org.apache.sshd.common.mac.HMACSHA512;
import org.apache.sshd.common.random.BouncyCastleRandom;
import org.apache.sshd.common.random.JceRandom;
import org.apache.sshd.common.random.SingletonRandomFactory;
import org.apache.sshd.common.session.ConnectionService;
import org.apache.sshd.common.signature.SignatureDSA;
import org.apache.sshd.common.signature.SignatureECDSA;
import org.apache.sshd.common.signature.SignatureRSA;
import org.apache.sshd.common.util.SecurityUtils;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.UnknownCommand;
import org.apache.sshd.server.global.CancelTcpipForwardHandler;
import org.apache.sshd.server.global.KeepAliveHandler;
import org.apache.sshd.server.global.NoMoreSessionsHandler;
import org.apache.sshd.server.global.TcpipForwardHandler;
import org.apache.sshd.server.kex.DHG1;
import org.apache.sshd.server.kex.DHG14;
import org.apache.sshd.server.kex.DHGEX;
import org.apache.sshd.server.kex.DHGEX256;
import org.apache.sshd.server.kex.ECDHP256;
import org.apache.sshd.server.kex.ECDHP384;
import org.apache.sshd.server.kex.ECDHP521;
import org.apache.sshd.server.keyprovider.AbstractGeneratorHostKeyProvider;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.security.FileSystemAuthenticator;
import org.uberfire.java.nio.security.FileSystemAuthorizer;
import org.uberfire.java.nio.security.FileSystemUser;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

public class GitSSHService {

    private final SshServer sshd = buildSshServer();

    public static SshServer buildSshServer() {
        SshServer sshd = new SshServer();
        // DHG14 uses 2048 bits key which are not supported by the default JCE provider
        // EC keys are not supported until OpenJDK 8
        if (SecurityUtils.isBouncyCastleRegistered()) {
            sshd.setKeyExchangeFactories(Arrays.<NamedFactory<KeyExchange>>asList(
                    new DHGEX256.Factory(),
                    new DHGEX.Factory(),
                    new ECDHP256.Factory(),
                    new ECDHP384.Factory(),
                    new ECDHP521.Factory(),
                    new DHG14.Factory(),
                    new DHG1.Factory()));
            sshd.setSignatureFactories(Arrays.<NamedFactory<Signature>>asList(
                    new SignatureECDSA.NISTP256Factory(),
                    new SignatureECDSA.NISTP384Factory(),
                    new SignatureECDSA.NISTP521Factory(),
                    new SignatureDSA.Factory(),
                    new SignatureRSA.Factory()));
            sshd.setRandomFactory(new SingletonRandomFactory(new BouncyCastleRandom.Factory()));
            // EC keys are not supported until OpenJDK 7
        } else if (SecurityUtils.hasEcc()) {
            sshd.setKeyExchangeFactories(Arrays.<NamedFactory<KeyExchange>>asList(
                    new DHGEX256.Factory(),
                    new DHGEX.Factory(),
                    new ECDHP256.Factory(),
                    new ECDHP384.Factory(),
                    new ECDHP521.Factory(),
                    new DHG1.Factory()));
            sshd.setSignatureFactories(Arrays.<NamedFactory<Signature>>asList(
                    new SignatureECDSA.NISTP256Factory(),
                    new SignatureECDSA.NISTP384Factory(),
                    new SignatureECDSA.NISTP521Factory(),
                    new SignatureDSA.Factory(),
                    new SignatureRSA.Factory()));
            sshd.setRandomFactory(new SingletonRandomFactory(new JceRandom.Factory()));
        } else {
            sshd.setKeyExchangeFactories(Arrays.<NamedFactory<KeyExchange>>asList(
                    new DHGEX256.Factory(),
                    new DHGEX.Factory(),
                    new DHG1.Factory()));
            sshd.setSignatureFactories(Arrays.<NamedFactory<Signature>>asList(
                    new SignatureDSA.Factory(),
                    new SignatureRSA.Factory()));
            sshd.setRandomFactory(new SingletonRandomFactory(new JceRandom.Factory()));
        }
        setUpDefaultCiphers(sshd);
        // Compression is not enabled by default
        // sshd.setCompressionFactories(Arrays.<NamedFactory<Compression>>asList(
        //         new CompressionNone.Factory(),
        //         new CompressionZlib.Factory(),
        //         new CompressionDelayedZlib.Factory()));
        sshd.setCompressionFactories(Arrays.<NamedFactory<Compression>>asList(
                new CompressionNone.Factory()));
        sshd.setMacFactories(Arrays.<NamedFactory<Mac>>asList(
                new HMACSHA256.Factory(),
                new HMACSHA512.Factory(),
                new HMACSHA1.Factory(),
                new HMACMD5.Factory(),
                new HMACSHA196.Factory(),
                new HMACMD596.Factory()));
        sshd.setChannelFactories(Arrays.<NamedFactory<Channel>>asList(
                new ChannelSession.Factory(),
                new TcpipServerChannel.DirectTcpipFactory()));
        sshd.setFileSystemFactory(new NativeFileSystemFactory());
        sshd.setTcpipForwarderFactory(new DefaultTcpipForwarderFactory());
        sshd.setGlobalRequestHandlers(Arrays.<RequestHandler<ConnectionService>>asList(
                new KeepAliveHandler(),
                new NoMoreSessionsHandler(),
                new TcpipForwardHandler(),
                new CancelTcpipForwardHandler()
        ));
        return sshd;
    }

    private static void setUpDefaultCiphers(SshServer sshd) {
        List<NamedFactory<Cipher>> avail = new LinkedList<NamedFactory<Cipher>>();
        avail.add(new AES128CTR.Factory());
        avail.add(new AES256CTR.Factory());
        avail.add(new ARCFOUR128.Factory());
        avail.add(new ARCFOUR256.Factory());
        avail.add(new AES192CBC.Factory());
        avail.add(new AES256CBC.Factory());

        for (Iterator<NamedFactory<Cipher>> i = avail.iterator(); i.hasNext(); ) {
            final NamedFactory<Cipher> f = i.next();
            try {
                final Cipher c = f.create();
                final byte[] key = new byte[c.getBlockSize()];
                final byte[] iv = new byte[c.getIVSize()];
                c.init(Cipher.Mode.Encrypt,
                       key,
                       iv);
            } catch (InvalidKeyException e) {
                i.remove();
            } catch (Exception e) {
                i.remove();
            }
        }
        sshd.setCipherFactories(avail);
    }

    private FileSystemAuthenticator fileSystemAuthenticator;
    private FileSystemAuthorizer fileSystemAuthorizer;
    private ExecutorService executorService;

    public void setup(final File certDir,
                      final InetSocketAddress inetSocketAddress,
                      final String sshIdleTimeout,
                      final String algorithm,
                      final ReceivePackFactory receivePackFactory,
                      final JGitFileSystemProvider.RepositoryResolverImpl<BaseGitCommand> repositoryResolver,
                      final ExecutorService executorService) {
        checkNotNull("certDir",
                     certDir);
        checkNotEmpty("sshIdleTimeout",
                      sshIdleTimeout);
        checkNotEmpty("algorithm",
                      algorithm);
        checkNotNull("receivePackFactory",
                     receivePackFactory);
        checkNotNull("repositoryResolver",
                     repositoryResolver);
        checkNotNull("executorService",
                     executorService);

        this.executorService = executorService;

        sshd.getProperties().put(SshServer.IDLE_TIMEOUT,
                                 sshIdleTimeout);

        if (inetSocketAddress != null) {
            sshd.setHost(inetSocketAddress.getHostName());
            sshd.setPort(inetSocketAddress.getPort());
        }

        if (!certDir.exists()) {
            certDir.mkdirs();
        }

        final AbstractGeneratorHostKeyProvider keyPairProvider = new SimpleGeneratorHostKeyProvider(new File(certDir,
                                                                                                             "hostkey.ser").getAbsolutePath());

        try {
            SecurityUtils.getKeyPairGenerator(algorithm);
            keyPairProvider.setAlgorithm(algorithm);
        } catch (final Exception ignore) {
            throw new RuntimeException(String.format("Can't use '%s' algorithm for ssh key pair generator.",
                                                     algorithm),
                                       ignore);
        }

        sshd.setKeyPairProvider(keyPairProvider);
        sshd.setCommandFactory(new CommandFactory() {
            @Override
            public Command createCommand(String command) {
                if (command.startsWith("git-upload-pack")) {
                    return new GitUploadCommand(command,
                                                repositoryResolver,
                                                getAuthorizationManager(),
                                                executorService);
                } else if (command.startsWith("git-receive-pack")) {
                    return new GitReceiveCommand(command,
                                                 repositoryResolver,
                                                 getAuthorizationManager(),
                                                 receivePackFactory,
                                                 executorService);
                } else {
                    return new UnknownCommand(command);
                }
            }
        });
        sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
            @Override
            public boolean authenticate(final String username,
                                        final String password,
                                        final ServerSession session) {
                FileSystemUser user = getUserPassAuthenticator().authenticate(username,
                                                                              password);
                if (user == null) {
                    return false;
                }
                session.setAttribute(BaseGitCommand.SUBJECT_KEY,
                                     user);
                return true;
            }
        });
    }

    public void stop() {
        try {
            sshd.stop(true);
        } catch (final InterruptedException ignored) {
        }
    }

    public void start() {
        try {
            sshd.start();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't start SSH daemon at " + sshd.getHost() + ":" + sshd.getPort(),
                                       e);
        }
    }

    public boolean isRunning() {
        return !(sshd.isClosed() || sshd.isClosing());
    }

    SshServer getSshServer() {
        return sshd;
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(sshd.getProperties());
    }

    public FileSystemAuthenticator getUserPassAuthenticator() {
        return fileSystemAuthenticator;
    }

    public void setUserPassAuthenticator(FileSystemAuthenticator fileSystemAuthenticator) {
        this.fileSystemAuthenticator = fileSystemAuthenticator;
    }

    public FileSystemAuthorizer getAuthorizationManager() {
        return fileSystemAuthorizer;
    }

    public void setAuthorizationManager(FileSystemAuthorizer fileSystemAuthorizer) {
        this.fileSystemAuthorizer = fileSystemAuthorizer;
    }
}
