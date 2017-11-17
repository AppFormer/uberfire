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

package org.uberfire.ext.metadata.io.elasticsearch;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.provider.IndexProvider;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

import static org.junit.Assert.*;
import static org.uberfire.ext.metadata.io.KObjectUtil.toKCluster;

@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMScript(value = "byteman/elastic.btm")
public class IOServiceIndexedDotFileGitInternalImplTest extends BaseIndexTest {

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{getSimpleName()};
    }

    @Test
    public void testIndexedGitInternalDotFile() throws IOException, InterruptedException {
        setupCountDown(2);
        final Path path1 = getBasePath(getSimpleName()).resolve(".gitkeep");
        ioService().write(path1,
                          "ooooo!",
                          Collections.<OpenOption>emptySet(),
                          getFileAttributes());

        final Path path2 = getBasePath(getSimpleName()).resolve("afile");
        ioService().write(path2,
                          "ooooo!",
                          Collections.<OpenOption>emptySet(),
                          getFileAttributes());

        waitForCountDown(10000);

        List<String> indices = Arrays.asList(toKCluster(path1.getFileSystem()).getClusterId());
        IndexProvider provider = this.config.getIndexProvider();

        //Check the file has been indexed
        List<KObject> hits = provider.findByQuery(indices,
                                                  new TermQuery(new Term("name",
                                                                         "value")),
                                                  10);

        assertEquals(1,
                     hits.size());

        assertEquals(hits.get(0).getKey(),
                     path2.toUri().toString());
    }

    private FileAttribute<?>[] getFileAttributes() {
        return new FileAttribute<?>[]{
                new FileAttribute<String>() {
                    @Override
                    public String name() {
                        return "name";
                    }

                    @Override
                    public String value() {
                        return "value";
                    }
                }};
    }
}
