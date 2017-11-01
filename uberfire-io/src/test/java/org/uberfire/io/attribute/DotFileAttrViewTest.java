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

package org.uberfire.io.attribute;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

import static org.junit.Assert.*;

/**
 *
 */
public class DotFileAttrViewTest {

    protected static final List<File> tempFiles = new ArrayList<>();
    protected static IOService ioService = null;
    private static boolean created = false;

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        for (final File tempFile : tempFiles) {
            FileUtils.deleteQuietly(tempFile);
        }
    }

    public static File createTempDirectory()
            throws IOException {
        final File temp = File.createTempFile("temp",
                                              Long.toString(System.nanoTime()));
        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        tempFiles.add(temp);

        return temp;
    }

    @Test
    public void testDotFileAttrAccess() throws IOException {
        final URI newRepo = URI.create("git://" + new Date().getTime() + "-repo-test-3");
        ioService().newFileSystem(newRepo,
                                  new HashMap<>());

        final Path dir = ioService().get(newRepo);
        final Path file = dir.resolve("myFile.txt");

        ioService().write(file,
                          "mycontent",
                          Collections.<OpenOption>emptySet());

        {
            final DublinCoreView view = ioService().getFileAttributeView(file,
                                                                         DublinCoreView.class);

            assertNotNull(view);

            assertNotNull(view.readAttributes());

            assertNotNull(view.readAttributes().languages());

            assertEquals(0,
                         view.readAttributes().languages().size());
        }

        ioService().write(file,
                          "mycontent",
                          Collections.<OpenOption>emptySet(),
                          new FileAttribute<Object>() {
                              @Override
                              public String name() {
                                  return "dcore.creator";
                              }

                              @Override
                              public Object value() {
                                  return "some user name here";
                              }
                          },
                          new FileAttribute<Object>() {
                              @Override
                              public String name() {
                                  return "dcore.language[0]";
                              }

                              @Override
                              public Object value() {
                                  return "en";
                              }
                          },
                          new FileAttribute<Object>() {
                              @Override
                              public String name() {
                                  return "dcore.language[1]";
                              }

                              @Override
                              public Object value() {
                                  return "pt-BR";
                              }
                          }
        );

        {
            final DublinCoreView view = ioService().getFileAttributeView(file,
                                                                         DublinCoreView.class);

            assertNotNull(view);

            assertNotNull(view.readAttributes());

            assertNotNull(view.readAttributes().languages());

            assertEquals(2,
                         view.readAttributes().languages().size());

            assertTrue(view.readAttributes().languages().contains("pt-BR"));

            assertTrue(view.readAttributes().languages().contains("en"));

            assertEquals(1,
                         view.readAttributes().creators().size());

            assertTrue(view.readAttributes().creators().contains("some user name here"));
        }

        final Path dotFile = file.getParent().resolve(".myFile.txt");
        assertTrue(Files.exists(dotFile));

        final VersionAttributeView attrs = Files.getFileAttributeView(dotFile,
                                                                      VersionAttributeView.class);
        assertEquals(1,
                     attrs.readAttributes().history().records().size());

        final Map<String, Object> result = Files.readAttributes(dotFile,
                                                                "*");
        assertNotNull(result);
    }

    public IOService ioService() {
        if (ioService == null) {
            ioService = new IOServiceDotFileImpl();
            assertTrue(PriorityDisposableRegistry.getDisposables().contains(ioService));
        }
        return ioService;
    }

    @Before
    public void setup() throws IOException {
        if (!created) {
            final String path = createTempDirectory().getAbsolutePath();
            System.setProperty("org.uberfire.nio.git.dir",
                               path);
            System.out.println(".niogit: " + path);

            final URI newRepo = URI.create("git://repo-test-3");

            try {
                ioService().newFileSystem(newRepo,
                                          new HashMap<String, Object>());
            } catch (final Exception ex) {
            } finally {
                created = true;
            }
        }
    }

    @After
    public void tearDown() {
        // dispose the IOService or it will badly influence the tests executed after
        ioService.dispose();
    }
}
