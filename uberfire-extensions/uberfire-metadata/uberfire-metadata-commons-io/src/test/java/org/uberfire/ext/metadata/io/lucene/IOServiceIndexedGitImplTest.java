/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.metadata.io.lucene;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TermQuery;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

import static org.junit.Assert.*;
import static org.uberfire.ext.metadata.io.KObjectUtil.toKCluster;

@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMScript(value = "byteman/lucene.btm")
public class IOServiceIndexedGitImplTest extends BaseIndexTest {

    protected final Date dateValue = new Date();

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{this.getClass().getSimpleName()};
    }

    @Test
    public void testIndexedFile() throws IOException, InterruptedException {
        setupCountDown(2);
        final Path path1 = getBasePath(this.getClass().getSimpleName()).resolve("myIndexedFile.txt");
        ioService().write(path1,
                          "ooooo!",
                          Collections.<OpenOption>emptySet(),
                          new FileAttribute<Object>() {
                              @Override
                              public String name() {
                                  return "custom";
                              }

                              @Override
                              public Object value() {
                                  return dateValue;
                              }
                          },
                          new FileAttribute<String>() {
                              @Override
                              public String name() {
                                  return "int.hello";
                              }

                              @Override
                              public String value() {
                                  return "hello some world jhere";
                              }
                          },
                          new FileAttribute<Integer>() {
                              @Override
                              public String name() {
                                  return "int";
                              }

                              @Override
                              public Integer value() {
                                  return 10;
                              }
                          }
        );

        final Path path2 = getBasePath(this.getClass().getSimpleName()).resolve("myOtherIndexedFile.txt");
        ioService().write(path2,
                          "ooooo!",
                          Collections.<OpenOption>emptySet(),
                          new FileAttribute<String>() {
                              @Override
                              public String name() {
                                  return "int.hello";
                              }

                              @Override
                              public String value() {
                                  return "jhere";
                              }
                          });

        waitForCountDown(5000);

        assertNotNull(config.getMetaModelStore().getMetaObject(Path.class.getName()));

        assertNotNull(config.getMetaModelStore().getMetaObject(Path.class.getName()).getProperty("int").get());
        assertNotNull(config.getMetaModelStore().getMetaObject(Path.class.getName()).getProperty("int.hello").get());
        assertNotNull(config.getMetaModelStore().getMetaObject(Path.class.getName()).getProperty("custom").get());

        assertNotNull(config.getMetaModelStore().getMetaObject(Path.class.getName()).getProperty("int").get());
        assertNotNull(config.getMetaModelStore().getMetaObject(Path.class.getName()).getProperty("int.hello").get());
        assertNotNull(config.getMetaModelStore().getMetaObject(Path.class.getName()).getProperty("custom").get());

        assertEquals(1,
                     config.getMetaModelStore().getMetaObject(Path.class.getName()).getProperty("int").get().getTypes().size());
        assertEquals(1,
                     config.getMetaModelStore().getMetaObject(Path.class.getName()).getProperty("int.hello").get().getTypes().size());
        assertEquals(1,
                     config.getMetaModelStore().getMetaObject(Path.class.getName()).getProperty("custom").get().getTypes().size());

        assertTrue(config.getMetaModelStore().getMetaObject(Path.class.getName()).getProperty("int").get().getTypes().contains(Integer.class));
        assertTrue(config.getMetaModelStore().getMetaObject(Path.class.getName()).getProperty("int.hello").get().getTypes().contains(String.class));
        assertTrue(config.getMetaModelStore().getMetaObject(Path.class.getName()).getProperty("custom").get().getTypes().contains(Date.class));

        final String index = toKCluster(path2.getFileSystem()).getClusterId();

        {
            TermQuery query = new TermQuery(new Term("int.hello",
                                                     "world"));

            long hits = config.getIndexProvider().findHitsByQuery(Arrays.asList(index),
                                                                  query);

            assertEquals(1,
                         hits);
        }

        {

            TermQuery query = new TermQuery(new Term("int.hello",
                                                     "jhere"));

            long hits = config.getIndexProvider().findHitsByQuery(Arrays.asList(index),
                                                                  query);

            assertEquals(2,
                         hits);
        }

        {
            long hits = config.getIndexProvider().findHitsByQuery(Arrays.asList(index),
                                                                  new MatchAllDocsQuery());

            assertEquals(2,
                         hits);
        }
    }
}
